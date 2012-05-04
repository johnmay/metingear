
/**
 * LoadChebiNames.java
 *
 * 2011.09.13
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


import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import uk.ac.ebi.mdk.db.DatabaseProperties;


/**
 *          LoadChebiNames – 2011.09.13 <br>
 *          A test Class using lucene and chebi names
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class LoadChebiNames {

    private static final Logger LOGGER = Logger.getLogger(LoadChebiNames.class);
    private DatabaseProperties DB_PROPS = DatabaseProperties.getInstance();


    public LoadChebiNames() {

        if( DB_PROPS.containsKey("chebi.names") ) {
            load(DB_PROPS.getFile("chebi.names"));
        }
    }


    private static final int CHEBI_ID = 1;
    private static final int TYPE = 2;
    private static final int NAME = 4;


    private void load(File file) {


        Map<String, Document> map = new HashMap<String, Document>();

        try {
            CSVReader reader = new CSVReader(new FileReader(file), '\t');

            String[] header = reader.readNext();
            String[] row;
            while( (row = reader.readNext()) != null ) {

                String chebi = row[CHEBI_ID];
                if( !map.containsKey(chebi) ) {
                    map.put(chebi, new Document());
                }

                map.get(chebi).add(new Field(row[TYPE],
                                             row[NAME],
                                             Field.Store.YES,
                                             Field.Index.ANALYZED));

            }


            reader.close();


            StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
            Directory index = new RAMDirectory();
            IndexWriter w = new IndexWriter(index, analyzer, true,
                                            IndexWriter.MaxFieldLength.UNLIMITED);
            // add documents to index
            for( Document doc : map.values() ) {
//                w.addDocument(doc);
            }
            Document doc = new Document();
            doc.add(new Field("title", "leucene", Field.Store.YES, Field.Index.ANALYZED));
            w.close();

            Query q = new QueryParser(Version.LUCENE_CURRENT, "NAME", analyzer).parse("leucene");
            IndexSearcher searcher = new IndexSearcher(index, true);
            TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            System.out.println("Docs:");
            for( ScoreDoc scoreDoc : hits ) {
                System.out.println(searcher.doc(scoreDoc.doc));
            }



            // searcher can only be closed when there
            // is no need to access the documents any more.
            searcher.close();



        } catch( Exception ex ) {

            ex.printStackTrace();
        }


    }


    private static void addDoc(IndexWriter w, String value) throws IOException {
        Document doc = new Document();
        doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
        w.addDocument(doc);
    }


    public static void main(String[] args) {
        new LoadChebiNames();
    }


}

