
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import mnb.view.old.TaskManager;
import uk.ac.ebi.mnb.view.entity.ColumnAccessType;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.EntityTableModel;
import org.apache.log4j.Logger;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.metabolomes.descriptor.observation.JobParamType;
import uk.ac.ebi.metabolomes.run.RunnableTask;
import uk.ac.ebi.metabolomes.run.TaskStatus;


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
                             ColumnAccessType.FIXED,
                             String.class),
        new ColumnDescriptor("Job Id", null,
                             ColumnAccessType.FIXED,
                             String.class),
        new ColumnDescriptor("Date", null,
                             ColumnAccessType.FIXED,
                             Date.class),
        new ColumnDescriptor("Status", null,
                             ColumnAccessType.FIXED,
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
                return task.getTaskDescription();
            } else if( name.equals("Job Id") ) {
                return task.getJobParameters().get(JobParamType.JOBID);
            } else if( name.equals("Date") ) {
                return task.getJobParameters().get(JobParamType.DATE);
            } else if( name.equals("Status") ) {
                return task.getStatus();
            }
        }

        return "NA";

    }


}

