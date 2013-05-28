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

import com.google.common.base.Joiner;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author John May
 */
public final class TransferableEntity implements Transferable {

    private static DataFlavor INTERNAL_FLAVOR;

    static {
        try {
            INTERNAL_FLAVOR = new DataFlavor(
                DataFlavor.javaJVMLocalObjectMimeType + ";class=uk.ac.ebi.metingear.TransferableEntity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final Reconstruction              recon;
    private final Collection<AnnotatedEntity> entities;

    public TransferableEntity(Reconstruction recon, Collection<AnnotatedEntity> entities) {
        this.recon = recon;
        this.entities = new ArrayList<AnnotatedEntity>(entities);
    }

    public Reconstruction reconstruction() {
        return recon;
    }

    public Collection<AnnotatedEntity> entities() {
        return entities;
    }

    public static DataFlavor dataFlavor() {
        return INTERNAL_FLAVOR;
    }

    @Override public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{INTERNAL_FLAVOR, DataFlavor.stringFlavor};
    }

    @Override public boolean isDataFlavorSupported(DataFlavor flavor) {
        return INTERNAL_FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
    }

    @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,
                                                               IOException {
        if(DataFlavor.stringFlavor.equals(flavor)){
            return Joiner.on(", ").join(entities);
        }
        // transferring our-selves
        return this;
    }
}
