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
package uk.ac.ebi.mnb.view.entity.tasks;

import uk.ac.ebi.mnb.view.entity.AbstractEntityView;
import uk.ac.ebi.mnb.view.entity.AbstractEntityInspector;
import uk.ac.ebi.mnb.view.entity.AbstractEntityTable;
import org.apache.log4j.Logger;

/**
 *          TaskView – 2011.09.28 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class TaskView extends AbstractEntityView {

    private static final Logger LOGGER = Logger.getLogger(TaskView.class);

    public TaskView() {
        super("Tasks", new TaskTable(), new TaskInspector());
    }

    @Override
    public TaskTable getTable() {
        return (TaskTable) super.getTable();
    }

    @Override
    public TaskInspector getInspector() {
        return (TaskInspector) super.getInspector();
    }

    @Override
    public String getName() {
        return "Tasks";
    }
}
