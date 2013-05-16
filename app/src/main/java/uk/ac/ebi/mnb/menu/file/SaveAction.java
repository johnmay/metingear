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
package uk.ac.ebi.mnb.menu.file;

import net.sf.furbelow.SpinningDialWaitIndicator;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.action.GeneralAction;
import uk.ac.ebi.caf.utility.version.Version;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.collection.DefaultReconstructionManager;
import uk.ac.ebi.mdk.domain.entity.collection.ReconstructionManager;
import uk.ac.ebi.mdk.io.IOConstants;
import uk.ac.ebi.mnb.core.ErrorMessage;
import uk.ac.ebi.mnb.main.MainView;

import java.awt.event.ActionEvent;


/**
 * SaveAction 2012.01.31
 *
 * @author johnmay
 * @author $Author$ (this version)
 *
 *         Class description
 * @version $Rev$ : Last Changed $Date$
 */
public class SaveAction extends GeneralAction {

    private static final Logger LOGGER = Logger.getLogger(SaveAction.class);


    public SaveAction() {
        super("SaveProject");
    }


    public void actionPerformed(ActionEvent e) {

        ReconstructionManager manager = DefaultReconstructionManager.getInstance();
        final Reconstruction reconstruction = manager.active();


        if (!reconstruction.getContainer().exists())
            reconstruction.setContainer(reconstruction.defaultLocation());


        final SpinningDialWaitIndicator waitIndicator = new SpinningDialWaitIndicator(MainView.getInstance());
        waitIndicator.setText("Saving reconstruction to " + reconstruction.getContainer());
        final long start = System.currentTimeMillis();

        Thread t = new Thread(new Runnable() {
            @Override public void run() {


                try {
                    Version version = IOConstants.VERSION;
                    ReconstructionIOHelper.write(reconstruction, reconstruction.getContainer());
                    final long end = System.currentTimeMillis();
                    LOGGER.info("Wrote reconstruction in " + (end - start) + " ms");
                } catch (Exception ex) {
                    MainView.getInstance().getMessageManager().addReport(new ErrorMessage("Unable to save reconstruction: " + ex.getMessage()));
                    LOGGER.error(ex);
                    ex.printStackTrace();
                } finally {
                    // determine how long to sleep, if it took longer then 1900 ms since
                    // we displayed the wait indicator we don't sleep
                    long sleepFor = 2000 - (System.currentTimeMillis() - start);
                    try {
                        if(sleepFor > 100)
                            Thread.sleep(sleepFor);
                    } catch (InterruptedException e1) {
                        // doesn't matter
                    }
                    waitIndicator.dispose();
                }

            }
        });
        t.setName("save-reconstruction");
        t.start();
    }
}
