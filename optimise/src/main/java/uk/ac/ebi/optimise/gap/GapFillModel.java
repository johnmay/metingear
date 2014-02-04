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
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.cli.Option;
import uk.ac.ebi.mdk.apps.CommandLineMain;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.metabolite.CompartmentalisedMetabolite;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.matrix.BasicStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.DefaultStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.mdk.io.ReactionMatrixIO;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/** @author John May */
public class GapFillModel extends CommandLineMain {

    public static void main(String[] args) {
        new GapFillModel().process(args);
    }

    @Override public void setupOptions() {
        add(new Option("m", "model", true, "metabolic reconstruction"));
    }

    @Override public void process() {

        System.out.println("reading model...");
        Reconstruction reconstruction = loadRecon("m");
        System.out.println("done");

        SimulationUtil.setup();

        final Map<String, MetabolicReaction> rxnAccessions = new HashMap<String, MetabolicReaction>();


        DefaultStoichiometricMatrix database = DefaultStoichiometricMatrix.create(reconstruction);
        DefaultStoichiometricMatrix model = DefaultStoichiometricMatrix.create();


        for (MetabolicReaction rxn : reconstruction.reactome()) {
            rxnAccessions.put(rxn.getAccession(), rxn);
            System.out.println(rxn.getAbbreviation() + " " + !rxn.getAbbreviation().endsWith("_db"));
            if (!rxn.getAbbreviation().endsWith("_db"))
                model.addReaction(rxn);
        }

        // mapping of a compartmentalised metabolite expressed as a string to
        // a compartment
        Function<CompartmentalisedMetabolite, Compartment> functor = new Function<CompartmentalisedMetabolite, Compartment>() {
            @Override public Compartment apply(CompartmentalisedMetabolite cm) {
                return cm.compartment;
            }
        };


        Multimap<String, String> map = HashMultimap.create();

        try {
            NpFind<CompartmentalisedMetabolite, String> gapFind = new NpFind<CompartmentalisedMetabolite, String>(model, functor);
            Set<CompartmentalisedMetabolite> nps = gapFind.solve();
            System.out.println(nps.size());
            System.out.println(Joiner.on("\n").join(Collections2.transform(nps, new Function<CompartmentalisedMetabolite, String>() {
                @Override public String apply(CompartmentalisedMetabolite cm) {
                    return cm.metabolite.getAbbreviation();
                }
            })));
            
            Map<String,CompartmentalisedMetabolite> lookup = new HashMap<String, CompartmentalisedMetabolite>();
            for (CompartmentalisedMetabolite cm : nps) {
                lookup.put(cm.metabolite.getAbbreviation(), cm);
            }

            GapFill<CompartmentalisedMetabolite, String> gapfill = new GapFill<CompartmentalisedMetabolite, String>(database, model, functor);

            Scanner scanner = new Scanner(System.in);

            System.out.println("all: run all, q: quit or enter an abbreviation:");
            String line = null;
            while (!(line = scanner.nextLine()).equals("q")) {
                if (line.equals("all")) {
                    for (CompartmentalisedMetabolite problem : nps) {
                        Collection<Set<String>> results = Collections2.transform(gapfill.getCandidateReactions(problem), new Function<Set<String>, Set<String>>() {
                            @Override public Set<String> apply(Set<String> s) {
                                return new HashSet<String>(Collections2.transform(s, new Function<String, String>() {
                                    @Override public String apply(String s) {
                                        return rxnAccessions.get(s).getAbbreviation();
                                    }
                                }));
                            }
                        });
                        if (results.isEmpty())
                            System.out.println(problem.metabolite.getAbbreviation() + problem.compartment + "\t");
                        for (Set<String> result : results) {
                            System.out.println(problem.metabolite.getAbbreviation() + "\t" + Joiner.on(",").join(result));
                        }
                        for (Set<String> reactions : results) {
                            for (String rxn : reactions) {
                                map.put(rxn, problem.metabolite.getAbbreviation());
                            }
                        }
                    }
                } else {
                    CompartmentalisedMetabolite problem = lookup.get(line);
                    if (problem == null) {
                        System.out.println(line + " was not a non-production metabolite");
                    } else {
                        System.out.println(line + " solutions");
                        Collection<Set<String>> results = Collections2.transform(gapfill.getCandidateReactions(problem), new Function<Set<String>, Set<String>>() {
                            @Override public Set<String> apply(Set<String> s) {
                                return new HashSet<String>(Collections2.transform(s, new Function<String, String>() {
                                    @Override public String apply(String s) {
                                        return rxnAccessions.get(s).getAbbreviation();
                                    }
                                }));
                            }
                        });
                        if (results.isEmpty())
                            System.out.println(problem.metabolite.getAbbreviation() + problem.compartment + "\t");
                        for (Set<String> result : results) {
                            System.out.println(problem.metabolite.getAbbreviation() + "\t" + Joiner.on(",").join(result));
                        }
                    }
                }
            }

            for (String rxn : new TreeSet<String>(map.keySet())) {
                System.out.println(Joiner.on("\t").join(rxn, Joiner.on(", ").join(map.get(rxn))));
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
