/**
 * DocumentFactory.java
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
package uk.ac.ebi.search;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import uk.ac.ebi.core.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.Participant;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 *          DocumentFactory – 2011.09.28 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DocumentFactory {

    private static final Logger LOGGER = Logger.getLogger(DocumentFactory.class);


    public static Map<UUID, AnnotatedEntity> write(IndexWriter writer, Reconstruction recon)
            throws
            CorruptIndexException, IOException {

        Map<UUID, AnnotatedEntity> documents = new HashMap();

        for (Metabolite m : recon.getMetabolome()) {
            Document doc = getDocument(m);
            UUID uuid = UUID.randomUUID();
            doc.add(new Field("uuid", uuid.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            documents.put(uuid, m);
            writer.addDocument(doc);
        }
        for (MetabolicReaction rxn : recon.getReactome()) {
            Document doc = getDocument(rxn);
            UUID uuid = UUID.randomUUID();
            doc.add(new Field("uuid", uuid.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            documents.put(uuid, rxn);
            writer.addDocument(doc);
        }

        return documents;

    }


    /**
     * Returns a searchable lucene document for a metabolite
     * @param metabolite
     * @return
     */
    public static Document getDocument(MetabolicReaction reaction) {

        Document document = getDocument(new Document(),
                                        (AnnotatedEntity) reaction, true);

        // add metabolite data
        for (Participant<Metabolite, ?> p : reaction.getParticipants()) {
            document = getDocument(document, p.getMolecule(), false);
        }

        return document;

    }


    /**
     * Returns a searchable lucene document for a metabolite
     * @param metabolite
     * @return
     */
    public static Document getDocument(Metabolite metabolite) {

        Document entry = getDocument(new Document(),
                                     (AnnotatedEntity) metabolite, true);


        return entry;

    }


    /**
     * Returns a searchable lucene document for an AnnotatedEntity
     * @param entity
     * @param  root whether this is the route object (defines type)
     * @return
     */
    public static Document getDocument(Document document,
                                       AnnotatedEntity entity, boolean root) {


        document.add(new Field(FieldType.ACCESSION.getName(), entity.getAccession(),
                               Field.Store.YES,
                               Field.Index.ANALYZED));
        if (root) {
            String type = DefaultEntityFactory.getInstance().getRootClass(entity.getClass()).getSimpleName();
            document.add(new Field(FieldType.TYPE.getName(),
                                   type,
                                   Field.Store.YES,
                                   Field.Index.ANALYZED));
        }
        if (entity.getName() != null) {
            document.add(new Field(FieldType.NAME.getName(),
                                   entity.getName(),
                                   Field.Store.YES, Field.Index.ANALYZED));
        }
        if (entity.getAbbreviation() != null) {
            document.add(new Field(FieldType.ABBREVIATION.getName(),
                                   entity.getAbbreviation(),
                                   Field.Store.YES, Field.Index.ANALYZED));
        }
        // index annotations

        // xref
        for (Annotation annotation : entity.getAnnotations()) {
            document.add(new Field(FieldType.ANNOTATION.getName(), annotation.toString(), Field.Store.YES, Field.Index.ANALYZED));
        }


        return document;

    }
}
