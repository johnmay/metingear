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

    private ProjectFilter mnbprojectFileFilter;
    private FastaFileFilter fastaFileFilter;

    private FileFilterManager() {
        mnbprojectFileFilter = new ProjectFilter();
        fastaFileFilter = new FastaFileFilter();
    }

    public static FileFilterManager getInstance() {
        return FileFilterManagerHolder.INSTANCE;
    }

    private static class FileFilterManagerHolder {
        private static final FileFilterManager INSTANCE = new FileFilterManager();
    }

    public ProjectFilter getProjectFilter() {
        return mnbprojectFileFilter;
    }

    public FastaFileFilter getFastaFilter() {
        return fastaFileFilter;
    }

 }
