/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 * MNBFileFilter.java
 *
 *
 * @author johnmay
 * @date Apr 15, 2011
 */
public class ProjectFilter extends FileFilter {

    @Override
    public boolean accept(File f) {

        if( ! f.isDirectory() ) {
            return false;
        }

        String path = f.getPath();
        int lastIndex = path.lastIndexOf(".");
        if( lastIndex != -1 ) {
            String extension = path.substring(lastIndex);
            if( extension.equals(".mnb") ) {
                return true;
            }
        }

        return false;

    }


    @Override
    public String getDescription() {
        return "MNB Project";
    }


}

