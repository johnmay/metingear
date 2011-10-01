/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mnb.view.old;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * FileSystemModel.java
 *
 *
 * @author johnmay
 * @date Apr 8, 2011
 */
public class FileSystemModel implements TreeModel {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( FileSystemModel.class );
    private File root;
    private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private VisibileFileFilter fileFilter = new VisibileFileFilter();

    public FileSystemModel( File rootDirectory ) {
        root = rootDirectory;
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild( Object parent , int index ) {
        File directory = ( File ) parent;

        String[] children = directory.list( fileFilter );

        return new TreeFile( directory , children[index] );
    }

    public int getChildCount( Object parent ) {
        File file = ( File ) parent;
        if ( file.isDirectory() ) {
            String[] fileList = file.list(fileFilter);
            if ( fileList != null ) {
                return fileList.length;
            }
        }
        return 0;
    }

    public boolean isLeaf( Object node ) {
        File file = ( File ) node;
        return file.isFile();
    }

    public int getIndexOfChild( Object parent , Object child ) {
        File directory = ( File ) parent;
        File file = ( File ) child;
        String[] children = directory.list();
        for ( int i = 0; i < children.length; i++ ) {
            if ( file.getName().equals( children[i] ) ) {
                return i;
            }
        }
        return -1;

    }

    public void valueForPathChanged( TreePath path , Object value ) {
        File oldFile = ( File ) path.getLastPathComponent();
        String fileParentPath = oldFile.getParent();
        String newFileName = ( String ) value;
        File targetFile = new File( fileParentPath , newFileName );
        oldFile.renameTo( targetFile );
        File parent = new File( fileParentPath );
        int[] changedChildrenIndices = { getIndexOfChild( parent , targetFile ) };
        Object[] changedChildren = { targetFile };
        fireTreeNodesChanged( path.getParentPath() , changedChildrenIndices , changedChildren );

    }

    private void fireTreeNodesChanged( TreePath parentPath , int[] indices , Object[] children ) {
        TreeModelEvent event = new TreeModelEvent( this , parentPath , indices , children );
        Iterator iterator = listeners.iterator();
        TreeModelListener listener = null;
        while ( iterator.hasNext() ) {
            listener = ( TreeModelListener ) iterator.next();
            listener.treeNodesChanged( event );
        }
    }

    public void addTreeModelListener( TreeModelListener listener ) {
        listeners.add( listener );
    }

    public void removeTreeModelListener( TreeModelListener listener ) {
        listeners.remove( listener );
    }

    private class TreeFile extends File {

        public TreeFile( File parent , String child ) {
            super( parent , child );
        }

        public String toString() {
            return getName();
        }
    }

    private class VisibileFileFilter implements FilenameFilter {

        public boolean accept( File dir , String name ) {
            return name.startsWith( "." ) ? false : true;
        }
    }
}
