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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.interfaces.MessageManager;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.chemet.ws.exceptions.UnfetchableEntry;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.core.ReconstructionManager;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.io.service.KEGGCompoundStructureService;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.ContextAction;
import uk.ac.ebi.mnb.view.MCheckBox;
import uk.ac.ebi.resource.chemical.ChEBIIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;

/**
 *          DownloadStructuresDialog – 2011.09.27 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DownloadStructuresDialog
        extends ControllerDialog
        implements ContextAction {

    private static final Logger LOGGER = Logger.getLogger(DownloadStructuresDialog.class);
    private Collection<AnnotatedEntity> components;
    private ChEBIWebServiceConnection chebi;
    private KeggCompoundWebServiceConnection kegg;
    private JCheckBox chebiCheckBox;
    private JCheckBox keggCheckBox;
    private JCheckBox chebiAllStarCheckBox;

    public DownloadStructuresDialog(JFrame frame, TargetedUpdate updater, MessageManager messages, SelectionController controller, UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");

        chebiCheckBox = new MCheckBox("ChEBI (currated only)");
        keggCheckBox = new MCheckBox("KEGG Compound");
        chebiAllStarCheckBox = new MCheckBox("ChEBI (All)");

        setDefaultLayout();
    }

    public JPanel getOptions() {
        JPanel panel = super.getOptions();
        CellConstraints cc = new CellConstraints();



        panel.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));
        panel.add(chebiCheckBox, cc.xy(1, 1));
        panel.add(chebiAllStarCheckBox, cc.xy(3, 1));
        panel.add(keggCheckBox, cc.xy(1, 3));


        return panel;
    }

    @Override
    public void process() {


        if (chebi == null) {
            chebi = new ChEBIWebServiceConnection();
        }
//        if (kegg == null) {
//            kegg = new KeggCompoundWebServiceConnection();
//        }

        boolean useChEBI = chebiAllStarCheckBox.isSelected() || chebiCheckBox.isSelected();
        boolean useKEGG = keggCheckBox.isSelected();

        // set chebi filtering
        if (chebiAllStarCheckBox.isSelected()) {
            chebi.setStarsCategory(StarsCategory.ALL);
        } else {
            chebi.setStarsCategory(StarsCategory.THREE_ONLY);
        }

        List<Identifier> problemIdentifiers = new ArrayList();

        for (AnnotatedEntity component : getSelection().get(Metabolite.class)) {

            for (Annotation xref : component.getAnnotationsExtending(CrossReference.class)) {

                Identifier id = ((CrossReference) xref).getIdentifier();

                System.out.println(id.getShortDescription() + ":" + id);

                if (useChEBI && id instanceof ChEBIIdentifier) {
                    try {
                        IAtomContainer molecule = chebi.getAtomContainer(id.getAccession());
                        if (molecule != null) {
                            component.addAnnotation(new ChemicalStructure(molecule));
                        } else {
                            problemIdentifiers.add(id);
                        }
                    } catch (Exception ex) {
                        problemIdentifiers.add(id);
                    }
                } else if (useKEGG && id instanceof KEGGCompoundIdentifier) {
                    IAtomContainer molecule;
                    try {
                        molecule = KEGGCompoundStructureService.getInstance().getStructure((KEGGCompoundIdentifier) id);
                        System.out.println(molecule);
                        component.addAnnotation(new ChemicalStructure(molecule));
                    } catch (UnfetchableEntry ex) {
                        problemIdentifiers.add(id);
                    }
                }
            }
        }


        if (problemIdentifiers.isEmpty() == false) {
            addMessage(new WarningMessage("Unable to download structure for " + StringUtils.join(problemIdentifiers, ", ")));
        }



    }

    @Override
    public boolean update() {

        // rebuild the map to avoid problems with non-equal hashes
        ReconstructionManager.getInstance().getActive().getReactions().rebuildParticipantMap();

        return update(getSelection());

    }

    public boolean setContext() {
        return getSelection().hasSelection(Metabolite.class);
    }

    public boolean setContext(Object obj) {
        return setContext();
    }
}
