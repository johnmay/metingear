package uk.ac.ebi.optimise.gap;

import com.google.common.base.Function;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import uk.ac.ebi.mdk.apps.io.ReconstructionIOHelper;
import uk.ac.ebi.mdk.domain.entity.Reconstruction;
import uk.ac.ebi.mdk.domain.entity.metabolite.CompartmentalisedMetabolite;
import uk.ac.ebi.mdk.domain.entity.reaction.Compartment;
import uk.ac.ebi.mdk.domain.entity.reaction.Direction;
import uk.ac.ebi.mdk.domain.entity.reaction.MetabolicReaction;
import uk.ac.ebi.mdk.domain.entity.reaction.compartment.Organelle;
import uk.ac.ebi.mdk.domain.matrix.DefaultStoichiometricMatrix;
import uk.ac.ebi.mdk.domain.matrix.StoichiometricMatrix;
import uk.ac.ebi.optimise.SimulationUtil;

import java.io.File;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Identify down-stream non-production (NP) metabolites.
 *
 * @author John May
 */
final class NpFind<M, R> {

    private IloCplex cplex;

    private IloNumVar[] v; // flux vector (size = n reactions)

    private IloIntVar[] xnp; // binary variable for maximising

    private SparseIloBoolMatrix w; // binary matrix - active reactions

    private final StoichiometricMatrix<M, R> s;

    private final BitSet cytosol, extracellular;

    private final Function<M, Compartment> fCompartment;

    NpFind(StoichiometricMatrix<M, R> s, Function<M, Compartment> fCompartment) throws Exception {

        SimulationUtil.setup();
        this.cplex = new IloCplex();
        this.s = s;
        this.fCompartment = fCompartment;

        cytosol = new BitSet(s.getMoleculeCount());
        extracellular = new BitSet(s.getMoleculeCount());

        // binary vector indicates metabolite is produced
        xnp = cplex.boolVarArray(s.getMoleculeCount());

        // flux can take any value between 0 and 100
        // LB ≤ v ≤ UB , j ∈ Model
        v = cplex.numVarArray(s.getReactionCount(),
                              -100,
                              100);

        w = new SparseIloBoolMatrix(cplex);

        assignedCompartments();

        binaryCons();

        // constrain flux of reversible reactions
        fluxCons();

        cytosolMassBalance();

        // constrain production
        irevProdCons();
        revProdCons();

        // objective function
        cplex.addMaximize(cplex.sum(xnp));

    }

