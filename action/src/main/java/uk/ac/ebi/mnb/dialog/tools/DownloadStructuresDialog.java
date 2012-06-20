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
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.LabelFactory;
import uk.ac.ebi.caf.component.list.MutableJListController;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.service.query.QueryService;
import uk.ac.ebi.mdk.service.query.structure.StructureService;
import uk.ac.ebi.mdk.ui.component.ResourceList;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.core.WarningMessage;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Dialog buffers downloading/fetching of structures
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class DownloadStructuresDialog
        extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(DownloadStructuresDialog.class);


    private JLabel allowWebServiceLabel = LabelFactory.newFormLabel("Allow Web services:",
                                                                    "Indicate you want to allow the use of web services to download" +
                                                                            " structures. Web services will dramatically reduce the speed and should" +
                                                                            " only be used for small numbers of entries");
    private JLabel fetchAllLabel        = LabelFactory.newFormLabel("Greedy mode:",
                                                                    "Retrieve all structures for all cross-references - structures can be filtered post download");

    private JCheckBox fetchAll = CheckBoxFactory.newCheckBox("");
    private JCheckBox ws       = CheckBoxFactory.newCheckBox("");

    private ResourceList resourceSelection = new ResourceList();


    public DownloadStructuresDialog(JFrame frame,
                                    TargetedUpdate updater,
                                    ReportManager messages,
                                    SelectionController controller,
                                    UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, "RunDialog");

        // blend the list in
        resourceSelection.setBackground(getBackground());
        resourceSelection.setForeground(LabelFactory.newFormLabel("").getForeground());
        resourceSelection.setVisibleRowCount(6);

        ws.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateResourceList();
            }
        });

        setDefaultLayout();

    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Retrieve structures for selected metabolites");
        return label;
    }

    public JPanel getForm() {
        JPanel panel = super.getForm();
        CellConstraints cc = new CellConstraints();

        panel.setLayout(new FormLayout("right:p, 4dlu,    left:p:grow",
                                       "p, 4dlu, p, 4dlu, top:p:grow"));


        panel.add(allowWebServiceLabel, cc.xy(1, 1));
        panel.add(ws, cc.xy(3, 1));
        panel.add(fetchAllLabel, cc.xy(1, 3));
        panel.add(fetchAll, cc.xy(3, 3));

        panel.add(LabelFactory.newFormLabel("Resource Priority:"), cc.xy(1, 5));
        panel.add(new MutableJListController(resourceSelection).getListWithController(),
                  cc.xy(3, 5, CellConstraints.FILL, CellConstraints.FILL));

        return panel;
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            updateResourceList();
        }
        super.setVisible(b);
    }

    @Override
    public void process(final SpinningDialWaitIndicator wait) {

        List<Identifier> problemIdentifiers = new ArrayList();

        ServiceManager services = DefaultServiceManager.getInstance();

        int i = 0;
        int n = getSelection().get(Metabolite.class).size();

        // could re-arrange data to make this easier
        for (Metabolite m : getSelection().get(Metabolite.class)) {

            ANNOTATION:
            for (CrossReference reference : m.getAnnotationsExtending(CrossReference.class)) {
                for (Identifier identifier : resourceSelection.getElements()) {

                    if (identifier.getClass().equals(reference.getIdentifier().getClass())) {

                        // get the appropiate service for the given ientifier
                        StructureService service = services.getService(identifier,
                                                                       StructureService.class);

                        if (canUse(service)) {

                            IAtomContainer structure = service.getStructure(reference.getIdentifier());

                            // don't add empty structures
                            if (structure.getAtomCount() > 1) {
                                m.addAnnotation(new AtomContainerAnnotation(structure));

                                // only get first
                                if (!fetchAll.isSelected())
                                    break ANNOTATION;

                            } else {
                                // record one's that wern't downloaded
                                problemIdentifiers.add(reference.getIdentifier());
                            }

                        }


                    }

                }

            }

            final float perc = (float) ++i / n;
            // update the text on the wait indicator
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    wait.setText(String.format("Retrieving... %.1f%%", perc * 100));
                }
            });


        }


        if (problemIdentifiers.isEmpty() == false) {
            addMessage(new WarningMessage("The following identifiers had empty or missing structures: " + StringUtils.join(problemIdentifiers, ", ")));
        }


    }


    @Override
    public void process() {
        // do nothing
    }

    public boolean canUse(QueryService service) {
        QueryService.ServiceType type = service.getServiceType();
        return ws.isSelected()
                || !type.equals(QueryService.ServiceType.SOAP_WEB_SERVICE)
                && !type.equals(QueryService.ServiceType.REST_WEB_SERVICE);
    }


    @Override
    public boolean update() {

        // rebuild the map to avoid problems with non-matches hashes
        DefaultReconstructionManager.getInstance().getActive().getReactome().rebuildMaps();

        return update(getSelection());

    }

    private void updateResourceList() {

        ServiceManager services = DefaultServiceManager.getInstance();
        Set<Identifier> available = new HashSet<Identifier>();

        for (Identifier id : services.getIdentifiers(StructureService.class)) {
            if (services.hasService(id, StructureService.class) &&
                    canUse(services.getService(id, StructureService.class))) {
                available.add(id);
            }
        }

        Set<Identifier> accept = new HashSet<Identifier>();

        long st = System.currentTimeMillis();
        resourceSelection.getModel().removeAllElements();
        // set up the resources
        for (Metabolite metabolite : getSelection().get(Metabolite.class)) {
            for (CrossReference xref : metabolite.getAnnotationsExtending(CrossReference.class)) {
                for (Identifier identifier : available) {
                    if (xref.getIdentifier().getClass().equals(identifier.getClass())) {
                        resourceSelection.addElement(identifier);
                        accept.add(identifier);
                        break;
                    }
                }
                available.removeAll(accept);
            }
        }
        long end = System.currentTimeMillis();

        if (resourceSelection.getElements().size() > 1)
            resourceSelection.setSelectedIndex(0);

        pack();

    }

    public static void main(String[] args) {
        ServiceManager manager = DefaultServiceManager.getInstance();
        for (Identifier id : manager.getIdentifiers(StructureService.class)) {
            System.out.println(id.getShortDescription());
        }
    }


}
