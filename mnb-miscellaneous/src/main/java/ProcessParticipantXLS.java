
/**
 * ProcessParticipantXLS.java
 *
 * 2011.10.21
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import uk.ac.ebi.mnb.parser.ExcelXLSHelper;

/**
 *          ProcessParticipantXLS - 2011.10.21 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ProcessParticipantXLS {

    private static final Logger LOGGER = Logger.getLogger(ProcessParticipantXLS.class);

    public static void main(String[] args) throws IOException {
        ExcelXLSHelper excelXLSHelper = new ExcelXLSHelper(new FileInputStream(new File("/Users/johnmay/EBI/Symposium/abstract-book/resource/participants.xls")));
        String[][] rows = excelXLSHelper.getSheetData(0);

        //  CSVWriter csv = new CSVWriter(new FileWriter(new File("/Users/johnmay/EBI/Symposium/abstract-book/resource/participants.tsv")), '\t', '\0');
        int index = 0;
        for (String[] row : rows) {
            String code = row[0];
            String firstname = row[4];
            String lastname = row[5];
            String company = row[10].replaceAll("&", "\\\\&");
            String country = row[11];
            String email = row[12];
            String type = row[43];
            String title = row[34].replaceAll("&", "\\\\&");

            if (type.contains("Poster")) {
                type = "poster";
            } else if (type.contains("Oral")) {
                type = "oral-presentation";
            }

            String abstractText = clean(row[35]);
            File f = new File("/Users/johnmay/EBI/Symposium/abstract-book/resource/", "riffraff/" + code + ".tex");
            FileWriter fos = new FileWriter(f);
            fos.write(abstractText);
            fos.close();


            // csv.writeNext(new String[]{code, firstname, lastname, company, country, email, type, title, f.getName()});

        }

        //  csv.close();

    }

    public static String clean(String str) {

        str = Pattern.compile("\\{.+?\\}", Pattern.DOTALL).matcher(str).replaceAll("");

        str = str.replaceAll("&#916;", "\\$\\\\Delta\\$");
        str = str.replaceAll("&#945;", "\\$\\\\alpha\\$");
        str = str.replaceAll("&#946;", "\\$\\\\beta\\$");
        str = str.replaceAll("&#949;", "\\$\\\\epsilon\\$");
        str = str.replaceAll("&#954;", "\\$\\\\kappa\\$");
        str = str.replaceAll("&#176;", "\\$^{\\\\circ}\\$");
        str = str.replaceAll("&amp;", "\\\\&");
        str = str.replaceAll("&#8222;", "``"); // german quotes
        str = str.replaceAll("\"", "''");
        str = str.replaceAll("&#8220;", "''");
        str = str.replaceAll("&#181;", "\\$\\\\mu\\$");
        str = str.replaceAll("&#956;", "\\$\\\\mu\\$");
        str = str.replaceAll("&#8221;", "''");
        str = str.replaceAll("&#8710;", "");
        str = str.replaceAll("&nbsp;", "");
        str = str.replaceAll("&#8211;", "-");
        str = str.replaceAll("&#252;", "\\\\\"u");
        str = str.replaceAll("&#228;", "\\\\\"a");
        str = str.replaceAll("&#8216;", "`");
        str = str.replaceAll("&#8217;", "'");
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&gt;", ">");







        return str;
    }
}
