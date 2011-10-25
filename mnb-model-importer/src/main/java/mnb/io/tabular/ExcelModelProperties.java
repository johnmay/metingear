/**
 * ExcelModelProperties.java
 *
 * 2011.08.30
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
package mnb.io.tabular;

import mnb.io.tabular.type.*;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import mnb.io.tabular.util.ExcelUtilities;
import org.apache.log4j.Logger;

/**
 *          ExcelModelProperties – 2011.08.30 <br>
 *          Wrapper for a properties class which provides fixed keys for column types
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class ExcelModelProperties extends Properties {

    private static final Logger LOGGER = Logger.getLogger(ExcelModelProperties.class);
    /**
     * Which sheets contain the reactions and metabolites
     */
    public static final String REACTION_SHEET = "rxn.sheet";
    public static final String METABOLITE_SHEET = "ent.sheet";

    public ExcelModelProperties() {
    }

    public ExcelModelProperties(Properties defaults) {
        super(defaults);
    }

    public void setFile(File f){
        setProperty("reconstruction.file.path", f.getAbsolutePath());
        setProperty("reconstruction.file.name", f.getName().replaceAll(" ", "_"));
    }

    public String getFilePath() {
        return getProperty("reconstruction.file.path");
    }
    public String getFileName() {
        return getProperty("reconstruction.file.name");
    }

    public String getPreferenceKey(TableDescription col) {
        return getPreferenceKey(col.getKey());
    }

    public String getPreferenceKey(String key) {
        return getFileName() + "." + key;
    }

    public List<TableDescription> getDefinedColumns(Class<? extends TableDescription> clazz) {

        List<TableDescription> rxnColumns = new ArrayList<TableDescription>();
        for (TableDescription c : clazz.getEnumConstants()) {
            if (isDefined(c)) {
                rxnColumns.add(c);
            }
        }

        return rxnColumns;

    }

    /**
     *
     * Access a column index for a standard column type (see. public class variables)
     *
     * @param key TableDescription e.g. EntityColumn or ReactionColumn the value in the properties
     *            file can be defined either as a programatic index (0...n) or Excel column
     *            (A, B .. AZ)
     *
     * @return    Index of that column as provided by the properties. null is returned if no key is
     *            found or value is empty)
     *
     */
    public Integer getColumnIndex(TableDescription columnType) {


        if (isDefined(columnType.getKey())) {

            String value = super.getProperty(columnType.getKey());
            Integer index = null;

            try {

                // first try as integer
                index = Integer.parseInt(value);

            } catch (NumberFormatException ex) {

                // then try as excel index
                index = ExcelUtilities.stringToIndex(value);
            }

            return index;
        }

        return null;

    }

    private boolean isDefined(TableDescription c) {
        return isDefined(c.getKey());
    }

    /*
     *
     * Checks whether the column key exists and that it is not null
     *
     * @param key The key (one of the public static strings of this class)
     * @return Whether the properties provide the column location
     *
     */
    public Boolean isDefined(String key) {
        return super.containsKey(key) && super.getProperty(key) != null;
    }

    /**
     *
     * Returns the data bounds ({@see getDataIndices(String key)}) of the data as a {@see Rectangle}
     * instance. The indices differ from the indicies in that the width and height are provided instead
     * of the raw indices. Therefore x2 = x1 + width and y2 = y1 + height
     *
     * @param key REACTION_DATA_BOUNDS, ENTITY_DATA_BOUNDS, etc.
     *
     * @return Rectangle instance providing the x1, y2, width and height of the data bounds
     *
     */
    public Rectangle getDataBounds(String key) {

        Integer[][] indices = this.getDataIndices(key);

        return new Rectangle(indices[0][0], indices[0][1],
                             indices[1][0] - indices[0][0], indices[1][1] - indices[0][1]);

    }

    /**
     *
     * Returns the index of a bounds type property. The format should be "START:END" e.g. A1:F10 means
     * excel cells A1 to F10. Translated into programming indices (0,0 to 5,9).
     *
     * @param key REACTION_DATA_BOUNDS, ENTITY_DATA_BOUNDS, etc.
     *
     * @return 2D array of indicies x1 = [0,0] y1 = [0,1] x2 = [1,0] y2 = [1,1]
     *
     */
    public Integer[][] getDataIndices(String key) {

        if (isDefined(key)) {
            String[] bounds = super.getProperty(key).split(":");
            return new Integer[][]{
                        ExcelUtilities.getIndices(bounds[0]),
                        ExcelUtilities.getIndices(bounds[1])
                    };
        }

        throw new UnsupportedOperationException("No bounds specified in properties");

        // todo: throw more meaningful exception

    }

    public static ExcelModelProperties createTemplate() {

        ExcelModelProperties properties = new ExcelModelProperties();

        for (TableDescription type : EntityColumn.values()) {
            properties.put(type.getKey(), "");
        }
        for (TableDescription type : ReactionColumn.values()) {
            properties.put(type.getKey(), "");
        }
        for (String type : Arrays.asList(REACTION_SHEET, METABOLITE_SHEET)) {
            properties.put(type, "");
        }


        return properties;

    }

    public static void main(String[] args) {
        try {
            createTemplate().storeToXML(new FileOutputStream(
                    "/Users/johnmay/Desktop/model.properites"),
                                        "Template Model Properties File");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
