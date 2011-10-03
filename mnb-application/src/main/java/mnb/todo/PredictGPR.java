
package mnb.todo;

/*
 *     This file is part of Metabolic Network Builder
 *
 *     Metabolic Network Builder is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.ebi.core.ReconstructionManager;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.crossreference.EnzymeClassification;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.metabolomes.core.gene.GeneProductCollection;
import uk.ac.ebi.metabolomes.core.gene.GeneProteinProduct;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.interfaces.Identifier;
import uk.ac.ebi.resource.classification.ECNumber;
import uk.ac.ebi.resource.protein.UniProtIdentifier;
import uk.ac.ebi.metabolomes.io.flatfile.IntEnzXML;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParameters;
import uk.ac.ebi.metabolomes.descriptor.observation.ObservationCollection;
import uk.ac.ebi.metabolomes.descriptor.observation.sequence.homology.BlastHit;
import uk.ac.ebi.metabolomes.core.reaction.BiochemicalReaction;
import uk.ac.ebi.metabolomes.descriptor.observation.BlastParamType;
import uk.ac.ebi.metabolomes.run.RunnableTask;
import uk.ac.ebi.metabolomes.run.TaskStatus;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.resource.protein.BasicProteinIdentifier;
import uk.ac.ebi.warehouse.exceptions.UnknownStructureException;
import uk.ac.ebi.warehouse.util.ReactionLoader;


/**
 * PredictGPR.java
 *
 *
 * @author johnmay
 * @date May 16, 2011
 */
