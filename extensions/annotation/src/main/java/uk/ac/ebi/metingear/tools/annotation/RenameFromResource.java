/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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

package uk.ac.ebi.metingear.tools.annotation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.service.DefaultServiceManager;
import uk.ac.ebi.mdk.service.ServiceManager;
import uk.ac.ebi.mdk.service.query.QueryService;
import uk.ac.ebi.mdk.service.query.name.PreferredNameService;
import uk.ac.ebi.mdk.ui.render.list.DefaultRenderer;
import uk.ac.ebi.metingear.edit.entity.RenameMetaboliteEdit;
import uk.ac.ebi.metingear.view.AbstractControlDialog;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.undo.CompoundEdit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

/**
 * A dialog to rename entries based on their cross-reference. The resource is
 * selected from a combo box and all names are replaced.
 */
public class RenameFromResource extends AbstractControlDialog {


    private static final Logger LOGGER = Logger
            .getLogger(RenameFromResource.class);

    /* indiciates whether web services can be used */
    private final JCheckBox webServices = CheckBoxFactory.newCheckBox();

    /* select a resource to rename from */
    private final JComboBox resourceSelection = ComboBoxFactory.newComboBox();

    /**
     * Create a new RenameFromResource dialog from a window.
     *
     * @param window the window to which the dialog will belong
     */
    public RenameFromResource(Window window) {
        super(window);
        resourceSelection.setModel(new DefaultComboBoxModel());
        resourceSelection.setRenderer(new DefaultRenderer<QueryService>() {
            @Override
            public JLabel getComponent(JList list, QueryService value, int index) {
                JLabel label = super.getComponent(list, value, index);
                String resource = value.getIdentifier().getBrief();

                if (value.getServiceType().remote())
                    label.setText(resource + " (remote)");
                else
                    label.setText(resource);

                return label;
            }
        });
        webServices.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                // re-prepare the resource list if the web-service selection has changed
                prepare();
            }
        });
    }

    /** @inheritDoc */
    @Override
    public void prepare() {

        // update available services based on the current selection
        ServiceManager services = DefaultServiceManager.getInstance();
        Map<Identifier, QueryService> available = new TreeMap<Identifier, QueryService>();

        for (Identifier id : services.getIdentifiers(PreferredNameService.class)) {
            if (services.hasService(id, PreferredNameService.class) &&
                    isUsable(services.getService(id, PreferredNameService.class))) {
                try {
                    available.put(id, services
                            .getService(id, PreferredNameService.class));
                } catch (NoSuchElementException ex) {
                    // timeout
                }
            }
        }

        Set<Identifier> accept = new HashSet<Identifier>();

        long start = System.currentTimeMillis();
        DefaultComboBoxModel model = (DefaultComboBoxModel) resourceSelection
                .getModel();
        model.removeAllElements();
        // set up the resources
        for (Metabolite metabolite : getSelection(Metabolite.class)) {
            for (CrossReference xref : metabolite
                    .getAnnotationsExtending(CrossReference.class)) {
                for (Identifier identifier : available.keySet()) {
                    if (xref.getIdentifier().getClass().equals(identifier
                                                                       .getClass())) {
                        model.addElement(available.get(identifier));
                        accept.add(identifier);
                        break;
                    }
                }

                for (Identifier accepted : accept)
                    available.remove(accepted);

            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("loaded resource model in: " + (end - start));

        if (model.getSize() > 1)
            resourceSelection.setSelectedIndex(0);

        pack();

    }

    /**
     * Check whether webServices are allowed and if this service is not a web
     * service.
     */
    public boolean isUsable(QueryService service) {
        return webServices.isSelected() || !service.getServiceType().remote();
    }

    /** @inheritDoc */
    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();
        CellConstraints cc = new CellConstraints();

        component.setLayout(new FormLayout("right:p, 4dlu, left:p:grow",
                                           "p, 4dlu, p"));


        component.add(getLabel("webServiesLabel"), cc.xy(1, 1));
        component.add(webServices, cc.xy(3, 1));

        component.add(getLabel("resourceSelection"), cc.xy(1, 3));
        component.add(resourceSelection, cc.xy(3, 3));

        return component;

    }

    @Override
    public void process(final SpinningDialWaitIndicator indicator) {

        // new edit action (one for all entries)
        CompoundEdit edits = new CompoundEdit();

        // get the preferred name service
        PreferredNameService service = (PreferredNameService) resourceSelection.getSelectedItem();

        Reconstruction reconstruction = DefaultReconstructionManager.getInstance().active();

        List<Metabolite> selection = new ArrayList<Metabolite>(getSelection(Metabolite.class));

        int done = 0;

        // for each selected metabolite select the first cross-reference which
        // matches the class of the identifier and set the new name
        for (final Metabolite metabolite : selection) {
            final String progress = String.format("%.1f%%", 100 * (done / (float) selection.size()));
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    indicator.setText("renaming " + metabolite.getName() + "... " + progress);
                }
            });

            for (CrossReference xref : metabolite.getAnnotationsExtending(CrossReference.class)) {
                if (xref.getIdentifier().getClass()
                        .equals(service.getIdentifier().getClass())) {

                    // old/new names for undo/redo closure
                    final String newName = service
                            .getPreferredName(xref.getIdentifier());

                    if (newName.isEmpty())
                        continue;

                    // create the edit
                    RenameMetaboliteEdit edit = new RenameMetaboliteEdit(metabolite, newName, reconstruction);
                    edits.addEdit(edit);

                    // actually perform the edit
                    edit.apply();

                    // don't update the name any more
                    break;

                }
            }
            done++;
        }


        edits.end();

        addEdit(edits);

    }


}
