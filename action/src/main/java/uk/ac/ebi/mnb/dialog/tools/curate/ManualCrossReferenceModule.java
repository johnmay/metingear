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
package uk.ac.ebi.mnb.dialog.tools.curate;

import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.ExpandingComponentList;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.identifier.Identifier;
import uk.ac.ebi.mdk.ui.edit.crossreference.IdentifierEditor;
import uk.ac.ebi.mdk.ui.tool.annotation.CrossreferenceModule;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;


/**
 *
 *          CrossReference 2012.02.15
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 *
 *          Class description
 *
 */
public class ManualCrossReferenceModule implements CrossreferenceModule {

    private static final Logger LOGGER = Logger.getLogger(ManualCrossReferenceModule.class);

    private ExpandingComponentList<IdentifierEditor> expand;

    private Metabolite metabolite;

    private final UndoManager undoManager;

    public ManualCrossReferenceModule(final Window window, UndoManager undoManager) {
        this.undoManager = undoManager;
        expand = new ExpandingComponentList<IdentifierEditor>(window) {
            @Override
            public IdentifierEditor newComponent() {
                return new IdentifierEditor();
            }
        };
        expand.setBackground(window.getBackground());
    }


    public String getDescription() {
        return "Manual Cross-reference";
    }


    public JComponent getComponent() {
        return expand.getComponent();
    }


    public void setup(Metabolite metabolite) {
        expand.reset();
        this.metabolite = metabolite;
    }


    public boolean canTransferAnnotations() {
        return true;
    }


    public void transferAnnotations() throws Exception {
        for (int i = 0; i < expand.getSize(); i++) {
            if (expand.getComponent(i).isFilled()) {
                Identifier id = expand.getComponent(i).getIdentifier();
                Annotation xref = CrossReference.create(id);
                undoManager.addEdit(new AddAnnotationEdit(metabolite, xref));
                metabolite.addAnnotation(xref);
            }
        }
    }
}
