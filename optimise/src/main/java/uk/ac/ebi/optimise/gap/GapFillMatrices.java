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

package uk.ac.ebi.optimise.gap;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import org.apache.commons.cli.Option;
import uk.ac.ebi.mdk.apps.CommandLineMain;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.metabolite.CompartmentalisedMetabolite;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.DefaultStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mdk.io.ReactionMatrixIO;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** @author John May */
public class GapFillMatrices extends CommandLineMain {

    public static void main(String[] args) {
        new GapFillMatrices().process(args);
    }

    @Override public void setupOptions() {
        add(new Option("m", "model", true, "stiochiometric matrix of the model"));
        add(new Option("d", "database", true, "stiochiometric matrix of the database"));
    }

    @Override public void process() {

        System.out.println("reading model and database...");
        
        StoichiometricMatrix<String, String> model    = loadMatrix("m");
        StoichiometricMatrix<String, String> database = loadMatrix("d");
        System.out.println("done");

        SimulationUtil.setup();

        // mapping of a compartmentalised metabolite expressed as a string to
        // a compartment
        Function<String,Compartment> functor = new Function<String, Compartment>() {
            @Override public Compartment apply(String s) {
                if (s.contains("[c]"))
                    return Organelle.CYTOPLASM;
                if (s.contains("[e]"))
                    return Organelle.EXTRACELLULAR;
                return null;
            }
        };
        
        try {
            NpFind<String, String> gapFind = new NpFind<String, String>(model, functor);
            Set<String> nps = gapFind.solve();
            System.out.println(nps.size());
            System.out.println(Joiner.on("\n").join(nps));

            GapFill<String, String> gapfill = new GapFill<String, String>(model, model, functor);
            
            Set<String> candidates = new HashSet<String>();
            
            for (String np : nps) {
                int index = model.getIndex(np);
//                List<String> reactions = gapfill.getCandidateReactions(index);
//                System.out.println(np + ": " + reactions);
//                candidates.addAll(reactions);
            }

            System.out.println(candidates);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    private StoichiometricMatrix<String, String> loadMatrix(String param) {
        InputStream in = null;
        try {
            return ReactionMatrixIO.readCompressedBasicStoichiometricMatrix(new FileInputStream(getFile(param)), BasicStoichiometricMatrix.create(2000, 2000));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // ignored
            }
        }
        return null;
    }

    private Reconstruction loadRecon(String param) {
        try {
            return ReconstructionIOHelper.read(getFile(param));
        } catch (Exception e) {
            return null;
        }
    }

}
