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

package uk.ac.ebi.metingear.view;

import com.google.common.base.Joiner;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.annotation.Charge;
import uk.ac.ebi.mdk.domain.annotation.ChemicalStructure;
import uk.ac.ebi.mdk.domain.annotation.MolecularFormula;
import uk.ac.ebi.mdk.domain.annotation.crossreference.CrossReference;
import uk.ac.ebi.mdk.domain.annotation.primitive.DoubleAnnotation;
import uk.ac.ebi.mdk.domain.annotation.primitive.FloatAnnotation;
import uk.ac.ebi.mdk.domain.annotation.primitive.StringAnnotation;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.StarRating;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.metingear.AppliableEdit;
import uk.ac.ebi.mnb.edit.AbbreviationEdit;
import uk.ac.ebi.mnb.edit.AddAnnotationEdit;
import uk.ac.ebi.mnb.edit.NameEdit;
import uk.ac.ebi.mnb.edit.RemoveAnnotationEdit;
import uk.ac.ebi.mnb.edit.ReplaceAnnotationEdit;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.undo.CompoundEdit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author John May
 */
class MetaboliteInfoModel extends AbstractTableModel {

    private Metabolite           mtbl;
    private Row[]                rows;
    private UndoableEditListener editListener;

    MetaboliteInfoModel(Metabolite mtbl, UndoableEditListener editListener, List<MetabolicReaction> reactions) {

        this.mtbl = mtbl;
        this.editListener = editListener;
        List<Row> rowList = new ArrayList<Row>();
        if (mtbl != null) {

            Set<Annotation> inlined = new HashSet<Annotation>();

            rowList.add(new Row(Type.Abbreviation, mtbl.getAbbreviation()));
            rowList.add(new Row(Type.Name, mtbl.getName()));
//            rowList.add(new Row(Type.Confidence, mtbl.getRating()));

            Collection<MolecularFormula> formulae = mtbl.getAnnotations(MolecularFormula.class);
            Collection<Charge> charges = mtbl.getAnnotations(Charge.class);

            // charge is always unique
            if (formulae.size() <= 1) {

                IMolecularFormula formula = formulae.isEmpty() ? null : formulae.iterator().next().getFormula();
                if (formula != null && !charges.isEmpty()) {
                    formula.setCharge(charges.iterator().next().getValue().intValue());
                }
                rowList.add(new Row(Type.Formula, formula));

                // mark as added        
                if (!formulae.isEmpty())
                    inlined.add(formulae.iterator().next());
                if (!charges.isEmpty()) inlined.add(charges.iterator().next());
            }
            else {
                for (MolecularFormula formula : formulae) {
                    rowList.add(new Row(Type.EditableAnnotation, formula));
                    inlined.add(formula);
                }
                for (Charge charge : charges) {
                    rowList.add(new Row(Type.EditableAnnotation, charge));
                    inlined.add(charge);
                }
            }


            if (!reactions.isEmpty()) {
                Set<String> compartments = new TreeSet<String>();
                for (MetabolicReaction rxn : reactions) {
                    for (MetabolicParticipant p : rxn.getParticipants()) {
                        if (p.getMolecule() == mtbl) {
                            compartments.add(p.getCompartment().getDescription());
                        }
                    }
                }
                rowList.add(new Row(Type.Compartments, Joiner.on(", ").join(compartments)));
                rowList.add(new Row(Type.Reactions, reactions));
            }

            List<Class> classes = new ArrayList<Class>(mtbl.getAnnotationClasses());
            Collections.sort(classes, new Comparator<Class>() {
                @Override public int compare(Class a, Class b) {
                    return a.getSimpleName().compareTo(b.getSimpleName());
                }
            });

            for (Class<Annotation> c : classes) {
                if (ChemicalStructure.class.isAssignableFrom(c))
                    continue;

                Type type = Type.Annotation;
                if (StringAnnotation.class.isAssignableFrom(c))
                    type = Type.EditableAnnotation;
                else if (DoubleAnnotation.class.isAssignableFrom(c))
                    type = Type.EditableAnnotation;
                else if (FloatAnnotation.class.isAssignableFrom(c))
                    type = Type.EditableAnnotation;
                else if (CrossReference.class.isAssignableFrom(c))
                    type = Type.Xref;

                for (Annotation annotation : mtbl.getAnnotations(c)) {
                    if (inlined.contains(annotation))
                        continue;
                    rowList.add(new Row(type, annotation));
                }
            }
        }
        rows = rowList.toArray(new Row[rowList.size()]);
    }

