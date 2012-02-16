/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.io;

import java.io.FileFilter;

/**
 *
 * @author johnmay
 * @date   Apr 18, 2011
 */
public class FileFilterManager {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FileFilterManager.class );

    private ProjectFilter mnb;
    private FastaFileFilter fasta;

    private FileFilterManager() {
        mnb = new ProjectFilter();
        fasta = new FastaFileFilter();
    }

    public static FileFilterManager getInstance() {
        return FileFilterManagerHolder.INSTANCE;
    }

    private static class FileFilterManagerHolder {
        private static final FileFilterManager INSTANCE = new FileFilterManager();
    }

    public ProjectFilter getProjectFilter() {
        return mnb;
    }

    public FastaFileFilter getFastaFilter() {
        return fasta;
    }

 }
