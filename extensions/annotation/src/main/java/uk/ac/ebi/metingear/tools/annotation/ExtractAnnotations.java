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

package uk.ac.ebi.metingear.tools.annotation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.CheckBoxFactory;
import uk.ac.ebi.caf.component.factory.ComboBoxFactory;
import uk.ac.ebi.caf.component.factory.FieldFactory;
import uk.ac.ebi.chemet.tools.annotation.BasicPatternMatcher;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.InChI;
import uk.ac.ebi.mdk.domain.annotation.Locus;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.Note;
import uk.ac.ebi.mdk.domain.annotation.SMILES;
import uk.ac.ebi.mdk.domain.annotation.primitive.StringAnnotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.metingear.view.AbstractControlDialog;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;

import javax.swing.*;
import javax.swing.undo.CompoundEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class ExtractAnnotations extends AbstractControlDialog {

    private static final Logger LOGGER = Logger.getLogger(ExtractAnnotations.class);

    private final JCheckBox caseInsensitive = CheckBoxFactory.newCheckBox();
    private final JTextField patternField = FieldFactory.newField(20);
    private final JComboBox annotationComboBox = ComboBoxFactory.newComboBox(getSelectableAnnotations());

    private final Map<Class, String> defaults = getDefaults();

    public ExtractAnnotations(Window window) {
        super(window);
    }

    @Override
    public void prepare() {
        super.prepare();
        if(annotationComboBox.getSelectedItem() == null)
            setEnabled(false);
    }

    @Override
    public JComponent createForm() {

        JComponent component = super.createForm();

        annotationComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value != null) {
                    Annotation annotation = (Annotation) value;
                    this.setText(annotation.getBrief());
                } else {
                    this.setText("-- select annotation --");
                }
                return this;
            }
        });

        component.setLayout(new FormLayout("right:p, 4dlu, left:p",
                                           "p, 4dlu, p, 4dlu, p"));

        CellConstraints cc = new CellConstraints();

        component.add(getLabel("annotationLabel"), cc.xy(1, 1));
        component.add(annotationComboBox, cc.xy(3, 1));
        component.add(getLabel("patternLabel"), cc.xy(1, 3));
        component.add(patternField, cc.xy(3, 3));
        component.add(getLabel("ciLabel"), cc.xy(1, 5));
        component.add(caseInsensitive, cc.xy(3, 5));

        patternField.setEnabled(false);
        caseInsensitive.setSelected(true);
        caseInsensitive.setEnabled(false);


        // action listener will fill out a suggested pattern for selected annotation types
        annotationComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = annotationComboBox.getSelectedItem();
                if(obj == null) {
                    patternField.setEnabled(false);
                    caseInsensitive.setEnabled(false);
                    setEnabled(false);
                } else {
                    patternField.setEnabled(true);
                    caseInsensitive.setEnabled(true);
                    patternField.setText(getDefault(obj.getClass()));
                    patternField.setCaretPosition(0);
                    setEnabled(true);
                }
            }
        });

        return component;

    }

    @Override
    public void process() {

        // put all the edits together
        CompoundEdit edit = new CompoundEdit();

        Pattern pattern = caseInsensitive.isSelected()
                ? Pattern.compile(patternField.getText(), Pattern.CASE_INSENSITIVE)
                : Pattern.compile(patternField.getText());

        BasicPatternMatcher<Note> matcher = new BasicPatternMatcher<Note>(Note.class,
                                                                          pattern);

        StringAnnotation target = (StringAnnotation) annotationComboBox.getSelectedItem();

        List<Annotation> queue = new ArrayList<Annotation>();

        for (AnnotatedEntity entity : getSelectionController().getSelection().getEntities()) {

            for (Annotation annotation : entity.getAnnotations()) {

                String matched = annotation.accept(matcher);

                if (!matched.isEmpty()) {
                    queue.add(target.getInstance(matched));
                }

            }

            edit.addEdit(new AddAnnotationEdit(entity, queue));
            entity.addAnnotations(queue);
            queue.clear();

        }

        // inform the compound edit that we've finished editing
        edit.end();

        addEdit(edit);

    }

    @Override
    public void update() {
        super.update(getSelectionController().getSelection());
    }


    private static Collection<? extends StringAnnotation> getSelectableAnnotations() {

        AnnotationFactory factory = DefaultAnnotationFactory.getInstance();
        Collection<StringAnnotation> annotations = new ArrayList<StringAnnotation>();
        annotations.add(null);
        annotations.addAll(factory.getSubclassInstances(StringAnnotation.class));

        return annotations;

    }

    private String getDefault(Class<? extends Object> c) {
        if(defaults.containsKey(c))
            return defaults.get(c);
        return "";
    }

    private static Map<Class, String> getDefaults() {

        Map<Class, String> patterns = new HashMap<Class, String>();

        patterns.put(MolecularFormula.class, "Formula:\\s+((?:[A-z]+[0-9]*?)+)");
        patterns.put(InChI.class,            "InChI:\\s+(InChI=1S?/.+)");
        patterns.put(Locus.class,            "Locus:\\s+(.+)");
        patterns.put(SMILES.class,           "SMILES:\\s+([^J][0-9BCOHNSOPrIFla@+\\-\\[\\]\\(\\)\\\\/%=#$]+)");

        return patterns;

    }


}
