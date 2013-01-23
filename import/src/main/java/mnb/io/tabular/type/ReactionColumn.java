/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.io.tabular.type;


/**
 *
 * @author johnmay
 */
public enum ReactionColumn implements TableDescription {

    ABBREVIATION( "rxn.col.identifier" ),
    DESCRIPTION( "rxn.col.description" ),
    EQUATION( "rxn.col.equation" ),
    DIRECTION( "rxn.col.direction" ),
    GENE( "rxn.col.gene" ),
    PROTEIN( "rxn.col.protein" ),
    LOCUS( "rxn.col.locus" ),
    SUBSYSTEM( "rxn.col.subsystem" ), // e.g. Trasport, Glycolysis
    CLASSIFICATION( "rxn.col.classification" ), // EC/TCDB code
    SOURCE( "rxn.col.source" ), // db, reference or stage (e.g. gap filling)
    KEGG_XREF( "rxn.col.xref.kegg" ), // db, reference or stage (e.g. gap filling)

    // flux values are somtimes in a different sheet but sometimes in the same as reaction
    // perhaps we could specify a key e.g. sheet/column for all data types
    FLUX( "rxn.col.flux" ),
    MIN_FLUX( "rxn.col.minflux" ),
    MAX_FLUX( "rxn.col.maxflux" ),

    // extra
    SYNONYMS("rxn.col.synonyms"),
    FREE_ENERGY("rxn.col.deltag"),
    FREE_ENERGY_ERROR("rxn.col.deltag.error"),


    //
    DATA_BOUNDS( "rxn.data.bounds" );
    // properties key for ExcelModelProperties
    private final String propertiesKey;


    private ReactionColumn( String propertiesKey ) {
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

