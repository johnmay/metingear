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

import mnb.io.tabular.EntityResolver;
import mnb.io.tabular.preparse.PreparsedReaction;
import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.Locus;
import uk.ac.ebi.mdk.domain.annotation.Subsystem;
import uk.ac.ebi.mdk.domain.annotation.crossreference.Classification;
import uk.ac.ebi.mdk.domain.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.mdk.domain.annotation.FluxLowerBound;
import uk.ac.ebi.mdk.domain.annotation.GibbsEnergy;
import uk.ac.ebi.caf.report.Report;
import uk.ac.ebi.mdk.domain.entity.reaction.ParticipantImplementation;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicReactionIdentifier;
import uk.ac.ebi.mdk.domain.identifier.classification.ECNumber;
import uk.ac.ebi.mdk.domain.identifier.classification.TransportClassificationNumber;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipantImplementation;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.Direction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.tool.CompartmentResolver;
import uk.ac.ebi.mnb.core.WarningMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mnb.io.tabular.type.ReactionColumn.*;


/**
 * ReactionParser – 2011.08.31 <br>
 * Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
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

    private static final Direction[] NORMALISED_ARROWS =
            new Direction[]{Direction.BIDIRECTIONAL,
                    Direction.BACKWARD,
                    Direction.FORWARD};

    private EntityResolver entites;
    private EntityFactory factory = DefaultEntityFactory.getInstance();
    private CompartmentResolver resolver;

    private Set<Report> messages = new HashSet();


    /**
     * Determines whether an item contains listed items.
     * separated by ';', '|' or ','
     *
     * @param str
     */
    public boolean containsList(String str) {
        return str.contains("[|,;]") || str.contains("\\sand\\s|\\sor\\s|&");
    }


    public ReactionParser(EntityResolver entityResolver,CompartmentResolver resolver ) {
        entites = entityResolver;
        this.resolver = resolver;
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


    public MetabolicReaction parseTwoSidedReaction(PreparsedReaction preparsed,
                                                   String[] equationSides) throws UnparsableReactionError {

        Matcher reactionCompartment = REACTION_COMPARTMENT.matcher(equationSides[0]);

        MetabolicReaction rxn = getReaction(preparsed);

        Compartment defaultCompartment = Organelle.CYTOPLASM;

        if (reactionCompartment.find()) {
            defaultCompartment = resolver.getCompartment(reactionCompartment.group(1));
            equationSides[0] = reactionCompartment.replaceAll("");
        }

        for (MetabolicParticipantImplementation p :
                parseParticipants(equationSides[0],
                                  defaultCompartment,
                                  preparsed)) {
            rxn.addReactant(p);
        }
        for (MetabolicParticipantImplementation p :
                parseParticipants(equationSides[1],
                                  defaultCompartment,
                                  preparsed)) {
            rxn.addProduct(p);
        }


        return rxn;

    }


    /**
     * Only have left side (or some weird reaction operator)
     */

    public MetabolicReaction parseExchangeReaction(PreparsedReaction reaction,
                                                   String equationSide) throws UnparsableReactionError {
        Matcher reactionCompartment = REACTION_COMPARTMENT.matcher(equationSide);

        MetabolicReaction rxn = getReaction(reaction);

        Compartment defaultCompartment = Organelle.CYTOPLASM;

        if (reactionCompartment.find()) {
            defaultCompartment = resolver.getCompartment(reactionCompartment.group(1));
            equationSide = reactionCompartment.replaceAll("");
        }

        for (MetabolicParticipantImplementation p :
                parseParticipants(equationSide,
                                  defaultCompartment,
                                  reaction)) {
            rxn.addReactant(p);
        }

        return rxn;
    }


    public MetabolicReaction getReaction(PreparsedReaction preparsed) {

        MetabolicReaction rxn = factory.ofClass(MetabolicReaction.class, BasicReactionIdentifier.nextIdentifier(),
                                                preparsed.hasValue(DESCRIPTION) ? preparsed.getValue(DESCRIPTION) : "",
                                                preparsed.hasValue(ABBREVIATION) ? preparsed.getValue(ABBREVIATION) : "");

        if (preparsed.hasValue(DIRECTION)) {
            rxn.setDirection(getReactionArrow(preparsed.getValue(DIRECTION)));
        } else {
            rxn.setDirection(getReactionArrow(preparsed.getEquation()));
        }

        // add subsytem annotation
        if (preparsed.hasValue(SUBSYSTEM)) {
            rxn.addAnnotation(new Subsystem(preparsed.getSubsystem()));
        }


        // add classification
        for (String classification : preparsed.getClassifications()) {
            // load EC code
            if (classification.matches("(?:\\d+\\.){3}\\d+") || classification.contains("EC")) {
                rxn.addAnnotation(new EnzymeClassification(new ECNumber(classification)));
            } else if (classification.contains("TC")) {
                rxn.addAnnotation(new Classification(new TransportClassificationNumber(classification)));
            }
        }

        // add loci
        for (String locus : preparsed.getLoci()) {
            rxn.addAnnotation(new Locus(locus));
        }

        // add delta g and delta g error
        if (preparsed.hasValue(FREE_ENERGY)) {
            try {
                if (preparsed.hasValue(FREE_ENERGY_ERROR)) {
                    rxn.addAnnotation(new GibbsEnergy(Double.parseDouble(preparsed.getValue(FREE_ENERGY)),
                                                      Double.parseDouble(preparsed.getValue(FREE_ENERGY_ERROR))));
                } else {
                    rxn.addAnnotation(new GibbsEnergy(Double.parseDouble(preparsed.getValue(FREE_ENERGY)), 0d));
                }
            } catch (NumberFormatException ex) {
                LOGGER.error("Gibbs energy column(s) contained invalid value (not a double)");
            }
        }

        if (preparsed.hasValue(MIN_FLUX)) {
            try {
                rxn.addAnnotation(new FluxLowerBound(Double.parseDouble(preparsed.getValue(MIN_FLUX))));
            } catch (NumberFormatException ex) {
                LOGGER.error("Min flux column contained invalid value (not a double): " + preparsed.getValue(MIN_FLUX));
            }
        }

        if (preparsed.hasValue(MAX_FLUX)) {
            try {
                rxn.addAnnotation(new FluxLowerBound(Double.parseDouble(preparsed.getValue(MAX_FLUX))));
            } catch (NumberFormatException ex) {
                LOGGER.error("Max flux column contained invalid value (not a double): " + preparsed.getValue(MAX_FLUX));
            }
        }

        return rxn;

    }

    public List<MetabolicParticipantImplementation> parseParticipants(String equationSide,
                                                                      Compartment defaultCompartment,
                                                                      PreparsedReaction reaction) throws UnparsableReactionError {

        List<MetabolicParticipantImplementation> parsedParticipants = new ArrayList();

        String[] participants = EQUATION_ADDITION.split(equationSide);
        for (String participant : participants) {
            if (!participant.trim().isEmpty()) {
                parsedParticipants.add(parseParticipant(participant, defaultCompartment, reaction));
            }
        }

        return parsedParticipants;

    }


    public Collection<Report> collectMessages() {
        Set collected = new HashSet();
        collected.addAll(messages);
        messages.clear();
        return collected;
    }


    public MetabolicParticipantImplementation parseParticipant(final String participant,
                                                               final Compartment defaultCompartment,
                                                               final PreparsedReaction rxn) throws UnparsableReactionError {

        String entityAbbr = participant.trim();
        String entityAbbrComp = entityAbbr;
        Compartment compartment = defaultCompartment;
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
            compartment = resolver.getCompartment(compartmentMatcher.group(1));
            entityAbbr = compartmentMatcher.replaceAll("");
        }


        // try fetching with compartment attached and without
        //        PreparsedMetabolite entity = entites.getEntity(entityAbbrComp.trim());
        Metabolite entity = entites.getReconciledMetabolite(entityAbbr.trim());

        if (entity != null) {
            // System.out.println( coef + " " + entity.getName() + " " + compartment );
            return new MetabolicParticipantImplementation(entity,
                                                          coef,
                                                          (Compartment) compartment);
        } else {
            messages.add(new WarningMessage("The metabolite "
                                                    + entityAbbr.trim()
                                                    + " was not found in the metabolite sheet for reaction " + rxn));
            entity = entites.getNonReconciledMetabolite(entityAbbr);
            return new MetabolicParticipantImplementation(entity,
                                                          coef,
                                                          (Compartment) compartment);
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


    public ParticipantImplementation[] parseReactionSide(String reactionSide) {
        return new ParticipantImplementation[0];
    }


    public String[] getReactionSides(String equation) {
        return equation.isEmpty() ? new String[0] : EQUATION_ARROW.split(equation.trim());
    }


    public static Direction getReactionArrow(String equation) {

        Matcher arrowMatcher = EQUATION_ARROW.matcher(equation);

        if (arrowMatcher.find()) {

            for (int i = 0; i < NORMALISED_ARROWS.length; i++) {
                if (arrowMatcher.group(i + 1) != null) {
                    return NORMALISED_ARROWS[i];
                }
            }
        }

        return Direction.UNKNOWN;

    }
}
