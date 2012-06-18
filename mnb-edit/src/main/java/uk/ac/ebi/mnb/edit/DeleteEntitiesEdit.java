package uk.ac.ebi.mnb.edit;

import uk.ac.ebi.mdk.domain.entity.Gene;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.Chromosome;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import java.util.Map;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class DeleteEntitiesEdit extends AbstractUndoableEdit {

    private EntityCollection collection;
    private Reconstruction recon;
    private Map<Gene,Chromosome> geneMap;
    
    public DeleteEntitiesEdit(Reconstruction recon, EntityCollection collection){
        this.recon      = recon;
        this.collection = collection;
    }


    @Override
    public void undo() throws CannotUndoException {


        for(GeneProduct gp : collection.get(GeneProduct.class)){
            recon.addProduct(gp);
        }

        for(Metabolite m : collection.get(Metabolite.class)){
            recon.getMetabolome().add(m);
        }

        for(MetabolicReaction rxn : collection.get(MetabolicReaction.class)){
            recon.addReaction(rxn);
        }

        // might not work??
        for(Gene gene : collection.get(Gene.class)){
            geneMap.get(gene).add(gene);
        }

        System.out.println(recon.getMetabolome().size());

    }


    @Override
    public void redo() throws CannotUndoException {

        for(GeneProduct gp : collection.get(GeneProduct.class)){
            recon.getProducts().remove(gp);
        }

        for(Metabolite m : collection.get(Metabolite.class)){
            System.out.println(m);
            recon.getMetabolome().remove(m);
        }

        for(MetabolicReaction rxn : collection.get(MetabolicReaction.class)){
            recon.getReactome().remove(rxn);
        }

        // might not work??
        for(Gene gene : collection.get(Gene.class)){
            geneMap.put(gene,gene.getChromosome());
            gene.getChromosome().remove(gene);
        }

    }

}