    public Type typeOf(int rowIndex) {
        return rows[rowIndex].type;
    }

    @Override public void setValueAt(Object value, int rowIndex, int columnIndex) {
        switch (typeOf(rowIndex)) {
            case Abbreviation:
                String newAbrv = value.toString();
                if (newAbrv.equals(mtbl.getAbbreviation()))
                    return;
                editListener.undoableEditHappened(new UndoableEditEvent(this,
                                                                        new AbbreviationEdit(mtbl, newAbrv)));
                mtbl.setAbbreviation(newAbrv);
                rows[rowIndex] = new Row(Type.Abbreviation, newAbrv);
                fireTableCellUpdated(rowIndex, columnIndex);
                break;
            case Name:
                String newName = value.toString();
                if (newName.equals(mtbl.getName()))
                    return;
                editListener.undoableEditHappened(new UndoableEditEvent(this,
                                                                        new NameEdit(mtbl, newName)));
                mtbl.setName(newName);
                rows[rowIndex] = new Row(Type.Name, newName);
                fireTableCellUpdated(rowIndex, columnIndex);
                break;
            case Formula:

                Collection<MolecularFormula> mfAnns = mtbl.getAnnotationsExtending(MolecularFormula.class);
                Collection<Charge> cghsAnns = mtbl.getAnnotationsExtending(Charge.class);

                // something wrong
                if (mfAnns.size() > 1)
                    break;

                MolecularFormula oldMf = mfAnns.isEmpty() ? null : mfAnns.iterator().next();
                Charge oldChg = cghsAnns.isEmpty() ? null : cghsAnns.iterator().next();

                String asciiFormula = (String) value;

                // delete formula and charge
                if (asciiFormula.isEmpty()) {
                    CompoundEdit edit = new CompoundEdit();
                    if (oldChg != null) {
                        edit.addEdit(new RemoveAnnotationEdit(mtbl, oldChg));
                        mtbl.removeAnnotation(oldChg);
                    }
                    if (oldMf != null) {
                        edit.addEdit(new RemoveAnnotationEdit(mtbl, oldMf));
                        mtbl.removeAnnotation(oldMf);
                    }
                    edit.end();
                    editListener.undoableEditHappened(new UndoableEditEvent(this, edit));
                    rows[rowIndex] = new Row(Type.Formula, null);
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
                // modify / add
                else {
                    IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(asciiFormula,
                                                                                                SilentChemObjectBuilder.getInstance());

                    // do nothing if formula could not be parsed
                    if (formula == null)
                        break;

                    // presumed neutral if not charge available
                    Charge newChg = formula.getCharge() != null ? new Charge(formula.getCharge().doubleValue()) : new Charge(0d);
                    MolecularFormula newMf = new MolecularFormula(formula);

                    CompoundEdit edit = new CompoundEdit();
                    if (oldChg != null) {
                        edit.addEdit(new RemoveAnnotationEdit(mtbl, oldChg));
                        mtbl.removeAnnotation(oldChg);
                    }

                    edit.addEdit(new AddAnnotationEdit(mtbl, newChg));
                    mtbl.addAnnotation(newChg);


                    if (oldMf != null) {
                        edit.addEdit(new RemoveAnnotationEdit(mtbl, oldMf));
                        mtbl.removeAnnotation(oldMf);
                    }
                    edit.addEdit(new AddAnnotationEdit(mtbl, newMf));
                    mtbl.addAnnotation(newMf);

                    edit.end();
                    editListener.undoableEditHappened(new UndoableEditEvent(this,
                                                                            edit));

                    rows[rowIndex] = new Row(Type.Formula, formula);
                    fireTableCellUpdated(rowIndex, columnIndex);
                }

                break;
            case Confidence:
                StarRating starRating = (StarRating) value;
                mtbl.setRating(starRating);
                rows[rowIndex] = new Row(Type.Confidence, starRating);
                fireTableCellUpdated(rowIndex, columnIndex);
                break;
            case EditableAnnotation:
            case Xref:
                assert value == null || value instanceof Annotation;
                Annotation oldAnnotation = (Annotation) rows[rowIndex].data;
                Annotation newAnnotation = (Annotation) value;
                if (oldAnnotation == newAnnotation)
                    return;
                if (newAnnotation != null) {
                    AppliableEdit edit = new ReplaceAnnotationEdit(mtbl, oldAnnotation, newAnnotation);
                    editListener.undoableEditHappened(new UndoableEditEvent(this, edit));
                    edit.apply();
                    rows[rowIndex] = new Row(rows[rowIndex].type, newAnnotation);
                    fireTableCellUpdated(rowIndex, columnIndex);
                }
                else {
                    // delete annotation
                    AppliableEdit edit = new RemoveAnnotationEdit(mtbl, oldAnnotation);
                    editListener.undoableEditHappened(new UndoableEditEvent(this, edit));
                    edit.apply();
                    for (int i = rowIndex + 1; i < rows.length; i++) {
                        rows[i - 1] = rows[i];
                    }
                    rows = Arrays.copyOf(rows, rows.length - 1);
                    fireTableDataChanged();
                }
                break;
        }
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        assert columnIndex > 0;
        assert columnIndex < 2;

        if (columnIndex == 0) {
            if (rowIndex == 0 || rows[rowIndex - 1].type != rows[rowIndex].type)
                return rows[rowIndex].desc();
            if (rows[rowIndex - 1].data.getClass() != rows[rowIndex].data.getClass())
                return rows[rowIndex].desc();
            return "";
        }

        return rows[rowIndex].data;
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) return false;
        if (typeOf(rowIndex) == Type.Reactions)
            return true;
        if (typeOf(rowIndex) == Type.Formula)
            return true;
        if (typeOf(rowIndex) == Type.Confidence)
            return false; // buggy for now
        if (typeOf(rowIndex) == Type.Abbreviation || typeOf(rowIndex) == Type.Name || typeOf(rowIndex) == Type.EditableAnnotation || typeOf(rowIndex) == Type.Xref)
            return true;
        return false;
    }