    Set<M> solve() throws Exception {
        cplex.solve();
        int n = 0;
        Set<M> ms = new HashSet<M>();
        double[] xSolutions = cplex.getValues(xnp);
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            if (xSolutions[i] == 0)
                ms.add(s.getMolecule(i));
        }
        return ms;
    }

    private void binaryCons() throws IloException {
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            IloLinearIntExpr exp = cplex.linearIntExpr();
            for (int j = 0; j < s.getReactionCount(); j++) {
                // Sij > 0 and member of IR or Sij != 0 and member of Rev
                if (s.isReversible(j) ? s.get(i, j) != 0 : s.get(i, j) > 0) {
                    exp.addTerm(1, w.get(i, j));
                }
            }
            cplex.addGe(exp,
                        xnp[i]);
        }
    }

    private void assignedCompartments() {
        // note we don't really need to do this but for now it ensure we don't
        // have other compartments being provided
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            Compartment c = fCompartment.apply(s.getMolecule(i));
            if (c == Organelle.CYTOPLASM)
                cytosol.set(i);
            else if (c == Organelle.EXTRACELLULAR)
                extracellular.set(i);
            else
                throw new UnsupportedOperationException("Only reconstructions with cytosol and extracellular are supported.");
        }
    }

    /** Irreversible reactions cannot have negative flux. */
    private void fluxCons() throws IloException {
        for (int j = 0; j < s.getReactionCount(); j++)
            if (!s.isReversible(j))
                cplex.addGe(v[j], 0);
    }

    private void cytosolMassBalance() throws IloException {
        // Massbalance(i)$(cytosol(i) and not extracellular(i)).. sum(j$S(i,j),S(i,j)*v(j))=g=0;
        for (int i = cytosol.nextSetBit(0); i >= 0; i = cytosol.nextSetBit(i + 1)) {
            IloNumExpr[] values = new IloNumExpr[s.getReactionCount()];
            for (int j = 0; j < s.getReactionCount(); j++) {
                values[j] = cplex.prod(s.get(i, j),
                                       v[j]);
            }
            cplex.addGe(cplex.sum(values),
                        0);
        }
    }

    private final double e = 0.001, E = 100;

    private void irevProdCons() throws IloException {
        // prodconsirrev1(i,j)$(  (S(i,j) gt 0 and not rev(j)) )..v(j)=g=0.001*w(i,j);
        // prodconsirrev2(i,j)$( (S(i,j) gt 0 and not rev(j)) )..v(j)=l=100*w(i,j); 
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            for (int j = 0; j < s.getReactionCount(); j++) {
                if (s.isReversible(j) || s.get(i, j) <= 0)
                    continue;
                cplex.addGe(v[j],
                            cplex.prod(e,
                                       w.get(i, j))
                           );
                cplex.addLe(v[j],
                            cplex.prod(E,
                                       w.get(i, j))
                           );
            }
        }
    }

    private void revProdCons() throws IloException {
        // prodconsrev1(i,j)$(  (S(i,j) ne 0 and rev(j)) )..S(i,j)*v(j)=g=0.001-100*(1-w(i,j));
        // prodconsrev2(i,j)$( (S(i,j) ne 0 and rev(j)) )..S(i,j)*v(j)=l=100*w(i,j);
        for (int i = 0; i < s.getMoleculeCount(); i++) {
            for (int j = 0; j < s.getReactionCount(); j++) {
                if (!s.isReversible(j) || s.get(i, j) == 0)
                    continue;
                // ε-M(1-wij)
                cplex.addGe(cplex.prod(s.get(i, j),
                                       v[j]),
                            cplex.sum(e,
                                      cplex.negative(cplex.prod(E,
                                                                cplex.sum(1, cplex.negative(w.get(i, j)))))
                                     )
                           );
                cplex.addLe(cplex.prod(s.get(i, j),
                                       v[j]),
                            cplex.prod(E,
                                       w.get(i, j))
                           );
            }
        }
    }

    public static void main(String[] args) throws Exception {

        String path = args[0];
        System.out.println("[GapFind] Openning: " + path + "...");
        Reconstruction recon = ReconstructionIOHelper.read(new File(path));
        System.out.println("[GapFind] done");

        DefaultStoichiometricMatrix s = DefaultStoichiometricMatrix.create(2000, 2000);
        for (MetabolicReaction rxn : recon.reactome()) {
            if (rxn.getParticipantCount() == 1) {
                rxn.setDirection(Direction.BIDIRECTIONAL);
            }
            s.addReaction(rxn);
        }

        System.out.print("[GapFind] Creating fromulation...");
        NpFind<CompartmentalisedMetabolite, String> dnpm = new NpFind<CompartmentalisedMetabolite, String>(s, new Function<CompartmentalisedMetabolite, Compartment>() {
            @Override public Compartment apply(CompartmentalisedMetabolite cm) {
                return cm.compartment;
            }
        });
        System.out.println("done");
        System.out.println("[GapFind] Solving");
        Set<CompartmentalisedMetabolite> np = dnpm.solve();
        System.out.println(np.size() + " Non-production metabolites");
        Set<String> abrvs = new TreeSet<String>();
        for (CompartmentalisedMetabolite m : np) {
            abrvs.add(m.metabolite.getAbbreviation() + " " + m.metabolite.getName());
        }
        for (String abrv : abrvs)
            System.out.println(abrv);
    }
}
