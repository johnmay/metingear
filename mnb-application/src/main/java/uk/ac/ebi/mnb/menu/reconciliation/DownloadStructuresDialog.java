
/**
 * DownloadStructuresDialog.java
 *
 * 2011.09.27
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.ebi.mnb.menu.reconciliation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import furbelow.SpinningDialWaitIndicator;
import furbelow.WaitIndicator;
import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.mnb.view.GeneralPanel;
import uk.ac.ebi.mnb.view.ViewUtils;
import uk.ac.ebi.mnb.view.labels.Label;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtomContainer;
import uk.ac.ebi.annotation.chemical.ChemicalStructure;
import uk.ac.ebi.annotation.crossreference.CrossReference;
import uk.ac.ebi.chebi.webapps.chebiWS.model.StarsCategory;
import uk.ac.ebi.chemet.ws.exceptions.MissingStructureException;
import uk.ac.ebi.chemet.ws.exceptions.UnfetchableEntry;
import uk.ac.ebi.interfaces.Annotation;
import uk.ac.ebi.interfaces.Identifier;
import uk.ac.ebi.core.AnnotatedEntity;
import uk.ac.ebi.metabolomes.webservices.ChEBIWebServiceConnection;
import uk.ac.ebi.metabolomes.webservices.KeggCompoundWebServiceConnection;
import uk.ac.ebi.mnb.main.MainFrame;
import uk.ac.ebi.mnb.view.AltPanel;
import uk.ac.ebi.mnb.view.CheckBox;
import uk.ac.ebi.resource.chemical.ChEBIIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;


/**
 *          DownloadStructuresDialog – 2011.09.27 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class DownloadStructuresDialog
  extends DropdownDialog {

    private static final Logger LOGGER = Logger.getLogger(DownloadStructuresDialog.class);
    private List<AnnotatedEntity> components;
    private ChEBIWebServiceConnection chebi;
    private KeggCompoundWebServiceConnection kegg;
    private JCheckBox chebiCheckBox;
    private JCheckBox keggCheckBox;
    private JCheckBox chebiAllStarCheckBox;


    public DownloadStructuresDialog() {
        super(MainFrame.getInstance(), MainFrame.getInstance(), "DownloadStructures");



        chebiCheckBox = new CheckBox("ChEBI (currated only)");
        keggCheckBox = new CheckBox("KEGG Compound");
        chebiAllStarCheckBox = new CheckBox("ChEBI (All)");


        layoutOptions();
    }


    private void layoutOptions() {
        setLayout(new FormLayout("10dlu, pref, 10dlu",
                                 "10dlu, pref, 2dlu, pref, 2dlu, pref, 4dlu, pref, 10dlu"));

        CellConstraints cc = new CellConstraints();

        // options
        JComponent selection = new AltPanel();

        selection.setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p"));
        selection.add(chebiCheckBox, cc.xy(1, 1));
        selection.add(chebiAllStarCheckBox, cc.xy(3, 1));
        selection.add(keggCheckBox, cc.xy(1, 3));

        // close and run buttons
        JComponent component = new AltPanel();
        component.setLayout(new FormLayout("left:p, pref:grow, right:p", "p"));
        component.add(getCloseButton(), cc.xy(1, 1));
        component.add(getRunButton(), cc.xy(3, 1));

        add(new Label("Please select which web services should be used"), cc.xy(2, 2));
        add(new JSeparator(JSeparator.HORIZONTAL), cc.xy(2, 4));
        add(selection, cc.xy(2, 6));
        add(component, cc.xy(2, 8));

    }


    public void setComponents(List<AnnotatedEntity> components) {
        this.components = components;
    }


    @Override
    public void process() {


        if( chebi == null ) {
            chebi = new ChEBIWebServiceConnection();
        }
        if( kegg == null ) {
            kegg = new KeggCompoundWebServiceConnection();
        }

        boolean useChEBI = chebiAllStarCheckBox.isSelected() || chebiCheckBox.isSelected();
        boolean useKEGG = keggCheckBox.isSelected();

        // set chebi filtering
        if( chebiAllStarCheckBox.isSelected() ) {
            chebi.setStarsCategory(StarsCategory.ALL);
        } else {
            chebi.setStarsCategory(StarsCategory.THREE_ONLY);
        }

        List<Identifier> problemIdentifiers = new ArrayList();

        for( AnnotatedEntity component : components ) {

            for( Annotation xref : component.getAnnotationsExtending(CrossReference.class) ) {

                Identifier id = ((CrossReference) xref).getIdentifier();
                if( useChEBI && id instanceof ChEBIIdentifier ) {
                    try {
                        IAtomContainer molecule = chebi.getAtomContainer(id.getAccession());
                        if( molecule != null ) {
                            component.addAnnotation(new ChemicalStructure(molecule));
                        } else {
                            problemIdentifiers.add(id);
                        }
                    } catch( Exception ex ) {
                        problemIdentifiers.add(id);
                    }
                } else if( useKEGG && id instanceof KEGGCompoundIdentifier ) {
                    IAtomContainer molecule;
                    try {
                        molecule = kegg.getAtomContainer(id.getAccession());
                        component.addAnnotation(new ChemicalStructure(molecule));
                    } catch( UnfetchableEntry ex ) {
                        problemIdentifiers.add(id);
                    } catch( MissingStructureException ex ) {
                        problemIdentifiers.add(id);
                    }
                }
            }
        }


        if( problemIdentifiers.isEmpty() == false ) {
            MainFrame.getInstance().addWarningMessage("Unable to download structure for; " +
                                                     StringUtils.join(problemIdentifiers, ", "));
        }


    }


    @Override
    public void update() {
        MainFrame.getInstance().getProjectPanel().update();
    }


}

