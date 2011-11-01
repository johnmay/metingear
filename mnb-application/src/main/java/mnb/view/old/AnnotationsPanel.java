/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mnb.view.old;

import com.google.common.collect.Iterables;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.util.Collection;
import javax.swing.JPanel;
import uk.ac.ebi.visualisation.ViewUtils;
import uk.ac.ebi.mnb.view.labels.BoldLabel;
import uk.ac.ebi.mnb.view.labels.MLabel;
import uk.ac.ebi.interfaces.Annotation;


/**
 * HomologyObservations.java
 *
 *
 * @author johnmay
 * @date Apr 29, 2011
 */
public class AnnotationsPanel
  extends JPanel {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(
      AnnotationsPanel.class);
    private static final long serialVersionUID = 1725701519795079585L;
    public Collection<Annotation> annotations;


    public AnnotationsPanel() {
        add(new BoldLabel("Annotations"));
        setBackground(Color.WHITE);
    }


    public void setAnnotations(Collection<Annotation> annotations) {
        this.annotations = annotations;
        layoutAnnotations();
    }


    public void layoutAnnotations() {
        removeAll();
        if( annotations == null ) {
            return;
        }

        int nrow = annotations.size();
        FormLayout layout = new FormLayout("right:80dlu, 4dlu, left:150dlu", ViewUtils.
          goodiesFormHelper(nrow + 1, 4, false));
        setLayout(layout);

        CellConstraints cc = new CellConstraints();
        for( int i = 0 ; i < nrow ; i++ ) {
            Annotation annotation = Iterables.get(annotations, i);
            int ccy = (i == 0) ? 1 : i * 2 + 1;
            //add(new ActionLabel(annotation.toString() + ":", new HighLightObservations(annotation)),  cc.xy(1, ccy));
            add(new MLabel(annotation.toString()), cc.xy(3, ccy));
        }
        //  add( new JSeparator( JSeparator.HORIZONTAL ) , cc.xyw( 1 , ( ( nrow == 0 ) ? 1 : nrow * 2 + 1 ) , 3 ) );

    }


}

