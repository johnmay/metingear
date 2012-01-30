package mnb.io.resolve;

/**
 * CandidateSelector.java
 *
 * 2011.10.31
 *
 * This file is part of the CheMet library
 *
 * The CheMet library is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * CheMet is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet. If not, see <http://www.gnu.org/licenses/>.
 */
import com.google.common.base.Joiner;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.openscience.cdk.Element;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import uk.ac.ebi.annotation.Synonym;
import uk.ac.ebi.annotation.chemical.MolecularFormula;
import uk.ac.ebi.annotation.crossreference.ChEBICrossReference;
import uk.ac.ebi.annotation.crossreference.KEGGCrossReference;
import uk.ac.ebi.chemet.render.components.MatchIndication;
import uk.ac.ebi.core.Metabolite;
import uk.ac.ebi.io.service.ChEBIChemicalDataService;
import uk.ac.ebi.metabolomes.webservices.util.CandidateEntry;
import uk.ac.ebi.metabolomes.webservices.util.SynonymCandidateEntry;
import uk.ac.ebi.mnb.core.ExpandableComponentGroup;
import uk.ac.ebi.mnb.view.BorderlessScrollPane;
import uk.ac.ebi.mnb.view.DropdownDialog;
import uk.ac.ebi.caf.component.factory.PanelFactory;
import uk.ac.ebi.render.reconciliation.modules.GoogleSearch;
import uk.ac.ebi.render.reconciliation.modules.PeptideGenerator;
import uk.ac.ebi.resource.chemical.ChEBIIdentifier;
import uk.ac.ebi.resource.chemical.KEGGCompoundIdentifier;
import uk.ac.ebi.render.molecule.MoleculeTable;
import uk.ac.ebi.visualisation.molecule.access.CrossReferenceAccessor;
import uk.ac.ebi.visualisation.molecule.access.NameAccessor;


/**
 * CandidateSelector - 2011.10.31 <br> A dialog to select candidate
 * reconciliation entries
 *
 * @version $Rev$ : Last Changed $Date: 2011-12-13 16:43:11 +0000 (Tue,
 * 13 Dec 2011) $
 * @author johnmay
 * @author $Author$ (this version)
 */
