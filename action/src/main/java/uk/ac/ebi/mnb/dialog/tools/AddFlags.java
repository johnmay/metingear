package uk.ac.ebi.mnb.dialog.tools;

import org.apache.log4j.Logger;
import uk.ac.ebi.mdk.domain.annotation.AnnotationFactory;
import uk.ac.ebi.mdk.domain.annotation.DefaultAnnotationFactory;
import uk.ac.ebi.mdk.domain.entity.AnnotatedEntity;
import uk.ac.ebi.mnb.core.ControllerAction;
import uk.ac.ebi.mnb.interfaces.MainController;

import java.awt.event.ActionEvent;

/**
 * @author John May
 */
public class AddFlags extends ControllerAction {

    private static final Logger LOGGER = Logger.getLogger(AddFlags.class);

    public AddFlags(MainController controller) {
        super(AddFlags.class.getSimpleName(), controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        AnnotationFactory factory = DefaultAnnotationFactory.getInstance();

        for (AnnotatedEntity entity : getSelection().getEntities()) {
            entity.addAnnotations(factory.getMatchingFlags(entity));
        }

        update(getSelection());

    }

}
