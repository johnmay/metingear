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
import org.apache.commons.cli.Option;
import uk.ac.ebi.mdk.apps.CommandLineMain;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mdk.io.ReactionMatrixIO;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/** @author John May */
public class GapFillMatrices extends CommandLineMain {

    public static void main(String[] args) {
        new GapFillMatrices().process(args);
    }

    @Override public void setupOptions() {
        add(new Option("m", "model", true, "stiochiometric matrix of the model"));
        add(new Option("d", "database", true, "stiochiometric matrix of the database"));
        add(new Option("mr", "model-recon", true, "model reconstruction"));
        add(new Option("dr", "database-recon", true, "database reconstruction"));
        add(new Option("i", "interactive", false, "run in interactive mode"));
        add(new Option("s", "start", true, "start from this problem metabolite"));
        add(new Option("a", "adj", false, "only check reactions adjacent to existing Non-production metabolites"));
    }

    @Override public void process() {

        System.out.println("reading model and database stiochiometric matrices...");
        StoichiometricMatrix<String, String> modelS = loadMatrix("m");
        StoichiometricMatrix<String, String> databaseS = loadMatrix("d");
        System.out.println("done");
        System.out.println("reading model and database reconstructions...");
        Reconstruction model = loadRecon("mr");
        Reconstruction database = loadRecon("dr");
        System.out.println("done");

        SimulationUtil.setup();

        // mapping of a compartmentalised metabolite expressed as a string to
        // a compartment
        Function<String, Compartment> functor = new Function<String, Compartment>() {
            @Override public Compartment apply(String s) {
                if (s.contains("[c]"))
                    return Organelle.CYTOPLASM;
                if (s.contains("[e]"))
                    return Organelle.EXTRACELLULAR;
                return null;
            }
        };

        

        try {
            NpFind<String, String> gapFind = new NpFind<String, String>(modelS, functor);

            String[] nps = gapFind.solve().toArray(new String[0]);
            Metabolite[] ms = new Metabolite[nps.length];

            GapFill<String, String> gapFill = new GapFill<String, String>(databaseS, modelS, functor);
            if (has("adj")) gapFill.onlyAddAdjacent();

            System.out.println(nps.length + " non-production metabolites");
            for (int i = 0; i < nps.length; i++) {
                String np = nps[i];
                Collection<Metabolite> metabolites = model.metabolome().ofName(np.substring(0, np.lastIndexOf(' ')));
                if (metabolites.size() > 1 || metabolites.isEmpty()) {
                    System.err.println(np + " could be any of " + metabolites);
                }
                else {
                    Metabolite m = metabolites.iterator().next();
                    ms[i] = m;
                    System.out.println("[" + i + "] : " + m.getAbbreviation() + " " + np);
                }
            }

            if (has("i")) {
                String line = null;
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter a number [0-" + nps.length + "]: ");
                while (!(line = scanner.nextLine()).equals("q")) {

                    try {
                        int index = Integer.parseInt(line);
                        if (index >= nps.length && index < 0)
                            throw new NumberFormatException();

                        System.out.println("Finding solutions for " + nps[index] + " " + ms[index].getAbbreviation());
                        long t0 = System.nanoTime();
                        List<Set<String>> solutions = gapFill.getCandidateReactions(nps[index]);
                        long t1 = System.nanoTime();
                        System.out.printf("found solution in %.0f seconds\n", (t1 - t0) / 1e9);
                        System.out.println(solutions);

                    } catch (NumberFormatException e) {
                        System.out.println(line + " was not a valid number");
                    }

                    System.out.println("Enter a number [0-" + nps.length + "]: ");
                }
            }
            else {
                int start = Integer.parseInt(get("s", "0"));
                for (int i = start; i < nps.length; i++) {
                    System.out.println("Finding solutions for " + nps[i] + " " + ms[i].getAbbreviation());
                    long t0 = System.nanoTime();
                    List<Set<String>> solutions = gapFill.getCandidateReactions(nps[i]);
                    long t1 = System.nanoTime();
                    System.out.printf("found solution in %.0f seconds\n", (t1 - t0) / 1e9);
                    System.out.println(solutions);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
