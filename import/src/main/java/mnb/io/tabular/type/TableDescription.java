/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.io.tabular.type;


/**
 *
 * @author johnmay
 */
public interface TableDescription {

    /**
     *
     * Returns the key for the properties file
     *
     * @return
     */
    public String getKey();


    /**
     *
     * Return the enumeration for the bounds
     *
     * @return
     */
    public TableDescription getBounds();

}