public class PredictGPR
  extends RunnableTask {

    private static final org.apache.log4j.Logger LOGGER =
                                                 org.apache.log4j.Logger.getLogger(PredictGPR.class);
    private GeneProductCollection productCollection;
    private Reconstruction project;


    public PredictGPR(JobParameters param,
                      GeneProductCollection products) {
        super(param);
        this.productCollection = products;
        // can change to get this from the gene products in future
        project = ReconstructionManager.getInstance().getActiveReconstruction();
    }


    @Override
    public void prerun() {

        if( this.productCollection == null ) {
            setErrorStatus();
            return;
        }

        GeneProteinProduct[] proteinProducts = this.productCollection.getProteinProducts();
        if( proteinProducts == null || proteinProducts.length == 0 ) {
            setErrorStatus();
            return;
        }



    }


    @Override
    public void run() {


        if( getStatus() == TaskStatus.ERROR ) {
            return;
        }

        setRunningStatus();

        // takes aprx. 2 seconds to load
        IntEnzXML iex = IntEnzXML.getLoadedInstance();

        ReactionLoader reactionHelper = ReactionLoader.getInstance();
        HashMap<ECNumber, Reaction> ecToReactionMap = new HashMap<ECNumber, Reaction>();
        Double pcthreshold = getJobParameters().containsKey(BlastParamType.POSITIVE_COVERAGE) ?
                             (Double) getJobParameters().get(BlastParamType.POSITIVE_COVERAGE) : 0.4;

        // filter, cluster and fetch reactions from BioWarehouse
        GeneProteinProduct[] proteinProducts = this.productCollection.getProteinProducts();
        LOGGER.info("Predicting GRP for " + proteinProducts.length + " protein products");
        for( int i = 0 ; i < proteinProducts.length ; i++ ) {
            GeneProteinProduct product = proteinProducts[i];

            HashMap<ECNumber, List<BlastHit>> ecToObservationMap =
                                              new HashMap<ECNumber, List<BlastHit>>(5);

            // filter blast hits
            List<BlastHit> hits = product.getObservations().getBlastHits();
            LOGGER.info("product has " + hits.size() + " assigned");

            for( BlastHit blastHit : hits ) {
                // only take those that fall above the positive threshold
                Double positiveCoverage = (double) blastHit.getPositive() / (double) blastHit.
                  getHitLength();
                UniProtIdentifier id = blastHit.getUniProtIdentifier();

                LOGGER.info("Checking hit " + id);

                if( positiveCoverage > pcthreshold &&
                    id != null ) {

                    List<ECNumber> ecs = iex.getECNumbers(id);
                    for( ECNumber ec : ecs ) {
                        if( ecToObservationMap.containsKey(ec) == Boolean.FALSE ) {
                            ecToObservationMap.put(ec, new ArrayList<BlastHit>(10));
                        }
                        ecToObservationMap.get(ec).add(blastHit);
                    }
                }
            }

            // assign EC
            List<ECNumber> tentativeECs = new ArrayList<ECNumber>(ecToObservationMap.keySet());

            // only one ec
            if( tentativeECs.size() == 1 ) {
                ECNumber ec = tentativeECs.get(0);
                ObservationCollection oc = new ObservationCollection();
                oc.addAll(ecToObservationMap.get(ec));
                EnzymeClassification annotation = new EnzymeClassification(ec);

//                if( oc.size() > 5 ) {
//                    // green flag
//                    annotation.setFlag(AnnotationFlag.GREEN);
//                } else if( oc.size() <= 5 ) {
//                    // orange flag
//                    annotation.setFlag(AnnotationFlag.AMBER);
//                } else {
//                    // should never happen
//                    LOGGER.error("could not assign ec: " + ec + " with " + oc.size() +
//                                 " observations");
//                    annotation.setFlag(AnnotationFlag.RED);
//                }
                product.addAnnotation(annotation);
            }
            // multiple ecs
            if( tentativeECs.size() > 1 ) {
                for( ECNumber ec : tentativeECs ) {
                    ObservationCollection oc = new ObservationCollection();
                    oc.addAll(ecToObservationMap.get(ec));
                    EnzymeClassification annotation = new EnzymeClassification(ec);
                    // annotation.setFlag(AnnotationFlag.RED);
                    product.addAnnotation(annotation);
                }
            }

            // fetch reaction
            if( product.getAnnotations(EnzymeClassification.class).size() == 1 ) {
                EnzymeClassification ecAnnotation =
                                     new ArrayList<EnzymeClassification>(
                  product.getAnnotations(EnzymeClassification.class)).get(0);
                ECNumber ec = (ECNumber) ecAnnotation.getIdentifier();
                Map<IMolecule, Metabolite> pool = new HashMap<IMolecule, Metabolite>();
                if( ecToReactionMap.containsKey(ec) ) {
                    // this reaction is a repeat so add the one we have stored
                  //todo  product.addReaction(ecToReactionMap.get(ec));
                } else {
                    try {
                        long start = System.currentTimeMillis();
                        // only do this for one reaction

                        BiochemicalReaction reaction =
                                            reactionHelper.getBiochemicalReaction(ecAnnotation.
                          getIdentifier());
                        long end = System.currentTimeMillis();
                        LOGGER.info("Time to fetch from bwh: " + (end - start) + " (ms)");
                        if( reaction != null ) {
                            ecToReactionMap.put((ECNumber) ecAnnotation.getIdentifier(), reaction);
                            // make cross-links to product and project
                           //todo  product.addReaction(reaction);
                           //todo  project.addReaction(reaction);

                            // add all to the project
                            IMoleculeSet productSet = reaction.getProducts();
                            int nProducts = reaction.getProductCount();
                            for( int pi = 0 ; pi < nProducts ; pi++ ) {
                                IMolecule mol = productSet.getMolecule(pi);
                                if( pool.containsKey(mol) == false ) {
                                    Metabolite ent = new Metabolite();
                                    ent.setIdentifier(generateId(mol.getID()));
                                    ent.setName(mol.getID());
                                    ent.addAnnotation(new ChemicalStructure(mol));
                                    pool.put(mol, ent);
                                }
                                project.addMetabolite(pool.get(mol));
                            }
                            IMoleculeSet reactantSet = reaction.getReactants();
                            int nReactants = reaction.getReactantCount();
                            for( int ri = 0 ; ri < nReactants ; ri++ ) {

                                IMolecule mol = productSet.getMolecule(ri);
                                if( pool.containsKey(mol) == false ) {
                                    Metabolite ent = new Metabolite();
                                    ent.setIdentifier(generateId(mol.getID()));
                                    ent.setName(mol.getID());
                                    ent.addAnnotation(new ChemicalStructure(mol));
                                    pool.put(mol, ent);
                                }
                                project.addMetabolite(pool.get(mol));
                            }
                        }
                    } catch( CDKException ex ) {
                        ex.printStackTrace();
                    } catch( SQLException ex ) {
                        LOGGER.error("SQL Exceptiong", ex);
                    } catch( UnknownStructureException ex ) {
                        LOGGER.error("Unknown Structure in Reaction for EC", ex);
                    }
                }

            }

        }

        setCompletedStatus();
    }


    private Identifier generateId(String id) {
        String[] sections = id.split("[^A-z0-9]");
        StringBuilder builder = new StringBuilder(sections.length);
        for( String string : sections ) {
            if( string.length() > 3 ) {
                builder.append(string.subSequence(0, 3));
            } else {
                builder.append(string);
            }
        }
        return new BasicProteinIdentifier(builder.toString());
    }


    @Override
    public void postrun() {
        // add to the products
        // MainView.getInstance().notifyProjectTreeOfStructureChange( ReconstructionManager.getInstance() );
        MainFrame.getInstance().getSourceListController().update();
        MainFrame.getInstance().getViewController().update();
    }


    @Override
    public String getTaskDescription() {
        return "Predict GPR";
    }


    @Override
    public String getTaskCommand() {
        return "non-command line task";
    }


}

