package uk.ac.ebi.mnb.edit;

import uk.ac.ebi.edit.entity.EntityEdit;
import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.Chromosome;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.edit.entity.RemoveGeneEdit;
import uk.ac.ebi.metingear.edit.entity.RemoveGeneProduct;
import uk.ac.ebi.metingear.edit.entity.RemoveMetaboliteEdit;
import uk.ac.ebi.metingear.edit.entity.RemoveReactionEdit;

import javax.swing.undo.CompoundEdit;
import java.util.HashSet;
import java.util.Map;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class DeleteEntitiesEdit extends CompoundEdit implements EntityEdit {

    private EntityCollection collection;
    private Reconstruction recon;
    private Map<Gene, Chromosome> geneMap;

    public DeleteEntitiesEdit(Reconstruction recon, EntityCollection collection) {

        this.recon = recon;
        this.collection = collection;

        for (Metabolite m : new HashSet<Metabolite>(collection.get(Metabolite.class))) {
            super.addEdit(new RemoveMetaboliteEdit(recon, m));
        }
        for (MetabolicReaction r : new HashSet<MetabolicReaction>(collection.get(MetabolicReaction.class))) {
            super.addEdit(new RemoveReactionEdit(recon, r));
        }
        for (GeneProduct p : collection.getGeneProducts()){
            super.addEdit(new RemoveGeneProduct(recon, p));
        }
        for (Gene g : new HashSet<Gene>(collection.get(Gene.class))) {
            super.addEdit(new RemoveGeneEdit(g));
        }

        super.end();

    }

    @Override
    public EntityCollection getEntities() {
        return collection;
    }

    //    @Override
//    public void undo() throws CannotUndoException {
//
//        for (GeneProduct gp : collection.get(GeneProduct.class)) {
//            recon.addProduct(gp);
//        }
//
//        for (Metabolite m : collection.get(Metabolite.class)) {
//            recon.getMetabolome().add(m);
//        }
//
//        for (MetabolicReaction rxn : collection.get(MetabolicReaction.class)) {
//            recon.addReaction(rxn);
//            // remove reference map
//        }
//        recon.getReactome().rebuildMaps();
//
//        // might not work??
//        for (Gene gene : collection.get(Gene.class)) {
//            geneMap.get(gene).add(gene);
//        }
//
//    }
//
//
//    @Override
//    public void redo() throws CannotUndoException {
//
//        for (GeneProduct gp : collection.get(GeneProduct.class)) {
//            recon.getProducts().remove(gp);
//        }
//
//        for (Metabolite m : collection.get(Metabolite.class)) {
//            recon.getMetabolome().remove(m);
//        }
//
//        // remove reactions
//        for (MetabolicReaction rxn : collection.get(MetabolicReaction.class)) {
//            recon.getReactome().remove(rxn);
//        }
//        recon.getReactome().rebuildMaps();
//
//        // might not work??
//        for (Gene gene : collection.get(Gene.class)) {
//            geneMap.put(gene, gene.getChromosome());
//            gene.getChromosome().remove(gene);
//        }
//
//    }

}
