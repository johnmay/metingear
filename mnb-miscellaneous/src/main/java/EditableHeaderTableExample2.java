
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import uk.ac.ebi.mnb.view.table.ComboRenderer;
import uk.ac.ebi.mnb.view.table.SelectableHeader;
import uk.ac.ebi.mnb.view.table.SelectableHeaderTableColumn;

/**
 * @version 1.0 08/22/99
 */
public class EditableHeaderTableExample2 extends JFrame {

    public EditableHeaderTableExample2() {
        super( "EditableHeader Example" );

        JTable table = new JTable( 7 , 5 );
        TableColumnModel columnModel = table.getColumnModel();
        table.setTableHeader( new SelectableHeader( columnModel ) );

        String[] items = { "Dog" , "Cat" };
        JComboBox combo = new JComboBox( items );
        ComboRenderer renderer = new ComboRenderer( items );

        SelectableHeaderTableColumn col;
        // column 1
        col = ( SelectableHeaderTableColumn ) table.getColumnModel().getColumn( 1 );
        col.setHeaderValue( combo.getItemAt( 0 ) );
        col.setHeaderRenderer( renderer );
        col.setHeaderEditor( new DefaultCellEditor( combo ) );

        JScrollPane pane = new JScrollPane( table );
        getContentPane().add( pane );
    }

    public static void main( String[] args ) {
        EditableHeaderTableExample2 frame = new EditableHeaderTableExample2();
        frame.addWindowListener( new WindowAdapter() {

            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        frame.setSize( 300 , 100 );
        frame.setVisible( true );
    }
}
