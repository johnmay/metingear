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
package uk.ac.ebi.mnb.view.entity.tasks;

import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.tool.task.RunnableTask;
import uk.ac.ebi.mdk.tool.task.TaskStatus;
import uk.ac.ebi.mnb.core.TaskManager;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTableModel;
import uk.ac.ebi.mnb.view.entity.ColumnDescriptor;
import uk.ac.ebi.mnb.view.entity.DataType;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * TaskTableModel – 2011.09.28 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class TaskTableModel extends AbstractEntityTableModel {

    private static final Calendar CALENDAR = GregorianCalendar.getInstance();


    private static final ColumnDescriptor[] DEFAULT = new ColumnDescriptor[]{
            new ColumnDescriptor("Date", null,
                                 DataType.FIXED,
                                 Date.class),
            new ColumnDescriptor("Elapsed Time", null,
                                 DataType.FIXED,
                                 String.class),
            new ColumnDescriptor(
                    "Status", null,
                    DataType.FIXED,
                    TaskStatus.class)
    };

    public TaskTableModel() {
        super();
        addColumn(DEFAULT);
    }

    /** @inheridDoc */
    @Override
    public Collection<? extends AnnotatedEntity> getEntities() {

        TaskManager tm = TaskManager.getInstance();
        return tm.getTasks();

    }

    /** @inheridDoc */
    @Override
    public Object getFixedType(AnnotatedEntity entity, String name) {

        if (entity instanceof RunnableTask) {

            RunnableTask task = (RunnableTask) entity;
            if (name.equals("Date")) {
                return task.getStart();
            } else if (name.equals("Status")) {
                return task.getStatus();
            } else if (name.equals("Elapsed Time")) {
                long elapsed = task.elapsed();
                return String.format("%d min, %d sec",
                                     NANOSECONDS.toMinutes(elapsed),
                                     NANOSECONDS.toSeconds(elapsed)
                                             - MINUTES.toSeconds(NANOSECONDS.toMinutes(elapsed))
                                    );
            }
        }

        return "NA";

    }

    private final static long NANO_TO_SECONDS = 1000000000;
}