public class CandidateSelector
        extends DropdownDialog
        implements ListSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(CandidateSelector.class);

    private MoleculeTable table = new MoleculeTable(new CrossReferenceAccessor(), new NameAccessor());

    private CellConstraints cc = new CellConstraints();

    private JLabel desc;

    private JLabel nameLabel = new JLabel();

    private JLabel matchLabel = new JLabel();

    private String name;

    private boolean selected = false;

    private JPanel options;

    private boolean skipall = false;


    public CandidateSelector(JFrame frame) {
        super(frame, "OkayDialog");
        setDefaultLayout();
        getClose().setText("Skip");
        table.getSelectionModel().addListSelectionListener(this);
    }

    private MatchIndication nameMatch = new MatchIndication(200, 200);

    private MatchIndication formulaMatch = new MatchIndication(200, 200);

    private MatchIndication chargeMatch = new MatchIndication(200, 200);

    private Map<Metabolite, CandidateEntry> map = new HashMap();

    private Metabolite query;


    public void setup(Metabolite query,
                      Collection<SynonymCandidateEntry> candidates) {

        this.query = query;

        String descText = String.format("The molecule '%s' was not found:", query.getName());

        desc.setText(descText);
        desc.setToolTipText(descText);

        this.nameMatch.setLeft(query.getName());
        this.nameMatch.setRight("");
        this.nameMatch.setDifference("");

        Collection<MolecularFormula> thismfs = query.getAnnotationsExtending(MolecularFormula.class);
        this.formulaMatch.setLeft(thismfs.isEmpty() ? "" : Joiner.on(",").join(thismfs));
        this.formulaMatch.setDifference("");
        this.formulaMatch.setRight("");

        this.chargeMatch.setLeft(query.getCharge().toString());
        this.chargeMatch.setDifference("");
        this.chargeMatch.setRight("");

        List<Metabolite> tmp = new ArrayList();

        map.clear();
        for (SynonymCandidateEntry candidate : candidates) {

            String accession = candidate.getId();
            Metabolite m = new Metabolite("", "", candidate.getDescription());

            m.setName(candidate.getDesc());

            if (accession.startsWith("ChEBI") || accession.startsWith("CHEBI")) {
                ChEBIIdentifier id = new ChEBIIdentifier(accession);
                m.addAnnotation(new ChEBICrossReference(id));

                Double c = ChEBIChemicalDataService.getInstance().getCharge(id);
                if (c != null) {
                    m.setCharge(c);
                } else {
                    LOGGER.debug("null charge for: " + id);
                }

                Collection<IMolecularFormula> mfs = ChEBIChemicalDataService.getInstance().getFormulas(id);
                if (mfs.isEmpty()) {
                    LOGGER.debug("No molecularformula for " + id);
                }
                for (IMolecularFormula mf : mfs) {
                    m.addAnnotation(new MolecularFormula(mf));
                }

            } else if (accession.startsWith("C")) {
                m.addAnnotation(new KEGGCrossReference(new KEGGCompoundIdentifier(accession)));
            } else {
                throw new UnsupportedOperationException("Need to add new identifier!");
            }

            for (String synonym : candidate.getSynonyms()) {
                m.addAnnotation(new Synonym(synonym));
            }

            map.put(m, candidate);
            tmp.add(m);

        }

        this.table.getModel().set(tmp);

    }


    @Override
    public JLabel getDescription() {
        desc = super.getDescription();
        desc.setPreferredSize(new Dimension(500, 16));
        desc.setText(String.format("The molecule '%s' was not found:", name));
        return desc;
    }


    @Override
    public JPanel getOptions() {

        options = super.getOptions();
        options.setLayout(new FormLayout("p:grow, 4dlu, p, 4dlu, p:grow",
                                         "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"));

        nameLabel.setText(name);
        matchLabel.setText("?");

        nameMatch.setLeft(name);
        nameMatch.setDifference("?");
        nameMatch.setRight("...");

        nameLabel.setPreferredSize(new Dimension(32, 100));
        matchLabel.setPreferredSize(new Dimension(32, 100));

        options.add(nameMatch.getComponent(), cc.xyw(1, 1, 5));
        options.add(formulaMatch.getComponent(), cc.xyw(1, 3, 5));
        options.add(chargeMatch.getComponent(), cc.xyw(1, 5, 5));

        Box tablePanel = Box.createVerticalBox();
        tablePanel.add(table.getTableHeader());
        tablePanel.add(table);

        options.add(new ExpandableComponentGroup("Suggested Matches", tablePanel), cc.xyw(1, 7, 5));

        final PeptideGenerator pg = new PeptideGenerator();
        GoogleSearch gs = new GoogleSearch();

        JComponent pgwrapper = PanelFactory.createDialogPanel("p, 4dlu, min", "p");
        pgwrapper.add(pg.getComponent(), cc.xy(1, 1));
        pgwrapper.add(new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                pg.transferAnnotations();
            }
        }), cc.xy(3, 1));

        options.add(new ExpandableComponentGroup(pg.getDescription(), pg.getComponent()), cc.xyw(1, 9, 5));
        options.add(new ExpandableComponentGroup(gs.getDescription(), gs.getComponent()), cc.xyw(1, 11, 5));

        JPanel panel = PanelFactory.createDialogPanel();

        JScrollPane pane = new BorderlessScrollPane(options);

        pane.getViewport().setBackground(panel.getBackground());
        pane.setPreferredSize(new Dimension(500, 400));

        panel.add(pane);

        return panel;

    }


    @Override
    public JPanel getNavigation() {

        JPanel navigation = super.getNavigation();

        FormLayout layout = (FormLayout) navigation.getLayout();

        layout.insertColumn(1, new ColumnSpec(ColumnSpec.LEFT, Sizes.MINIMUM, ColumnSpec.NO_GROW));
        layout.insertColumn(2, new ColumnSpec(ColumnSpec.LEFT, Sizes.PREFERRED, ColumnSpec.DEFAULT_GROW));

        navigation.add(new JButton(new AbstractAction("Skip All") {

            public void actionPerformed(ActionEvent e) {
                skipall = true;
                setVisible(false);
            }
        }), cc.xy(1, 1));

        return navigation;

    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            selected = false; // reset on each showing
        }
        if (visible && !skipall) {
            super.setVisible(visible);
        } else if (!visible) {
            super.setVisible(visible);
        }
    }


    @Override
    public void process() {
        // do nothing
        selected = true;
    }


    public boolean okaySelected() {
        return selected;
    }


    public Collection<Metabolite> getSelected() {
        return table.getSelectedEntities();
    }


    @Override
    public boolean update() {
        // do nothing
        return true;
    }


    public void valueChanged(ListSelectionEvent e) {
        Collection<Metabolite> selection = getSelected();
        if (selection.iterator().hasNext()) {
            try {
                Metabolite m = selection.iterator().next();
                nameMatch.setRight(m.getName());
                Integer diff = map.containsKey(m) ? map.get(m).getDistance() : 100;
                nameMatch.setDifference(diff.toString());
                nameMatch.setQuality(diff <= 2 ? MatchIndication.Quality.Good
                                     : diff <= 5 ? MatchIndication.Quality.Okay
                                       : MatchIndication.Quality.Bad);

                Collection<MolecularFormula> mfs = m.getAnnotationsExtending(MolecularFormula.class);
                formulaMatch.setRight(mfs.isEmpty() ? "" : Joiner.on(", ").join(mfs));
                formulaMatch.setQuality(MatchIndication.Quality.Bad);

                Isotope hydrogen = new Isotope(new Element("H"));

                for (MolecularFormula mf : query.getAnnotationsExtending(MolecularFormula.class)) {
                    for (MolecularFormula mfo : mfs) {
                        if (MolecularFormulaManipulator.compare(mf.getFormula(), mfo.getFormula())) {
                            formulaMatch.setQuality(MatchIndication.Quality.Good);
                        } else {
                            IMolecularFormula mf1 = mf.getFormula();
                            IMolecularFormula mf2 = mfo.getFormula();

                            int mf1hc = MolecularFormulaManipulator.getElementCount(mf1, hydrogen);
                            int mf2hc = MolecularFormulaManipulator.getElementCount(mf2, hydrogen);

                            mf1 = MolecularFormulaManipulator.removeElement(mf1, hydrogen);
                            mf2 = MolecularFormulaManipulator.removeElement(mf2, hydrogen);


                            if (MolecularFormulaManipulator.compare(mf1, mf2)) {
                                formulaMatch.setQuality(MatchIndication.Quality.Okay);
                            }

                            mf1.addIsotope(hydrogen, mf1hc);
                            mf2.addIsotope(hydrogen, mf2hc);

                        }
                    }
                }

                chargeMatch.setRight(m.getCharge().toString());
                double chDiff = Math.abs(query.getCharge() - m.getCharge());
                chargeMatch.setQuality(chDiff < 1 ? MatchIndication.Quality.Good
                                       : chDiff < 2 ? MatchIndication.Quality.Okay
                                         : MatchIndication.Quality.Bad);

                nameMatch.getComponent().repaint();
                formulaMatch.getComponent().repaint();
                chargeMatch.getComponent().repaint();

                nameMatch.getComponent().revalidate();
                formulaMatch.getComponent().revalidate();
                chargeMatch.getComponent().revalidate();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(CandidateSelector.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }
}
