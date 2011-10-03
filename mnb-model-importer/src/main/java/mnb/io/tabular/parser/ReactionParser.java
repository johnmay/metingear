
/**
 * ReactionParser.java
 *
 * 2011.08.31
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package mnb.io.tabular.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mnb.io.tabular.EntityResolver;
import mnb.io.tabular.preparse.PreparsedReaction;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Subsystem;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.core.Compartment;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.chemet.entities.reaction.Reaction;
import uk.ac.ebi.chemet.entities.reaction.Reversibility;
import uk.ac.ebi.chemet.entities.reaction.participant.Participant;
import uk.ac.ebi.core.reaction.MetaboliteParticipant;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.resource.classification.ECNumber;
import uk.ac.ebi.resource.reaction.BasicReactionIdentifier;


/**
 *          ReactionParser – 2011.08.31 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ReactionParser {

    private static final Logger LOGGER = Logger.getLogger(ReactionParser.class);
    // Reaction arrow matcher (note excess space is gobbeled up in split)
    private static final Pattern EQUATION_ARROW = Pattern.compile("(<[-=]+>)|(<[-=]*)|([-=]*>)");
    private static final Pattern EQUATION_ADDITION = Pattern.compile("\\s+[+]\\s+");
    private static final Pattern REACTION_COMPARTMENT =
                                 Pattern.compile("\\A\\[(\\w{1,2})\\]\\s*:");
    public static final Pattern COEFFICIENT_PATTERN =
                                Pattern.compile("\\((\\d+(?:.\\d+)?)\\)");
    public static final Pattern ENTITY_COMPARTMENT =
                                Pattern.compile("\\[(\\w{1,2})\\]");
    private static final Reversibility[] NORMALISED_ARROWS =
                                         new Reversibility[]{ Reversibility.REVERSIBLE,
                                                              Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT,
                                                              Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT };
    private EntityResolver entites;


    public ReactionParser(EntityResolver entityResolver) {
        entites = entityResolver;
    }


    // attrb include the entitysheet (if available)
    public MetabolicReaction parseReaction(PreparsedReaction reaction) throws UnparsableReactionError {

        String equation = reaction.getEquation();
        String[] rxnSides = getReactionSides(equation);

        // determine whether the reaction contains two sides or one.
        if( rxnSides.length == 2 ) {

            // standard reaction
            return parseTwoSidedReaction(reaction, rxnSides);

        } else if( rxnSides.length == 1 ) {


            // exchange reaction
            if( getReactionArrow(equation) == Reversibility.UNKNOWN ) {

                throw new UnparsableReactionError("Unparsable reaction arrow, rxn id" +
                                                  reaction.getIdentifier());
            }

            parseExchangeReaction(reaction, rxnSides[0]);
            return null;

        } else {

            throw new UnparsableReactionError("Equation is empty, rxn id: " +
                                              reaction.getIdentifier());
        }
    }

    /*
     *
     */

    private static int ticker = 0;


    public MetabolicReaction parseTwoSidedReaction(PreparsedReaction reaction,
                                          String[] equationSides) throws UnparsableReactionError {
        // todo
        Matcher reactionCompartment = REACTION_COMPARTMENT.matcher(equationSides[0]);
        equationSides[0] = reactionCompartment.replaceAll("");
        MetabolicReaction rxn = new MetabolicReaction();
        rxn.setIdentifier(new BasicReactionIdentifier("Rxn-" + ++ticker));
        for( MetaboliteParticipant p :
             parseParticipants(equationSides[0],
                               Compartment.CYTOPLASM) ) {
            rxn.addReactant(p);
        }
        for( MetaboliteParticipant p :
             parseParticipants(equationSides[1],
                               Compartment.CYTOPLASM) ) {
            rxn.addProduct(p);
        }

        rxn.setReversibility(Reversibility.REVERSIBLE);

        // add subsytem annotation
        String subsytem = reaction.getSubsystem();
        if( subsytem != null ) {
            rxn.addAnnotation(new Subsystem(subsytem));
        }

        // add classification
        String classification = reaction.getClassification();
        if( classification != null ) {
            // load EC code
            if( classification.matches("(?:\\d+\\.){3}\\d+") || classification.contains("EC") ) {
                System.out.println("loading ec: '" + classification + "'");
                rxn.addAnnotation(new EnzymeClassification(new ECNumber(classification)));
            }
        }



        return rxn;

    }


    /**
     * Only have left side (or some weird reaction operator)
     */
    public void parseExchangeReaction(PreparsedReaction reaction,
                                      String equationSide) {
    }


    public List<MetaboliteParticipant> parseParticipants(String equationSide,
                                               Compartment defaultCompartment) throws UnparsableReactionError {

        List<MetaboliteParticipant> parsedParticipants = new ArrayList();

        String[] participants = EQUATION_ADDITION.split(equationSide);
        for( String string : participants ) {
            parsedParticipants.add(parseParticipant(string, defaultCompartment));
        }

        return parsedParticipants;

    }


    public MetaboliteParticipant parseParticipant(final String participant,
                                        final Compartment defaultCompartment) throws UnparsableReactionError {

        String entityAbbr = participant;
        String entityAbbrComp = participant;
        Compartment compartment = defaultCompartment;
        Double coef = 1d;

        // stoichiometric coefficients
        Matcher coefMatcher = COEFFICIENT_PATTERN.matcher(entityAbbr);
        if( coefMatcher.find() ) {
            coef = Double.parseDouble(coefMatcher.group(1));
            entityAbbr = coefMatcher.replaceAll("");
            entityAbbrComp = entityAbbr;
        }

        // compartment
        Matcher compartmentMatcher = ENTITY_COMPARTMENT.matcher(entityAbbr);
        if( compartmentMatcher.find() ) {
            compartment = Compartment.getCompartment(compartmentMatcher.group(1));
            entityAbbr = compartmentMatcher.replaceAll("");
        }


        // try fetching with compartment attached and without
//        PreparsedMetabolite entity = entites.getEntity(entityAbbrComp.trim());
        Metabolite entity = entites.getReconciledMetabolite(entityAbbr.trim());

        if( entity != null ) {
            // System.out.println( coef + " " + entity.getName() + " " + compartment );
            return new MetaboliteParticipant(entity, coef,
                                                                    compartment);
        } else {
            System.out.println("Unable to find " + entityAbbrComp.trim() + " or " +
                               entityAbbr.trim() +
                               " in metabolite sheet");
            throw new UnparsableReactionError("...");
//            return new MetaboliteParticipant(entity, coef, compartment);

        }



        //TODO:
        // ?ion match
        // ?remove _ext part
        // pmf associated?
        // match name with + and without + and then with number and without numbers
        // e.g.
        // YGH+FHD, 1) YGH+FHD 2) YGH, FHD
        // 35FGD+1400DH 1) 35FGD+1400DH 2) 35FGD 1400DH 3) FGD, DH
    }


    public Participant[] parseReactionSide(String reactionSide) {
        return new Participant[0];
    }


    public String[] getReactionSides(String equation) {
        return EQUATION_ARROW.split(equation.trim());
    }


    public Reversibility getReactionArrow(String equation) {

        Matcher arrowMatcher = EQUATION_ARROW.matcher(equation);

        if( arrowMatcher.find() ) {

            for( int i = 0 ; i < NORMALISED_ARROWS.length ; i++ ) {
                if( arrowMatcher.group(i + 1) != null ) {
                    return NORMALISED_ARROWS[i];
                }
            }
        }

        return Reversibility.UNKNOWN;

    }


}

