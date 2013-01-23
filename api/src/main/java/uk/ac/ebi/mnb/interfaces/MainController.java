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
package uk.ac.ebi.mnb.interfaces;

import uk.ac.ebi.caf.report.ReportManager;

import javax.swing.undo.UndoManager;

/**
 * @name    MainController - 2011.10.03 <br>
 *          Interface description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public interface MainController extends TargetedUpdate {

    public ReportManager getMessageManager();

    public ViewController getViewController();

    public DialogController getDialogController();

    public UndoManager getUndoManager();

    public void updateMenuContext();

}
