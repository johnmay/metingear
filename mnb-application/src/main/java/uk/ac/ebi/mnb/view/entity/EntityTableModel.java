/**
 * EntityTableModel.java
 *
 * 2011.09.06
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
package uk.ac.ebi.mnb.view.entity;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import uk.ac.ebi.core.ReconstructionManager;
import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.render.source.AbbreviationAccessor;
import uk.ac.ebi.chemet.render.source.AccessionAccessor;
import uk.ac.ebi.chemet.render.source.Accessor;
import uk.ac.ebi.chemet.render.source.NameAccessor;
import uk.ac.ebi.core.Reconstruction;
import uk.ac.ebi.interfaces.AnnotatedEntity;

/**
 *          EntityTableModel – 2011.09.06 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public abstract class EntityTableModel
        extends AbstractTableModel {

    private static final Logger LOGGER = Logger.getLogger(EntityTableModel.class);
    private Reconstruction currentReconstruction;
    private ReconstructionManager pm = ReconstructionManager.getInstance();
    private List<ColumnDescriptor> columnDescriptors = new ArrayList();
    private List<? extends AnnotatedEntity> components = new ArrayList<AnnotatedEntity>();
    private static List<ColumnDescriptor> defaultColumns = new ArrayList(
            Arrays.asList(new ColumnDescriptor("Accession", String.class,
                                               DataType.BASIC, String.class),
                          new ColumnDescriptor("Abbreviation", String.class,
                                               DataType.BASIC, String.class),
                          new ColumnDescriptor("Name", String.class, DataType.BASIC,
                                               String.class)));
    private Map<String, Accessor> accessMap = new HashMap();

    public EntityTableModel() {
        this(defaultColumns);
        accessMap.put("Name", new NameAccessor());
        accessMap.put("Abbreviation", new AbbreviationAccessor());
        accessMap.put("Accession", new AccessionAccessor());
    }

    public EntityTableModel(List<ColumnDescriptor> columnDescriptors) {
        this.columnDescriptors.addAll(columnDescriptors);
        accessMap.put("Name", new NameAccessor());
        accessMap.put("Abbreviation", new AbbreviationAccessor());
        accessMap.put("Accession", new AccessionAccessor());
    }

    /**
     * Returns the default columns Accession, Abbreviation and Name
     */
    public static List<ColumnDescriptor> getDefaultColumns() {
        return defaultColumns;
    }

    /**
     * Returns the component and given index
     * @param index
     * @return
     */
    public AnnotatedEntity getEntity(int index) {
        return components.get(index);
    }
    private Object[][] data;

    /**
     *
     * Updates the underlying table-model
     *
     */
    public boolean update() {
        loadComponents();
        data = new Object[components.size()][getColumnCount()];
        long start = System.currentTimeMillis();
        for (int i = 0; i < components.size(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                data[i][j] = getValue(components.get(i), j);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Loaded table data: " + getClass() + " : " + (end - start) + " ms ");
        fireTableDataChanged();
        return true;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /*
     *
     * Method is called on update before cells are copied over to data[][]
     *
     */
    public abstract void loadComponents();

    public void setEntities(List<? extends AnnotatedEntity> components) {
        this.components = components;
    }

    private boolean displayNewReconstruction() {
        Reconstruction reconstruction = pm.getActiveReconstruction();
        return reconstruction instanceof Reconstruction && reconstruction != currentReconstruction;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnDescriptors.get(columnIndex).getDataClass();
    }

    @Override
    public String getColumnName(int column) {
        return columnDescriptors.get(column).getName();
    }

    public int getColumnCount() {
        return columnDescriptors.size();
    }

    public boolean addColumns(List<ColumnDescriptor> descriptors) {
        return columnDescriptors.addAll(descriptors);
    }

    public boolean addColumn(ColumnDescriptor... descriptors) {
        return columnDescriptors.addAll(Arrays.asList(descriptors));
    }

    public boolean addColumn(ColumnDescriptor descriptor) {
        return columnDescriptors.add(descriptor);
    }

    public DataType getAccessType(Integer column) {
        return columnDescriptors.get(column).getType();
    }

    public int getRowCount() {
        return components.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    public Object getValue(AnnotatedEntity entity,
                           Integer columnIndex) {

        DataType type = getAccessType(columnIndex);
        if (type == DataType.FIXED) {
            return getFixedType(entity, getColumnName(columnIndex));
        } else if (type == DataType.BASIC) {
            return getBasicInfo(entity, getColumnName(columnIndex));
        } else if (type == DataType.ANNOTATION) {
            return entity.getAnnotations(columnDescriptors.get(columnIndex).getAccessClass());
        } else if (type == DataType.OBSERVATION) {
            return null;
//            return entity.getObservationCollection().get(
//                    columnDescriptors.get(columnIndex).getAccessClass());
        }

        // will never happen...
        LOGGER.error("Unknown ColumnAccessType?");

        return "";

    }

    public abstract Object getFixedType(AnnotatedEntity component, String name);

    public Integer indexOf(AnnotatedEntity component) {
        return components.indexOf(component);
    }

    private Object getBasicInfo(AnnotatedEntity entity, String name) {

        Accessor accessor = accessMap.get(name);

        if (accessor != null) {
            return accessor.access(entity);
        }
        // maybe do this when os x has java 1.7
//        switch (name) {
//            case "Name":
//                return entity.getName();
//            case "Abbreviation":
//                return entity.getName();
//            case "Accession":
//                return entity.getName();
//            default:
//                return "NA";
//        }
//


        return "NA";

    }
}
