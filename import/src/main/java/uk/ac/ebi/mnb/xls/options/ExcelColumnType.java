/**
 * ExcelColumnType.java
 *
 * 2011.08.04
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
package uk.ac.ebi.mnb.xls.options;

/**
 * @name    ExcelColumnType
 * @date    2011.08.04
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 * @brief   This class stores the types of columns that can be parsed
 *          from an Excel sheet
 *
 */
public enum ExcelColumnType {

    ABBREVIATION( "Abbreviation" ),
    DESCRIPTOR( "Descriptor" ),
    FORMULA( "Formula" ),
    EQUATION( "Equation" ),
    GENE( "Gene" ),
    LOCUS( "Locus" ),
    PROTEIN( "Protein" ),
    FLUX( "Flux" ),
    FLUX_LOW( "Flux Lower Bound" ),
    FLUX_HIGH( "Flux Upper Bound" );
    private String name;

    private ExcelColumnType( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String[] getColumnNames() {
        ExcelColumnType[] types = values();
        String[] names = new String[ types.length ];
        for ( int i = 0; i < types.length; i++ ) {
            names[i] = types[i].getName();
        }
        return names;
    }
}
