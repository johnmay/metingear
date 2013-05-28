/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metingear.util;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.RNASequence;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.ProteinProduct;
import uk.ac.ebi.mdk.domain.entity.RibosomalRNA;
import uk.ac.ebi.mdk.domain.entity.TransferRNA;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicProteinIdentifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicRNAIdentifier;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicReactionIdentifier;

/**
 * Duplicate items - can be removed if we go immutable.
 *
 * @author John May
 */
class Duplicator {

    private static final EntityFactory entities = DefaultEntityFactory.getInstance();

    private Duplicator() {
    }

    /**
     * Shallow copy of the provided entity.
     *
     * @param entity source entity
     *
     * @return shallow copy
     *
     * @throws IllegalArgumentException type of entity is not yet supported
     */
    static AnnotatedEntity shallow(final AnnotatedEntity entity) {
        if (entity instanceof Metabolite) {
            return shallow((Metabolite) entity);
        } else if (entity instanceof MetabolicReaction) {
            return shallow((MetabolicReaction) entity);
        } else if (entity instanceof GeneProduct) {
            return shallow((GeneProduct) entity);
        }
        throw new IllegalArgumentException(
            "Copy of " + entity.getClass() + " not yet supported");
    }

    /**
     * Shallow copy of the provided metabolite, the new metabolite will have the
     * same annotations, name and abbreviation but a different UUID and
     * Identifier.
     *
     * @param src source metabolite
     *
     * @return shallow copy
     */
    static Metabolite shallow(final Metabolite src) {
        Metabolite dest = entities.metabolite();
        dest.setIdentifier(BasicChemicalIdentifier.nextIdentifier());
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.addAnnotations(src.getAnnotations());
        return dest;
    }

    /**
     * Shallow copy of the provided reaction, the new reaction will have a
     * different UUID and Identifier and participant holders but everything else
     * should remain unchanged.
     *
     * @param src source reaction
     *
     * @return shallow copy
     */
    static MetabolicReaction shallow(final MetabolicReaction src) {
        MetabolicReaction dest = entities.reaction();
        dest.setIdentifier(BasicReactionIdentifier.nextIdentifier());
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.addAnnotations(src.getAnnotations());

        for (MetabolicParticipant p : src.getReactants()) {
            MetabolicParticipant r = entities.newInstance(
                MetabolicParticipant.class);
            r.setMolecule(p.getMolecule());
            r.setCoefficient(p.getCoefficient());
            r.setCompartment(p.getCompartment());
            dest.addReactant(r);
        }
        for (MetabolicParticipant p : src.getProducts()) {
            MetabolicParticipant r = entities.newInstance(
                MetabolicParticipant.class);
            r.setMolecule(p.getMolecule());
            r.setCoefficient(p.getCoefficient());
            r.setCompartment(p.getCompartment());
            dest.addProduct(r);
        }
        return dest;
    }

    static GeneProduct shallow(final GeneProduct src) {
        if (src instanceof ProteinProduct) {
            return shallow((ProteinProduct) src);
        } else if (src instanceof RibosomalRNA) {
            return shallow((RibosomalRNA) src);
        } else if (src instanceof TransferRNA) {
            return shallow((TransferRNA) src);
        }
        throw new IllegalArgumentException("unknown GeneProduct type");
    }

    /**
     * Shallow copy of the provided protein, the new protein will have the same
     * annotations, name and abbreviation but a different UUID and Identifier.
     *
     * @param src source protein
     *
     * @return shallow copy
     */
    static ProteinProduct shallow(final ProteinProduct src) {
        ProteinProduct dest = entities.protein();
        dest.setIdentifier(BasicProteinIdentifier.nextIdentifier());
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.addAnnotations(src.getAnnotations());
        for (ProteinSequence s : src.getSequences()) {
            dest.addSequence(s);
        }
        return dest;
    }

    /**
     * Shallow copy of the provided RNA, the new RNA will have the same
     * annotations, name and abbreviation but a different UUID and Identifier.
     *
     * @param src source RNA
     *
     * @return shallow copy
     */
    static RibosomalRNA shallow(final RibosomalRNA src) {
        RibosomalRNA dest = entities.rRNA();
        dest.setIdentifier(BasicRNAIdentifier.nextIdentifier());
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.addAnnotations(src.getAnnotations());
        for (RNASequence s : src.getSequences()) {
            dest.addSequence(s);
        }
        return dest;
    }

    /**
     * Shallow copy of the provided RNA, the new RNA will have the same
     * annotations, name and abbreviation but a different UUID and Identifier.
     *
     * @param src source RNA
     *
     * @return shallow copy
     */
    static TransferRNA shallow(final TransferRNA src) {
        TransferRNA dest = entities.tRNA();
        dest.setIdentifier(BasicRNAIdentifier.nextIdentifier());
        dest.setName(src.getName());
        dest.setAbbreviation(src.getAbbreviation());
        dest.addAnnotations(src.getAnnotations());
        for (RNASequence s : src.getSequences()) {
            dest.addSequence(s);
        }
        return dest;
    }
}
