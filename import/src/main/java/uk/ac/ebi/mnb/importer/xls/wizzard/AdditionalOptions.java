/*
 * Copyright (c) 2013. EMBL, European Bioinformatics Institute
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
package uk.ac.ebi.mnb.importer.xls.wizzard;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import mnb.io.tabular.ExcelModelProperties;
import org.apache.log4j.Logger;
import uk.ac.ebi.caf.component.factory.LabelFactory;

import javax.swing.*;


/**
 * ImportPanel – 2011.09.26 <br> Class description
 *
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 */
public class AdditionalOptions
        extends JPanel
        implements WizzardStage {

    private static final Logger LOGGER = Logger.getLogger(AdditionalOptions.class);

    private ExcelModelProperties properties;

    public AdditionalOptions(ExcelModelProperties properties) {
        this.properties = properties;
        init();
    }


    private void init() {
        setLayout(new FormLayout("p, 4dlu, p", "p, 4dlu, p, 4dlu, p"));
        CellConstraints cc = new CellConstraints();

        add(LabelFactory.newLabel("Please click 'Next' and then 'Okay' if you are ready to import the reconstruction"), cc.xy(1, 1));

    }


    public Boolean updateSelection() {

        return true;
    }


    public void reloadPanel() {
        init();
    }

    private JProgressBar bar = new JProgressBar();


    public JProgressBar getProgressBar() {
        return bar;
    }


    public String getDescription() {
        return "<html>Please confirm additional options</html>";
    }
}
