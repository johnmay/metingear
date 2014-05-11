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
package uk.ac.ebi.mnb.menu;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.mdk.domain.annotation.AtomContainerAnnotation;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.EntityCollection;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.metingear.preference.PreferenceFrame;
import uk.ac.ebi.metingeer.interfaces.menu.ContextResponder;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.dialog.edit.AddAnnotation;
import uk.ac.ebi.mnb.dialog.edit.AddAuthorAnnotation;
import uk.ac.ebi.mnb.dialog.edit.ClearAnnotations;
import uk.ac.ebi.mnb.dialog.edit.CreateSubset;
import uk.ac.ebi.mnb.dialog.edit.DeleteEntities;
import uk.ac.ebi.mnb.dialog.edit.MergeEntities;
import uk.ac.ebi.mnb.dialog.edit.ReassignIdentifiers;
import uk.ac.ebi.mnb.dialog.edit.Resync;
import uk.ac.ebi.mnb.dialog.edit.SplitMetabolites;
import uk.ac.ebi.mnb.main.MainView;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;


/**
 * EditMenu – 2011.09.26 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class EditMenu extends ContextMenu {

    private static final Logger LOGGER = Logger.getLogger(EditMenu.class);

    private boolean prefItemLoaded = false;
    
    private final boolean devItems = Boolean.getBoolean("metingear.developer");


    public EditMenu() {
        super("Edit", MainView.getInstance());

        final MainView view = MainView.getInstance();

        add(new JMenuItem(new GeneralAction("Undo") {

            public void actionPerformed(ActionEvent ae) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canUndo()) {
                    manager.undo();
                    MainView.getInstance().update();
                }
            }
        }));
        add(new JMenuItem(new GeneralAction("Redo") {

            public void actionPerformed(ActionEvent ae) {
                UndoManager manager = MainView.getInstance().getUndoManager();
                if (manager.canRedo()) {
                    manager.redo();
                    MainView.getInstance().update();
                }
            }
        }));

        add(new JSeparator());
        add(create(MergeEntities.class), new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null && selection
                        .hasSelection(Metabolite.class) && selection
                        .get(Metabolite.class).size() > 1;
            }
        });
        add(create(SplitMetabolites.class), new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null && selection
                        .hasSelection(Metabolite.class) && selection
                        .get(Metabolite.class).size() == 1;
            }
        });
        add(new DeleteEntities(MainView.getInstance()));

        add(new JSeparator());
        add(create(CreateSubset.class), new ContextResponder() {

            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection();
            }
        });

        add(new JSeparator());
        add(create(AddAuthorAnnotation.class));
        add(create(AddAnnotation.class));
        add(new ReassignIdentifiers(MainView.getInstance()), new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return selection.hasSelection();
            }
        });
        add(new Resync(MainView.getInstance()), new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null;
            }
        });
        
        
        
        add(new ControllerAction("fix.valence.errors", MainView.getInstance()) {
            @Override public void actionPerformed(ActionEvent e) {
                for (Metabolite m : getSelection().get(Metabolite.class)) {
                    for (ChemicalStructure cs : m.getStructures()) {
                        if (cs instanceof AtomContainerAnnotation) {
                            try {
                                IAtomContainer ac = cs.getStructure();
                                for (IAtom a : ac.atoms()) {
                                    a.setValency(null);
                                    a.setImplicitHydrogenCount(null);
                                }
                                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                                CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance())
                                                .addImplicitHydrogens(ac);
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());  
                            }
                        }
                    }
                }
            }
        }, new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return devItems;
            }
        });
        add(new ClearAnnotations(MainView.getInstance()), new ContextResponder() {
            @Override
            public boolean getContext(ReconstructionManager reconstructions, Reconstruction active, EntityCollection selection) {
                return active != null && !selection.isEmpty();
            }
        });

    }

    // work around to allow plugins to load before the preferences item
    public void addPreferenceItem() {
        if (!prefItemLoaded && !hasPreferenceOnMenuBar()) {
            add(new JSeparator());
            add(new AbstractAction("Preferences") {

                PreferenceFrame preferences = new PreferenceFrame();

                @Override
                public void actionPerformed(ActionEvent e) {
                    preferences.setVisible(true);
                    preferences.pack();
                }
            });
            prefItemLoaded = true;
        }
    }

    public boolean hasPreferenceOnMenuBar() {
        return System.getProperty("os.name").equals("Mac OS X") && "true"
                .equals(System.getProperty("apple.laf.useScreenMenuBar"));
    }
}
