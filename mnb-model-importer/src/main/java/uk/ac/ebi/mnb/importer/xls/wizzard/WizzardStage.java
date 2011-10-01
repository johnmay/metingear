/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.mnb.importer.xls.wizzard;

/**
 *
 * @author johnmay
 */
public interface WizzardStage {
    public Boolean updateSelection();
    // called when options are updated
    public void reloadPanel();

}
