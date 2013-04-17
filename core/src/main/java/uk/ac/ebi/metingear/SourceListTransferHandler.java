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

package uk.ac.ebi.metingear;

import uk.ac.ebi.caf.utility.ResourceUtility;
import uk.ac.ebi.chemet.render.source.ReconstructionSourceItem;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.GeneProduct;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Handles drop events on on SourceList.
 *
 * @author John May
 */
public class SourceListTransferHandler extends TransferHandler {

    @Override
    public boolean canImport(TransferSupport support) {
        support.setShowDropLocation(true);
        return super.canImport(support);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

        for (DataFlavor flavor : transferFlavors) {
            if (TransferableEntity.dataFlavor().equals(flavor)) {
                JTree tree = (JTree) comp;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                        .getSelectionPath().getLastPathComponent();
                return node.getUserObject() instanceof ReconstructionSourceItem;
            }
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override public boolean importData(JComponent comp, Transferable t) {
        JTree tree = (JTree) comp;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                .getSelectionPath().getLastPathComponent();
        if (node.getUserObject() instanceof ReconstructionSourceItem) {
            ReconstructionSourceItem item = (ReconstructionSourceItem) node
                    .getUserObject();
            try {
                TransferableEntity entity = (TransferableEntity) t
                        .getTransferData(TransferableEntity.dataFlavor());
                Reconstruction recon = item.getEntity();
                if (recon != entity.reconstruction()) {
                    for (AnnotatedEntity e : entity.entities()) {
                        if (e instanceof Metabolite) {
                            recon.addMetabolite((Metabolite) e);
                        } else if (e instanceof MetabolicReaction) {
                            recon.addReaction((MetabolicReaction) e);
                        } else if (e instanceof GeneProduct) {
                            recon.addProduct((GeneProduct) e);
                        }
                    }
                    // genes... we need the chromosome etc.
                }
                return true;
            } catch (UnsupportedFlavorException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return false;
    }
}
