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