    @Override public int getRowCount() {
        return rows.length;
    }

    @Override public int getColumnCount() {
        return 2;
    }

    private static final class Row {
        private final Type   type;
        private final Object data;

        private Row(Type type, Object data) {
            this.type = type;
            this.data = data;
            assert data != null;
        }

        String desc() {
            return type.desc(data);
        }
    }

    public enum Type {
        Abbreviation {
            @Override String desc(Object value) {
                return "Abbreviation";
            }
        },
        Name {
            @Override String desc(Object value) {
                return "Name";
            }
        },
        Formula {
            @Override String desc(Object value) {
                return "Molecular formula";
            }
        },
        Confidence {
            @Override String desc(Object value) {
                return "Confidence";
            }
        },
        Compartments {
            @Override String desc(Object value) {
                return "Compartments";
            }
        },
        Reactions {
            @Override String desc(Object value) {
                return "Reactions";
            }
        },
        Xref {
            @Override String desc(Object value) {
                return "External databases";
            }
        },
        Annotation {
            @Override String desc(Object value) {
                assert value instanceof uk.ac.ebi.mdk.domain.annotation.Annotation;
                return ((Annotation) value).getBrief();
            }
        },
        EditableAnnotation {
            @Override String desc(Object value) {
                assert value instanceof uk.ac.ebi.mdk.domain.annotation.Annotation;
                return ((Annotation) value).getBrief();
            }
        },
        Unknown {
            @Override String desc(Object obj) {
                return "...";
            }
        };

        abstract String desc(Object obj);
    }
}
