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

package uk.ac.ebi.metingear.tools.structure;

import net.sf.jniinchi.INCHI_RET;
import org.apache.log4j.Logger;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.smiles.SmilesGenerator;
import uk.ac.ebi.mdk.ui.render.molecule.MoleculeRenderer;

import javax.imageio.ImageIO;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for copying an {@link IAtomContainer} to the clipboard
 * (copy/paste).
 *
 * @author John May
 */
final class StructureClipboard {

    /**
     * Copy the isomeric smiles (non-canonical) to the system clipboard.
     *
     * @param container the structure
     * @return the SMILES was generated and copied
     */
    public static boolean copyAsIsoSmiles(IAtomContainer container) {
        if (container == null)
            return false;
        try {
            String smi = SmilesGenerator.isomeric().create(container);
            return setClipboard(smi);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    /**
     * Copy the unique smiles (canonical, no-stereo) to the system clipboard.
     *
     * @param container the structure
     * @return the SMILES was generated and copied
     */
    public static boolean copyAsUSmiles(IAtomContainer container) {
        if (container == null)
            return false;
        try {
            String smi = SmilesGenerator.unique().create(container);
            return setClipboard(smi);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    /**
     * Copy the InChI to the system clipboard.
     *
     * @param container the structure
     * @return the InChI was generated and copied
     */
    public static boolean copyAsInChI(IAtomContainer container) {
        if (container == null)
            return false;
        try {
            InChIGeneratorFactory inchiFactory = InChIGeneratorFactory.getInstance();
            inchiFactory.setIgnoreAromaticBonds(true);
            InChIGenerator generator = inchiFactory.getInChIGenerator(container);
            // can use as bit masks?
            if (generator.getReturnStatus() == INCHI_RET.OKAY
                    || generator.getReturnStatus() == INCHI_RET.OKAY) {
                return setClipboard(generator.getInchi());
            }
        } catch (Exception e) {
            log(e);
        }
        return false;
    }

    /**
     * Copy as a Molfile (V2000).
     *
     * @param container the structure
     * @return the Molfile was generated and copied
     */
    public static boolean copyAsMolfile(IAtomContainer container) {
        if (container == null)
            return false;
        try {
            StringWriter sw = new StringWriter();
            new MDLV2000Writer(sw).write(container);
            return setClipboard(sw.toString());
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    /**
     * Copy a PNG depiction to the system clipboard. The PNG is stored to a
     * temporary file.
     *
     * @param container the structure
     * @return the InChI was generated and copied
     */
    public static boolean copyAsPng(IAtomContainer container, int size) {
        if (container == null)
            return false;
        try {
            final BufferedImage img = MoleculeRenderer.getInstance().getImage(container, size);
            final File f = File.createTempFile("PastedGraphic", ".png");
            if (f.canWrite()) {
                ImageIO.write(img, "png", f);
                try {
                    Class.forName("javafx.scene.input.Clipboard"); // is JavaFX available
                    setFXClipboard(f);
                } catch (ClassNotFoundException e) {
                    // doesn't work on OS X - but should on other platforms?
                    setClipboard(f);
                }
                return true;
            }
        } catch (Exception e) {
            log(e);
        }
        return false;
    }

    /**
     * High-level class - we just dump all exceptions in the logger and indicate
     * the data was not copied.
     *
     * @param e exception
     */
    private static void log(Exception e) {
        Logger.getLogger(StructureClipboard.class).error(e);

    }

    /**
     * Set the string on the clipboard using AWT.
     *
     * @param str the string to set
     */
    private static boolean setClipboard(String str) {
        if (str == null)
            return false;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(str), null);
        return true;
    }

    /**
     * Set the file in the clipboard using AWT.
     *
     * @param f a file
     */
    private static void setClipboard(final File f) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new FileSelection(f), null);
    }

    /**
     * Set the file in the clipboard using JavaFX - this clipboard seems to work
     * better (at least on OS X).
     *
     * @param f a file
     */
    private static void setFXClipboard(final File f) {
        // TODO: difficulties building with maven
//        new javafx.embed.swing.JFXPanel(); // init javafx
//        javafx.application.Platform.runLater(new Runnable() {
//            @Override public void run() {
//                javafx.scene.input.Clipboard.getSystemClipboard().setContent(Collections.<javafx.scene.input.DataFormat, Object>singletonMap(javafx.scene.input.DataFormat.FILES, Arrays.asList(f)));
//            }
//        });
    }

    private static class FileSelection implements Transferable {

        private final List<File> files;

        private FileSelection(File f) {
            this.files = Collections.singletonList(f);
        }

        @Override public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
        }

        @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.javaFileListFlavor.equals(flavor);
        }

        @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (DataFlavor.javaFileListFlavor.equals(flavor))
                return files;
            throw new IllegalArgumentException("unsupported flavour type");
        }
    }
}
