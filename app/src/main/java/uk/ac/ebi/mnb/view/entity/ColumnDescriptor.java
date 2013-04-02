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
package uk.ac.ebi.mnb.view.entity;

import org.apache.log4j.Logger;
import uk.ac.ebi.edit.entity.FieldManager;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;

import javax.swing.undo.UndoableEdit;

/**
 *          ColumnDescriptor – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ColumnDescriptor {

    private static final Logger LOGGER = Logger.getLogger(ColumnDescriptor.class);
    private String name;
    private Class accessClass;
    private DataType accessType;
    private Class dataClass;
    private Object instance;
    private boolean editable;
    private FieldManager setter;

    public ColumnDescriptor(Annotation annotation) {
        this.name = annotation.getShortDescription();
        this.accessClass = annotation.getClass();
        this.accessType = DataType.ANNOTATION;
        this.dataClass = annotation.getClass();
        this.instance = annotation;
    }

    public ColumnDescriptor(String name,
                            Class accessClass,
                            DataType type,
                            Class dataClass) {
        this(name, accessClass, type, dataClass, false);
    }

    public ColumnDescriptor(String name,
                            Class accessClass,
                            DataType type,
                            Class dataClass,
                            boolean editable) {
        this.name = name;
        this.accessClass = accessClass;
        this.accessType = type;
        this.dataClass = dataClass;
        this.editable = editable;
    }

    public ColumnDescriptor(String name,
                            Class accessClass,
                            DataType type,
                            Class dataClass,
                            FieldManager setter) {
        this.name = name;
        this.accessClass = accessClass;
        this.accessType = type;
        this.dataClass = dataClass;
        this.setter = setter;
        this.editable = true; // have a setter
    }

    public boolean hasSetter() {
        return setter != null;
    }

    public UndoableEdit getUndoableEdit(AnnotatedEntity entity, Object value) {
        return setter.getUndoableEdit(entity, value);
    }

    public boolean set(AnnotatedEntity entity, Object value) {
        return setter.set(entity, value);
    }

    public Class getAccessClass() {
        return accessClass;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return accessType;
    }

    public Class getDataClass() {
        return dataClass;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Column: " + accessClass;
    }

    public boolean isEditable() {
        return editable;
    }
}
