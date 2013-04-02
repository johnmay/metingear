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

package uk.ac.ebi.metingear.view;

import uk.ac.ebi.caf.report.ReportManager;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.identifier.IdentifierFactory;
import uk.ac.ebi.mnb.interfaces.SelectionController;
import uk.ac.ebi.mnb.interfaces.TargetedUpdate;

import javax.swing.event.UndoableEditListener;


/**
 * @author johnmay
 */
public interface ControlDialog extends ProcessingDialog {


    public void setSelectionController(SelectionController controller);

    public void setUndoManager(UndoableEditListener undoManager);

    public void setReportManager(ReportManager report);

    public void setUpdateManager(TargetedUpdate update);

    public void setEntityFactory(EntityFactory entityFactory);

    public void setIdentifierFactory(IdentifierFactory identifierFactory);

    public void setAnnotationFactory(AnnotationFactory annotationFactory);

}
