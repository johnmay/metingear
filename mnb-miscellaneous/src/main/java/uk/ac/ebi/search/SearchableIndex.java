/**
 * SearchableIndex.java
 *
 * 2011.09.29
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
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *          SearchableIndex – 2011.09.29 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class SearchableIndex {

    private static final Logger LOGGER = Logger.getLogger(SearchableIndex.class);
    private final Directory index;
    private final Map<UUID, AnnotatedEntity> map;

    public SearchableIndex(Directory index, Map map) {
        this.index = index;
        this.map = map;
    }

    public Map<UUID, AnnotatedEntity> getMap() {
        return map;
    }

    public Directory getIndex() {
        return index;
    }

    public List<AnnotatedEntity> getRankedEntities(Query query) throws IOException {
        return getRankedEntities(query, 25);
    }

    public List<AnnotatedEntity> getRankedEntities(Query query, Integer number) throws IOException {

        return getRankedEntities(query, number, AnnotatedEntity.class);

    }

    public List<AnnotatedEntity> getRankedEntities(Query query, Integer number, Class filter) throws IOException {

        IndexSearcher searcher = new IndexSearcher(index, true);
        TopDocsCollector collector = TopScoreDocCollector.create(number * 4, true); // * 4 to get more qureires. The number is for all queries when in fact we only want a certain number
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        List<AnnotatedEntity> entities = new ArrayList<AnnotatedEntity>();

        for (ScoreDoc scoreDoc : hits) {
            UUID uuid = UUID.fromString(searcher.doc(scoreDoc.doc).get("uuid"));
            if (map.containsKey(uuid)) {
                AnnotatedEntity entity = map.get(uuid);
                if (filter.isInstance(entity)) {
                    entities.add(entity);
                    // reached limit
                    if(entities.size() == number){
                        return entities;
                    }
                }
            } else {
                LOGGER.error("Null object in UUID -> Entity map");
            }
        }

        return entities;

    }

    public void close() throws IOException {
        index.close();
    }
}
