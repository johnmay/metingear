/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * FastaFileFilter.java
 *
 *
 * @author johnmay
 * @date Apr 18, 2011
 */
public class FastaFileFilter extends FileFilter {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FastaFileFilter.class );

    @Override
    public boolean accept( File f ) {
        String path = f.getPath();
        int lastIndex = path.lastIndexOf( "." );
        if ( lastIndex != -1 ) {
            String extension = path.substring( lastIndex );
            if ( extension.matches( ".fa|.fasta|.faa|.fna" )  ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Fasta Files (.fa, .fasta, .faa, .fna)";
    }



}
