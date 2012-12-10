/*
 * Copyright (c) 2012. John May <jwmay@sf.net>
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

package uk.ac.ebi.mnb.dialog.edit;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicChemicalIdentifier;
import uk.ac.ebi.metingear.edit.entity.MergeMetaboliteEdit;
import uk.ac.ebi.mnb.core.ControllerDialog;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name MergeEntities - 2011.10.04 <br> Class allows merging of entries
 */
public class MergeEntities extends ControllerDialog {

    private static final Logger LOGGER = Logger.getLogger(MergeEntities.class);

    private EntityCollection selection;


    public MergeEntities(JFrame frame,
                         TargetedUpdate updater,
                         ReportManager messages,
                         SelectionController controller,
                         UndoableEditListener undoableEdits) {
        super(frame, updater, messages, controller, undoableEdits, MergeEntities.class.getSimpleName());
        setDefaultLayout();
    }


    @Override
    public JLabel getDescription() {
        JLabel label = super.getDescription();
        label.setText("Merge multiple entries into one");
        return label;
    }


    @Override
    public JPanel getForm() {
        return super.getForm();
    }


    @Override
    public void process() {

        selection = getSelection();
        Collection<Metabolite> entities = selection.get(Metabolite.class);


        Reconstruction recon = DefaultReconstructionManager.getInstance().getActive();

        List<Annotation> annotations = new ArrayList<Annotation>();

        Set<String> names = new HashSet<String>();
        Set<String> abbreviations = new HashSet<String>();

        for (Metabolite m : entities) {

            names.add(m.getName().trim());
            abbreviations.add(m.getName().trim());

            annotations.addAll(m.getAnnotations());
        }

        Metabolite union = DefaultEntityFactory.getInstance().newInstance(Metabolite.class,
                                                                          BasicChemicalIdentifier.nextIdentifier(),
                                                                          Joiner.on(" ").join(names),
                                                                          Joiner.on(" ").join(abbreviations));
        // add all annotations to the union
        union.addAnnotations(annotations);

        MergeMetaboliteEdit edit = new MergeMetaboliteEdit(new ArrayList<Metabolite>(entities),
                                                           union,
                                                           recon);

        addEdit(edit); // add the edit to the manager

        edit.apply(); // actually do the edit - this is quite complex, hence it is bundled in with the undoable edit


    }


    @Override
    public void setVisible(boolean visible) {

//        // check they're all the same class

        super.setVisible(visible);
    }
}
