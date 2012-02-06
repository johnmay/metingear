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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mnb.io.tabular.EntityResolver;
import mnb.io.tabular.preparse.PreparsedReaction;
import mnb.io.tabular.type.ReactionColumn;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.Locus;
import uk.ac.ebi.annotation.Subsystem;
import uk.ac.ebi.annotation.crossreference.Classification;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.core.CompartmentImplementation;
import uk.ac.ebi.core.MetabolicReaction;
import uk.ac.ebi.chemet.entities.reaction.Reversibility;
import uk.ac.ebi.chemet.entities.reaction.participant.Participant;
import uk.ac.ebi.interfaces.entities.Metabolite;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.core.reaction.MetabolicParticipant;
import uk.ac.ebi.resource.classification.ECNumber;
import uk.ac.ebi.resource.protein.BasicProteinIdentifier;
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

    public static final Pattern EQUATION_ARROW = Pattern.compile("(<[-=]+>)|(<[-=]*)|([-=]*>)");

    public static final Pattern EQUATION_ADDITION = Pattern.compile("\\s+[+]\\s+");

    private static final Pattern REACTION_COMPARTMENT =
                                 Pattern.compile("\\A\\[(\\w{1,2})\\]\\s*:");

    public static final Pattern DOUBLE_PATTERN =
                                Pattern.compile("([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?)");

    public static final Pattern COEFFICIENT_PATTERN =
                                Pattern.compile("\\A" + DOUBLE_PATTERN.pattern() + "\\s+" + "|\\(" + DOUBLE_PATTERN.pattern() + "\\)");

    public static final Pattern COMPARTMENT_PATTERN =
                                Pattern.compile("[\\(\\[](\\w{1,2})[\\)\\]]");

    private static final Reversibility[] NORMALISED_ARROWS =
                                         new Reversibility[]{Reversibility.REVERSIBLE,
                                                             Reversibility.IRREVERSIBLE_RIGHT_TO_LEFT,
                                                             Reversibility.IRREVERSIBLE_LEFT_TO_RIGHT};

    private EntityResolver entites;

    private Set<Report> messages = new HashSet();


    /**
     * Determines whether an item contains listed items.
     * separated by ';', '|' or ','
     * @param str
     */
    public boolean containsList(String str) {
        return str.contains("[|,;]") || str.contains("\\sand\\s|\\sor\\s|&");
    }


    public ReactionParser(EntityResolver entityResolver) {
        entites = entityResolver;
    }

    // attrb include the entitysheet (if available)

    public MetabolicReaction parseReaction(PreparsedReaction reaction) throws UnparsableReactionError {

        String equation = reaction.getEquation();
        String[] rxnSides = getReactionSides(equation);

        // determine whether the reaction contains two sides or one.
        if (rxnSides.length == 2) {

            // standard reaction
            return parseTwoSidedReaction(reaction, rxnSides);

        } else if (rxnSides.length == 1) {

            // exchange reaction
            return parseExchangeReaction(reaction, rxnSides[0]);

        } else {

            throw new UnparsableReactionError("No equation found for rxn: "
                                              + reaction.getIdentifier());
        }
    }

    /*
     *
     */
    private static int ticker = 0;


    public MetabolicReaction parseTwoSidedReaction(PreparsedReaction reaction,
                                                   String[] equationSides) throws UnparsableReactionError {

        Matcher reactionCompartment = REACTION_COMPARTMENT.matcher(equationSides[0]);

        MetabolicReaction rxn = new MetabolicReaction(new BasicReactionIdentifier("{rxn/" + ++ticker + "}"), null, null);
        rxn.setAbbreviation(reaction.hasValue(ReactionColumn.ABBREVIATION) ? reaction.getIdentifier() : "");
        rxn.setName(reaction.hasValue(ReactionColumn.DESCRIPTION) ? reaction.getDescription() : "");

        CompartmentImplementation defaultCompartment = CompartmentImplementation.CYTOPLASM;

        if (reactionCompartment.find()) {
            defaultCompartment = CompartmentImplementation.getCompartment(reactionCompartment.group(1));
            equationSides[0] = reactionCompartment.replaceAll("");
        }

        for (MetabolicParticipant p :
             parseParticipants(equationSides[0],
                               defaultCompartment,
                               reaction)) {
            rxn.addReactant(p);
        }
        for (MetabolicParticipant p :
             parseParticipants(equationSides[1],
                               defaultCompartment,
                               reaction)) {
            rxn.addProduct(p);
        }

        rxn.setReversibility(getReactionArrow(reaction.getEquation()));

        // add subsytem annotation
        String subsytem = reaction.getSubsystem();
        if (subsytem != null && subsytem.isEmpty() == false) {
            rxn.addAnnotation(new Subsystem(subsytem));
        }


        // add classification
        for (String classification : reaction.getClassifications()) {
            // load EC code
            if (classification.matches("(?:\\d+\\.){3}\\d+") || classification.contains("EC")) {
                rxn.addAnnotation(new EnzymeClassification(new ECNumber(classification)));
            } else if (classification.contains("TC")) {
                rxn.addAnnotation(new Classification(new BasicProteinIdentifier(classification)));
            }
        }

        for (String locus : reaction.getLoci()) {
            rxn.addAnnotation(new Locus(locus));
        }



        return rxn;

    }


    /**
     * Only have left side (or some weird reaction operator)
     */
    public MetabolicReaction parseExchangeReaction(PreparsedReaction reaction,
                                                   String equationSide) throws UnparsableReactionError {
        Matcher reactionCompartment = REACTION_COMPARTMENT.matcher(equationSide);

        MetabolicReaction rxn = new MetabolicReaction(new BasicReactionIdentifier("{rxn/" + ++ticker + "}"), null, null);
        rxn.setAbbreviation(reaction.hasValue(ReactionColumn.ABBREVIATION) ? reaction.getIdentifier() : "");
        rxn.setName(reaction.hasValue(ReactionColumn.DESCRIPTION) ? reaction.getDescription() : "");

        CompartmentImplementation defaultCompartment = CompartmentImplementation.CYTOPLASM;

        if (reactionCompartment.find()) {
            defaultCompartment = CompartmentImplementation.getCompartment(reactionCompartment.group(1));
            equationSide = reactionCompartment.replaceAll("");
        }

        for (MetabolicParticipant p :
             parseParticipants(equationSide,
                               defaultCompartment,
                               reaction)) {
            rxn.addReactant(p);
        }


        rxn.setReversibility(getReactionArrow(reaction.getEquation()));

        // add subsytem annotation
        String subsytem = reaction.getSubsystem();
        if (subsytem != null && subsytem.isEmpty() == false) {
            rxn.addAnnotation(new Subsystem(subsytem));
        }


        // add classification
        for (String classification : reaction.getClassifications()) {
            // load EC code
            if (classification.matches("(?:\\d+\\.){3}\\d+") || classification.contains("EC")) {
                rxn.addAnnotation(new EnzymeClassification(new ECNumber(classification)));
            } else if (classification.contains("TC")) {
                rxn.addAnnotation(new Classification(new BasicProteinIdentifier(classification)));
            }
        }

        for (String locus : reaction.getLoci()) {
            rxn.addAnnotation(new Locus(locus));
        }



        return rxn;
    }


    public List<MetabolicParticipant> parseParticipants(String equationSide,
                                                        CompartmentImplementation defaultCompartment,
                                                        PreparsedReaction reaction) throws UnparsableReactionError {

        List<MetabolicParticipant> parsedParticipants = new ArrayList();

        String[] participants = EQUATION_ADDITION.split(equationSide);
        for (String string : participants) {
            parsedParticipants.add(parseParticipant(string, defaultCompartment, reaction));
        }

        return parsedParticipants;

    }


    public Collection<Report> collectMessages() {
        Set collected = new HashSet();
        collected.addAll(messages);
        messages.clear();
        return collected;
    }


    public MetabolicParticipant parseParticipant(final String participant,
                                                 final CompartmentImplementation defaultCompartment,
                                                 final PreparsedReaction rxn) throws UnparsableReactionError {

        String entityAbbr = participant.trim();
        String entityAbbrComp = entityAbbr;
        CompartmentImplementation compartment = defaultCompartment;
        Double coef = 1d;

        // stoichiometric coefficients
        Matcher coefMatcher = COEFFICIENT_PATTERN.matcher(entityAbbr);
        if (coefMatcher.find()) {
            coef = Double.parseDouble(coefMatcher.group(1) == null ? coefMatcher.group(2) : coefMatcher.group(1));
            entityAbbr = coefMatcher.replaceAll("");
            entityAbbrComp = entityAbbr;
        }

        // compartment
        Matcher compartmentMatcher = COMPARTMENT_PATTERN.matcher(entityAbbr);
        if (compartmentMatcher.find()) {
            compartment = CompartmentImplementation.getCompartment(compartmentMatcher.group(1));
            entityAbbr = compartmentMatcher.replaceAll("");
        }


        // try fetching with compartment attached and without
//        PreparsedMetabolite entity = entites.getEntity(entityAbbrComp.trim());
        Metabolite entity = entites.getReconciledMetabolite(entityAbbr.trim());

        if (entity != null) {
            // System.out.println( coef + " " + entity.getName() + " " + compartment );
            return new MetabolicParticipant(entity,
                                            coef,
                                            compartment);
        } else {
            messages.add(new WarningMessage("The metabolite "
                                            + entityAbbr.trim()
                                            + " was not found in the metabolite sheet for reaction " + rxn));
            entity = entites.getNonReconciledMetabolite(entityAbbr);
            return new MetabolicParticipant(entity,
                                            coef,
                                            compartment);
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


    public static Reversibility getReactionArrow(String equation) {

        Matcher arrowMatcher = EQUATION_ARROW.matcher(equation);

        if (arrowMatcher.find()) {

            for (int i = 0; i < NORMALISED_ARROWS.length; i++) {
                if (arrowMatcher.group(i + 1) != null) {
                    return NORMALISED_ARROWS[i];
                }
            }
        }

        return Reversibility.UNKNOWN;

    }
}
