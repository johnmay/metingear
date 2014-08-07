/*
 * Copyright (c) 2014. EMBL, European Bioinformatics Institute
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.ebi.metingear.view;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import uk.ac.ebi.caf.component.factory.ButtonFactory;
import uk.ac.ebi.caf.utility.font.EBIIcon;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.annotation.rex.RExCompound;
import uk.ac.ebi.mdk.domain.annotation.rex.RExExtract;
import uk.ac.ebi.mdk.domain.annotation.rex.RExTag;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.mdk.domain.annotation.rex.RExTag.Type.ACTION;
import static uk.ac.ebi.mdk.domain.annotation.rex.RExTag.Type.MODIFIER;
import static uk.ac.ebi.mdk.domain.annotation.rex.RExTag.Type.PRODUCT;
import static uk.ac.ebi.mdk.domain.annotation.rex.RExTag.Type.SUBSTRATE;

/**
 * A widget for displaying LIMET / REx annotations.
 *
 * @author John May
 */
public class LIMETViewWidget {

    View       view;
    Controller controller;
    Model model = new Model(Collections.<RExExtract>emptyList(), Collections.<RExCompound>emptyList());

    public LIMETViewWidget(Model model) {
        this.model = model;
        this.controller = new Controller(model);
        this.view = new View(this.controller, this.model);
        controller.addView(view);
        view.update();
    }

    public JComponent component() {
        return view.component();
    }

    private static final class Controller {
        Model model;
        List<View> views = new ArrayList<View>();

        private Controller(Model model) {
            this.model = model;
        }

        void next() {
            model.position++;
            updateViews();
        }

        void prev() {
            model.position--;
            updateViews();
        }

        void updateViews() {
            for (View view : views)
                view.update();
        }

        void addView(View view) {
            views.add(view);
        }
    }

    private static final class View {
        JComponent         extractPanel;
        RExCompoundDisplay compoundPanel;
        JComponent         component;
        JButton            prev, next;
        JTextPane  extractPane;
        Controller controller;
        Model      model;
        JLabel organismLabel = new JLabel(), seedMetabolites = new JLabel(), sourceLabel = new JLabel();
        private final Map<RExTag.Type, Style> style = new HashMap<RExTag.Type, Style>();

