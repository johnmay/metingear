package uk.ac.ebi.app;

import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.DefaultEntityFactory;
import uk.ac.ebi.mdk.domain.entity.EntityFactory;
import uk.ac.ebi.mdk.domain.entity.Metabolite;
import uk.ac.ebi.mdk.domain.entity.Reaction;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.reaction.Direction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicParticipant;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.identifier.basic.BasicReactionIdentifier;
import uk.ac.ebi.mdk.domain.matrix.DefaultStoichiometricMatrix;
import uk.ac.ebi.mdk.tool.domain.TransportReactionUtil;
import uk.ac.ebi.optimise.SimulationUtil;
import uk.ac.ebi.optimise.gap.GapFind;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author John May */
public class GapFindOnMatrix {

    public static void main(String[] args) throws Exception {

        if (args.length < 1)
            throw new IllegalArgumentException("Expected matrix path");

        String matrixPath = args[0];
        String reconPath = args[1];

        System.out.println("Reading reaction matrix" + matrixPath);

//        Reader reader = new BufferedReader(new FileReader(matrixPath));
//        BasicStoichiometricMatrix matrix = (BasicStoichiometricMatrix)
//                ReactionMatrixIO.readBasicStoichiometricMatrix(reader,
//                                                               BasicStoichiometricMatrix.create());
//        reader.close();
//
//        Map<String, Integer> reactionIndex = new HashMap<String, Integer>();
//        for (int j = 0; j < matrix.getReactionCount(); j++) {
//            reactionIndex.put(matrix.getReaction(j), j);
//        }

        Set<String> allowExchange = new HashSet<String>();

        DefaultStoichiometricMatrix s = DefaultStoichiometricMatrix.create();

        Reconstruction reconstruction = ReconstructionIOHelper.read(new File(reconPath));
        for (MetabolicReaction mr : reconstruction.reactome()) {

            String str = TransportReactionUtil.compartmentsAsString((Reaction) mr);
            if (str.equals("{c}")) {
                s.addReaction(mr);
            }
            else if (str.equals("{c, e}")) {
//                List<Metabolite> ms = TransportReactionUtil.exchanged(mr);
//                if (ms.size() == 0) {
//                    System.out.println(mr);
//                }
//                else {
//                    for (Metabolite m : ms) {
//                        s.addReaction(newProduction(reconstruction, m));
//                        s.addReaction(newConsumtion(reconstruction, m));
//                    }
//                }
                for (MetabolicParticipant p : mr.getParticipants()) {
                    if (p.getCompartment() == Organelle.CYTOPLASM) {
                        s.addReaction(newProduction(reconstruction, p.getMolecule()));
                        s.addReaction(newConsumtion(reconstruction, p.getMolecule()));
                    }
                }
            }
        }


//        List<String> additionalExchange = Arrays.asList("D-Sorbitol 6-phosphate",
//                                                        "alpha,alpha'-Trehalose 6-phosphate",
//                                                        "D-Fructose 1-phosphate",
//                                                        "D-Glucose 6-phosphate",
//                                                        "Sucrose 6-phosphate",
//                                                        "D-Mannitol 1-phosphate",
//                                                        "N-Acetyl-D-mannosamine 6-phosphate",
//                                                        "D-Glucosamine 6-phosphate",
//                                                        "D-Mannose 6-phosphate",
//                                                        "D-Fructose 6-phosphate",
//                                                        "Galactitol 1-phosphate",
//                                                        "N-Acetyl-D-glucosamine 6-phosphate",
//                                                        "Maltose 6'-phosphate",
//                                                        "Nicotinamide",
//                                                        "alpha-D-Ribose 5-phosphate");
//        addExchange(s, reconstruction, additionalExchange);


        System.out.println(s.getReactionCount() + " after allow exchange of all external metabolites");

        SimulationUtil.setup(); // load C library

        GapFind gaps = new GapFind(s);
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            Map<Integer, Double> m = s.getReactions(s.getMolecule(i));
            boolean produced = false;
            for (Map.Entry<Integer, Double> e : m.entrySet()) {
                if (s.isReversible(e.getKey())) {
                    produced |= true;
                    break;
                }
                else if (e.getValue() > 0) {
                    produced |= true;
                    break;
                }
            }
            if (!produced) {
                System.out.println(s.getMolecule(i).metabolite.getAbbreviation() + " is root NP");                
            }
        }
        Integer[] unproduced = gaps.getUnconsumedMetabolites();
        System.out.println(unproduced.length);
        System.out.println(Arrays.toString(unproduced));

    }

    private static void addExchange(DefaultStoichiometricMatrix s, Reconstruction r, List<String> ms) {
        for (String m : ms) {
            s.addReaction(newConsumtion(r, r.metabolome().ofName(m).iterator().next()));
            s.addReaction(newProduction(r, r.metabolome().ofName(m).iterator().next()));
        }
    }

    private static final EntityFactory entities = DefaultEntityFactory.getInstance();

    static MetabolicReaction newExchange(Reconstruction reconstruction, Metabolite m) {
        MetabolicReaction rxn = entities.reaction();
        rxn.setIdentifier(new BasicReactionIdentifier(m.getName() + "_ex"));
        rxn.addReactant(m);
        rxn.setDirection(Direction.BIDIRECTIONAL);
        return rxn;
    }

    static MetabolicReaction newProduction(Reconstruction reconstruction, Metabolite m) {
        MetabolicReaction rxn = entities.reaction();
        rxn.setIdentifier(new BasicReactionIdentifier(m.getName() + "_ex_out"));
        rxn.addReactant(m);
        rxn.setDirection(Direction.FORWARD);
        return rxn;
    }

    static MetabolicReaction newConsumtion(Reconstruction reconstruction, Metabolite m) {
        MetabolicReaction rxn = entities.reaction();
        rxn.setIdentifier(new BasicReactionIdentifier(m.getName() + "_ex_in"));
        rxn.addProduct(m);
        rxn.setDirection(Direction.FORWARD);
        return rxn;
    }
}
