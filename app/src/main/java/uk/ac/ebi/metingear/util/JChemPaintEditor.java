/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
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

import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.caf.action.DelayedBuildAction;
import uk.ac.ebi.metingear.launch.DialogLauncherFactory;
import uk.ac.ebi.metingear.tools.structure.DrawStructure;
import uk.ac.ebi.mnb.interfaces.StructureEditor;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.SwingUtilities;
import java.util.concurrent.Callable;

/**
 * An structure editor utilising JChemPaint.
 *
 * @author John May
 */
public enum JChemPaintEditor implements StructureEditor {

    INSTANCE;

    private final DelayedBuildAction action;
    private final DrawStructure      dialog;

    @SuppressWarnings("unchecked")
    private JChemPaintEditor() {

        MainView view = MainView.getInstance();

        // we use the factory because there's some intricate setup needed
        DialogLauncherFactory factory = new DialogLauncherFactory(view,
                                                                  view,
                                                                  view.getViewController(),
                                                                  view.getUndoManager(),
                                                                  view.getMessageManager(),
                                                                  view);
        this.action = factory.getLauncher(DrawStructure.class);
        this.dialog = (DrawStructure) action.component();
    }

    /** @inheritDoc */
    @Override
    public IAtomContainer edit(final IAtomContainer input) {
        dialog.setStructure(input);
        dialog.setModal(true);
        action.activateActions();
        dialog.setModal(false);
        return dialog.getStructure();
    }
}