        private View(final Controller controller, Model modelParam) {
            this.controller = controller;
            this.model = modelParam;

            this.component = Box.createHorizontalBox();
            this.extractPanel = Box.createVerticalBox();
            this.compoundPanel = new RExCompoundDisplay();
            component.setOpaque(true);
            component.setBackground(Color.WHITE);
            component.add(extractPanel);
            component.add(compoundPanel.component());

            Box topBar = Box.createHorizontalBox();
            Box btmBar = Box.createHorizontalBox();

            this.prev = transparentButton(EBIIcon.PREVIOUS.create().color(Color.BLACK).icon());
            this.next = transparentButton(EBIIcon.NEXT.create().color(Color.BLACK).icon());
            this.extractPane = new JTextPane();

            topBar.add(organismLabel);
            topBar.add(Box.createHorizontalGlue());
            topBar.add(sourceLabel);
            topBar.add(Box.createHorizontalGlue());
            topBar.add(seedMetabolites);

            btmBar.add(prev);
            btmBar.add(Box.createHorizontalGlue());
            btmBar.add(next);

            topBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            extractPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            btmBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            extractPane.setEditable(false);
            extractPane.setBackground(Color.WHITE);
            style.put(ACTION, extractPane.addStyle("action", null));
            style.put(SUBSTRATE, extractPane.addStyle("participant", null));
            style.put(PRODUCT, style.get(SUBSTRATE));
            style.put(MODIFIER, extractPane.addStyle("modifier", null));

            StyleConstants.setBackground(style.get(ACTION), new Color(255, 200, 200));
            StyleConstants.setBackground(style.get(PRODUCT), new Color(200, 255, 200));
            StyleConstants.setBackground(style.get(MODIFIER), new Color(200, 200, 255));

            extractPanel.add(topBar);
            extractPanel.add(extractPane);
            extractPanel.add(btmBar);

            prev.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.prev();
                }
            });
            next.addActionListener(new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    controller.next();
                }
            });

            extractPane.addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int i = extractPane.viewToModel(e.getPoint());
                    RExExtract extract = model.selected();
                    for (RExTag tag : extract.tags()) {
                        if (i >= tag.start() && i <= (tag.start() + tag.length())) {
                            for (RExCompound compound : model.compounds) {
                                if (compound.getID().equals(tag.id())) {
                                    compoundPanel.update(compound);
                                }
                            }
                        }
                    }
                }
            });


        }

        JComponent component() {
            return component;
        }

        void clear() {
            extractPane.setText("");
        }

        void update() {
            if (model.extracts.isEmpty()) {
                clear();
            }
            else {
                RExExtract extract = model.selected();

                if (extract.isInCorrectOrganism()) {
                    organismLabel.setText("Correct organism");
                }
                else {
                    organismLabel.setText("");
                }

                if (extract.source() != null) {
                    sourceLabel.setText(extract.source().getAccession());
                }
                else {
                    sourceLabel.setText("");
                }

                if (extract.totalSeedMetabolitesInSource() == 0) {
                    seedMetabolites.setText("No seed metabolites");
                }
                else if (extract.totalSeedMetabolitesInSource() == 1) {
                    seedMetabolites.setText("1 seed metabolite");
                }
                else if (extract.totalSeedMetabolitesInSource() > 1) {
                    seedMetabolites.setText(extract.totalSeedMetabolitesInSource() + " seed metabolites");
                }

                // update sentence display
                extractPane.setText(extract.sentence());

                StyledDocument doc = extractPane.getStyledDocument();
                for (final RExTag tag : extract.tags()) {
                    doc.setCharacterAttributes(tag.start(), tag.length(), style.get(tag.type()), true);
                }
            }
        }

        private static JButton transparentButton(ImageIcon icon) {
            JButton button = ButtonFactory.newCleanButton(icon, new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) {
                    // will be set elsewhere
                }
            });
            button.setOpaque(false);
            return button;
        }
    }

    private static final class Model {

        private final List<RExExtract>  extracts;
        private final List<RExCompound> compounds;
        private int position = 0;

        private Model(List<RExExtract> extracts, List<RExCompound> compounds) {
            this.extracts = extracts;
            this.compounds = compounds;
        }

        int index() {
            if (extracts.isEmpty()) return 0;
            return Math.abs(position % extracts.size());
        }

        RExExtract selected() {
            if (extracts.isEmpty())
                return null;
            return extracts.get(index());
        }

        static Model createForReaction(MetabolicReaction reaction) {
            List<RExExtract> extracts = new ArrayList<RExExtract>(reaction.getAnnotations(RExExtract.class));
            List<RExCompound> compounds = new ArrayList<RExCompound>(reaction.getAnnotations(RExCompound.class));
            return new Model(extracts, compounds);
        }
    }

    private static final class RExCompoundDisplay {
        JPanel      panel     = new JPanel(new FormLayout("p, 4dlu, p", "p, p, p"));
        JLabel      idLabel   = new JLabel();
        JLabel      idValue   = new JLabel();
        JLabel      typeLabel = new JLabel();
        JLabel      typeValue = new JLabel();
        RExCompound current   = null;
        

        private RExCompoundDisplay() {
            idLabel.setText("Id");
            idValue.setText("...");
            typeLabel.setText("Type");
            typeValue.setText("...");
            CellConstraints cc = new CellConstraints();
            panel.add(idLabel, cc.xy(1, 1));
            panel.add(idValue, cc.xy(3, 1));
            panel.add(typeLabel, cc.xy(1, 3));
            panel.add(typeValue, cc.xy(3, 3));
            
            idLabel.setOpaque(true);
            typeLabel.setOpaque(true);
            idLabel.setBackground(Color.BLACK);
            typeLabel.setBackground(Color.BLACK);
            idLabel.setForeground(Color.WHITE);
            typeLabel.setForeground(Color.WHITE);
        }

        JComponent component() {
            return panel;
        }

        void update(RExCompound compound) {
            if (current == compound) return;
            current = compound;
            idValue.setText(compound.getID());
            typeValue.setText(compound.getType().toString());
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Reconstruction reconstruction = ReconstructionIOHelper.read(new File("/Users/johnmay/Desktop/iSpa385.mr"));
        for (MetabolicReaction reaction : reconstruction.reactome()) {
            if (reaction.getAbbreviation().equals("_000000162")) {
                final JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);
                frame.setSize(256, 256);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                LIMETViewWidget widget = new LIMETViewWidget(Model.createForReaction(reaction));
                frame.add(widget.component());
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        frame.setVisible(true);
                    }
                });
                break;
            }
        }
    }
}
