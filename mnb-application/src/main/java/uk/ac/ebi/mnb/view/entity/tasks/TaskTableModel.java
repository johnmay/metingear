
/**
 * TaskTableModel.java
 *
 * 2011.09.28
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
package uk.ac.ebi.mnb.view.entity.tasks;

import java.util.Date;
import uk.ac.ebi.mnb.view.entity.DataType;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.EntityTableModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParamType;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.interfaces.AnnotatedEntity;
import uk.ac.ebi.metabolomes.run.TaskStatus;
import uk.ac.ebi.mnb.core.TaskManager;


/**
 *          TaskTableModel – 2011.09.28 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class TaskTableModel extends EntityTableModel {

    private static final Logger LOGGER = Logger.getLogger(TaskTableModel.class);
    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
        new ColumnDescriptor("Description", null,
                             DataType.FIXED,
                             String.class),
        new ColumnDescriptor("Job Id", null,
                             DataType.FIXED,
                             String.class),
        new ColumnDescriptor("Date", null,
                             DataType.FIXED,
                             Date.class),
        new ColumnDescriptor("Status", null,
                             DataType.FIXED,
                             TaskStatus.class)
    };


    public TaskTableModel() {
        super();
        addColumn(DEFAULT);
    }


    /**
     * @inheridDoc
     */
    @Override
    public void loadComponents() {

        TaskManager tm = TaskManager.getInstance();
        setEntities(tm.getTasks());

    }


    /**
     * @inheridDoc
     */
    @Override
    public Object getFixedType(AnnotatedEntity entity, String name) {

        if( entity instanceof RunnableTask ) {

            RunnableTask task = (RunnableTask) entity;

            if( name.equals("Description") ) {
                return task.getName();
            } else if( name.equals("Job Id") ) {
                return task.getAccession();
            } else if( name.equals("Date") ) {
                return task.getOptions().getInitialisationDate();
            } else if( name.equals("Status") ) {
                return task.getStatus();
            }
        }

        return "NA";

    }


}

