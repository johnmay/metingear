/**
 * DownloadStructuresDialog.java
 *
 * 2011.09.27
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
package uk.ac.ebi.mnb.dialog.tools;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.chemet.resource.chemical.ChEBIIdentifier;
import uk.ac.ebi.chemet.resource.chemical.KEGGCompoundIdentifier;
import uk.ac.ebi.mdk.service.query.structure.KEGGCompoundStructureService;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *          DownloadStructuresDialog – 2011.09.27 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DownloadStructuresDialog
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(DownloadStructuresDialog.class);

    private Collection<AnnotatedEntity> components;

    private ChEBIWebServiceConnection chebi;

    private KEGGCompoundStructureService keggService = new KEGGCompoundStructureService();

    private JCheckBox chebiCheckBox;

    private JCheckBox keggCheckBox;

    private JCheckBox chebiAllStarCheckBox;


    public DownloadStructuresDialog(JFrame frame,
                                    TargetedUpdate updater,
                                    ReportManager messages,
                                    SelectionController controller,
                                    UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");

        chebiCheckBox = CheckBoxFactory.newCheckBox("ChEBI (currated only)");
        keggCheckBox = CheckBoxFactory.newCheckBox("KEGG Compound");
        chebiAllStarCheckBox = CheckBoxFactory.newCheckBox("ChEBI (All)");

        setDefaultLayout();
    }


    public JPanel getForm() {
        JPanel panel = super.getForm();
        CellConstraints cc = new CellConstraints();



        panel.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));
        panel.add(chebiCheckBox, cc.xy(1, 1));
        panel.add(chebiAllStarCheckBox, cc.xy(3, 1));
        panel.add(keggCheckBox, cc.xy(1, 3));


        return panel;
    }


    @Override
    public void process(final SpinningDialWaitIndicator wait) {


        if (chebi == null) {
            chebi = new ChEBIWebServiceConnection();
        }
//        if (kegg == null) {
//            kegg = new KEGGCompoundStructureService();
//        }

        boolean useChEBI = chebiAllStarCheckBox.isSelected() || chebiCheckBox.isSelected();
        boolean useKEGG  = keggCheckBox.isSelected();

        // set chebi filtering
        if (chebiAllStarCheckBox.isSelected()) {
            chebi.setStarsCategory(StarsCategory.ALL);
        } else {
            chebi.setStarsCategory(StarsCategory.THREE_ONLY);
        }

        List<Identifier> problemIdentifiers = new ArrayList();

        int completed = 0;
        int size = getSelection().get(Metabolite.class).size();

        for (AnnotatedEntity component : getSelection().get(Metabolite.class)) {

            completed++;

            final float perc = completed / (float) size;

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    wait.setText(String.format("Downloading... %.1f%%", perc * 100));
                }
            });

            for (Annotation xref : component.getAnnotationsExtending(CrossReference.class)) {

                Identifier id = ((CrossReference) xref).getIdentifier();

                System.out.println(id.getShortDescription() + ":" + id);

                if (useChEBI && id instanceof ChEBIIdentifier) {
                    try {
                        IAtomContainer molecule = chebi.getAtomContainer(id.getAccession());
                        if (molecule != null) {
                            component.addAnnotation(new AtomContainerAnnotation(molecule));
                        } else {
                            problemIdentifiers.add(id);
                        }
                    } catch (ExceptionInInitializerError ex) {
                        problemIdentifiers.add(id);
                    } catch (NoClassDefFoundError ex) {
                        problemIdentifiers.add(id);
                    } catch (Exception ex) {
                        problemIdentifiers.add(id);
                    }
                } else if (useKEGG && id instanceof KEGGCompoundIdentifier) {
                    IAtomContainer molecule;
                    try {
                        molecule =  keggService.getStructure((KEGGCompoundIdentifier) id);
                        if (molecule != null) {
                            component.addAnnotation(new AtomContainerAnnotation(molecule));
                        }
                    }  catch (Exception ex) {
                        problemIdentifiers.add(id);
                    }
                }
            }
        }


        if (problemIdentifiers.isEmpty() == false) {
            addMessage(new WarningMessage("Unable to download structure for " + StringUtils.join(problemIdentifiers,
                                                                                                 ", ")));
        }



    }


    @Override
    public void process() {
        // do nothing
    }


    @Override
    public boolean update() {

        // rebuild the map to avoid problems with non-matches hashes
        DefaultReconstructionManager.getInstance().getActive().getReactome().rebuildParticipantMap();

        return update(getSelection());

    }
}
