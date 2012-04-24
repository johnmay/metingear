/**
 * CrossReferenceEditor.java
 *
 * 2011.10.07
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
package uk.ac.ebi.mnb.dialog.popup;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.annotation.util.DefaultAnnotationFactory;
import uk.ac.ebi.caf.component.ExpandingComponentList;
import uk.ac.ebi.interfaces.identifiers.Identifier;
import uk.ac.ebi.resource.DefaultIdentifierFactory;
import uk.ac.ebi.chemet.render.components.IdentifierEditor;


/**
 * @name    CrossReferenceEditor - 2011.10.07 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CrossReferenceEditorDialog extends PopupDialog {

    private static final Logger LOGGER = Logger.getLogger(CrossReferenceEditorDialog.class);

    private CellConstraints cc = new CellConstraints();

    private Map<String, Byte> map = new HashMap();

    private FormLayout layout;
    

    private DefaultAnnotationFactory ANNOTATION_FACTORY = DefaultAnnotationFactory.getInstance();

    private DefaultIdentifierFactory ID_FACTORY = DefaultIdentifierFactory.getInstance();

    private ExpandingComponentList<IdentifierEditor> expand;


    public CrossReferenceEditorDialog(JFrame frame) {
        super(frame);

        expand = new ExpandingComponentList<IdentifierEditor>(this) {

            @Override
            public IdentifierEditor newComponent() {
                return new IdentifierEditor();
            }
        };
        expand.append(); // need first component
        getPanel().add(expand.getComponent());
    }


    public void setup(Collection<? extends CrossReference> references) {

        Iterator<? extends CrossReference> referenceIterator = references.iterator();

        if (references.size() > expand.getLimit()) {
            expand.setLimit(references.size());
        }

        expand.setSize(references.size());


        for (int i = 0; i < references.size(); i++) {
            expand.getComponent(i).setIdentifier(referenceIterator.next().getIdentifier());
        }

        if (expand.getSize() == 0) {
            expand.append();
        }

    }


    private void update() {
    }


    public Collection<CrossReference> getCrossReferences() {

        Collection<CrossReference> xref = new ArrayList<CrossReference>();

        for (int i = 0; i < expand.getSize(); i++) {

            IdentifierEditor editor = expand.getComponent(i);

            if (editor.isFilled()) {
                Identifier id = editor.getIdentifier();
                CrossReference crossreference = ANNOTATION_FACTORY.getCrossReference(id);
                xref.add(crossreference);
            }

        }

        return xref;

    }
}
