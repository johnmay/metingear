package uk.ac.ebi.mnb.dialog.edit;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.Annotation;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * AssignFlags - 21.03.2012 <br/>
 * <p/>
 * Automatically add flags to the model
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class AssignFlags extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AssignFlags.class);

    public AssignFlags(String command, MainController controller) {
        super(command, controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        DefaultAnnotationFactory factory = DefaultAnnotationFactory.getInstance();

        Collection<AnnotatedEntity> entities = getSelection().getEntities();
        for(AnnotatedEntity entity : entities){
            for(Annotation flag : factory.getMatchingFlags(entity)){
                entity.addAnnotation(flag);
            }
        }

        update(getSelection());

    }

}
