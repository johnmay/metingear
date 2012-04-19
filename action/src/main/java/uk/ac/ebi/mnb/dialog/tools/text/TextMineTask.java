package uk.ac.ebi.mnb.dialog.tools.text;

import org.apache.log4j.Logger;
import uk.ac.ebi.chemet.io.external.RunnableTask;
import uk.ac.ebi.chemet.resource.basic.TaskIdentifier;
import uk.ac.ebi.interfaces.entities.Entity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$
 */
public class TextMineTask extends RunnableTask {

    private static final Logger LOGGER = Logger.getLogger(TextMineTask.class);

    public TextMineTask() {
        super(new TaskIdentifier(), "rxn-txt", "Reaction Mining");
    }

    @Override
    public void prerun() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void postrun() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Entity newInstance() {
        return new TextMineTask();
    }

    @Override
    public void run() {

        try {                                                 desc.bbk
            InputStream in = getClass().getResourceAsStream("/desc/bbkREx/PubMedReader.xml");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        //Initialise the PubMed reader
        //        XMLInputSource reader = new XMLInputSource("src/main/resources/desc/bbkREx/PubMedReader.xml");
        //        CollectionReaderDescription crDesc = UIMAFramework.getXMLParser().parseCollectionReaderDescription(reader);
        //        ConfigurationParameterSettings crParams = crDesc.getMetaData().getConfigurationParameterSettings();
        //        crParams.setParameterValue("query", "11669627");
        //        crParams.setParameterValue("maxReturn", 10);
        //        CollectionReader cr = UIMAFramework.produceCollectionReader(crDesc);


    }


    public static void main(String[] args) {
        new TextMineTask().run();
    }
}
