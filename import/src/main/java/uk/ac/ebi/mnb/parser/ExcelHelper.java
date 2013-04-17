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
package uk.ac.ebi.mnb.parser;

import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * @name    ExcelImporter
 * @date    2011.07.30
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   Importer for a metabolic model in Office XLS format
 *
 */
public abstract class ExcelHelper {

    private static final Logger LOGGER = Logger.getLogger( ExcelHelper.class );
    private Pattern reactionSheetPattern = Pattern.compile( "Reaction|Reconstruction|model" ,
                                                            Pattern.CASE_INSENSITIVE );
    private Pattern metaboliteSheetPattern = Pattern.compile( "Meta|compound|abbreviations" ,
                                                              Pattern.CASE_INSENSITIVE );
    private Pattern reactionOperatorPattern = Pattern.compile( "[<][-=]+[>]|[-=]*[><]" ,
                                                               Pattern.CASE_INSENSITIVE );
    protected Integer INTIAL_COLUMN_NUMBER = 4;

    protected Boolean nameMatchesReactionSheet( String sheetName ) {
        return reactionSheetPattern.matcher( sheetName ).find();
    }

    protected Boolean nameMatchesMetaboliteSheet( String sheetName ) {
        return metaboliteSheetPattern.matcher( sheetName ).find();
    }

    protected Boolean hasReactionOperator( String value ) {
        return reactionOperatorPattern.matcher( value ).find();
    }

    public abstract List<Integer> getReactionSheetIndices();

    public abstract List<Integer> getMetaboliteSheetIndices();

    public abstract List<String> getSheetNames();

    public abstract String[][] getSheetData(int index);

    /**
     *
     * @return
     */
    public abstract String[][] getTableHead( Integer sheetIndex , Integer rowCount );
}
