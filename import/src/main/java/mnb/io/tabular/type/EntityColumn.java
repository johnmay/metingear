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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.io.tabular.type;


/**
 *
 * @author johnmay
 */
public enum EntityColumn implements TableDescription {

    ABBREVIATION( "ent.col.abbreviation" ),
    DESCRIPTION( "ent.col.name" ),
    SYNONYMS( "ent.col.synonyms" ),
    FORMULA( "ent.col.formula" ),
    FORMULA_CHARGED( "ent.col.formula.charged" ),
    FORMULA_NEUTRAL( "ent.col.formula.neutral" ),
    CHARGE( "ent.col.charge" ),
    COMPARTMENT( "ent.col.compartment" ),
    KEGG_XREF( "ent.col.xref.kegg" ),
    CAS_XREF( "ent.col.xref.cas" ),
    CHEBI_XREF( "ent.col.xref.chebi" ),
    PUBCHEM_XREF( "ent.col.xref.pubchem" ),
    // data bounds

    DATA_BOUNDS( "ent.data.bounds" );
    // properties key for ExcelModelProperties
    private final String propertiesKey;


    private EntityColumn( String propertiesKey ) {
        this.propertiesKey = propertiesKey;
    }


    /**
     * @inheritDoc
     */
    public String getKey() {
        return propertiesKey;
    }


    public TableDescription getBounds() {
        return DATA_BOUNDS;
    }


}

