/**
 * MetaboliteCoordinatesMain.java
 *
 * 2011.09.02
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
package uk.ac.ebi.execs;

/**
 *          MetaboliteCoordinatesMain – 2011.09.02 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class MetaboliteCoordinatesMain {

// extends CommandLineMain {
//
//    private static final Logger LOGGER = Logger.getLogger(MetaboliteCoordinatesMain.class);
//
//
//    public static void main(String[] args) {
//        new MetaboliteCoordinatesMain(args).process();
//    }
//
//
//    public MetaboliteCoordinatesMain(String[] args) {
//        super(args);
//    }
//
//
//    @Override
//    public void setupOptions() {
//        add(new Option("f", "file", true,
//                       "Metabolic model in SBML format (inc. MIRIAM annotations)"));
//    }
//
//
//    @Override
//    public void process() {
//
//        File sbmlInput = getFileOption("f");
//        InputStream stream = null;
//        try {
//            // IO
//
//            stream = new FileInputStream(sbmlInput);
//
//            Map<List<Integer>, List<IAtomContainer>> coordinates = getCoodinateMap(stream);
//
//            for (Entry<List<Integer>, List<IAtomContainer>> e : coordinates.entrySet()) {
//                for (IAtomContainer mol : e.getValue()) {
//                    System.out.println(mol.getProperty("Name") + "\t" + StringUtils.join(e.getKey(),
//                                                                                         "\t"));
//                }
//            }
//
//            stream.close();
//
//        } catch (XMLStreamException ex) {
//            LOGGER.error("Error reading SBML document");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (stream != null) {
//                    stream.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//
//    public Map<List<Integer>, List<IAtomContainer>> getCoodinateMap(InputStream stream)
//            throws XMLStreamException {
//
//        Map<List<Integer>, List<IAtomContainer>> coordinateMap =
//                                                 new HashMap<List<Integer>, List<IAtomContainer>>();
//        SBMLReactionReader modelReactions = new SBMLReactionReader(stream, DefaultEntityFactory.getInstance(), new AutomaticCompartmentResolver());
//
//        while (modelReactions.hasNext()) {
//
//            MetabolicReaction rxn;
//
//                rxn = modelReactions.next();
//
//            if(true) throw new UnsupportedOperationException("Old code, please update!");
////                for (IAtomContainer molecule : (List<IAtomContainer>) rxn.getAllReactionMolecules()) {
////
////                    List<Integer> location = getPoint(molecule);
////
////                    if (coordinateMap.containsKey(location)) {
////
////                        boolean found = false;
////                        String newName =
////                               molecule.getProperty("Name").toString().trim().toLowerCase();
////
////                        for (IAtomContainer existingMol : coordinateMap.get(location)) {
////                            String existingName = existingMol.getProperty("Name").toString().trim().
////                                    toLowerCase();
////                            if (newName.equals(existingName)) {
////                                found = true;// can also use SMSD
////                            }
////                        }
////
////                        if (found == false) {
////                            coordinateMap.get(location).add(molecule);
////                        }
////
////                    } else {
////                        coordinateMap.put(location, new ArrayList());
////                        coordinateMap.get(location).add(molecule);
////                    }
////
////
////                    // what about the hypergraph?
////                    // match left and right sides and add coordinate lines
////
////                }
//
//
//        }
//
//        // have a look at the number of molecules in each slot (if < 4 can distribute this)
//
//        return coordinateMap;
//
//    }
//
//
//    public Point3d getPoint3d(IAtomContainer molecule) {
//
//        Map<String, Integer> atomSymbolMap = new HashMap<String, Integer>();
//
//        // make sure no null pointer exceptions on return
//        atomSymbolMap.put("C", 0);
//        atomSymbolMap.put("O", 0);
//        atomSymbolMap.put("N", 0);
//
//        Iterator<IAtom> atomIt = molecule.atoms().iterator();
//        while (atomIt.hasNext()) {
//            IAtom atom = atomIt.next();
//            if (atomSymbolMap.containsKey(atom.getSymbol()) == Boolean.FALSE) {
//                atomSymbolMap.put(atom.getSymbol(), 0);
//            }
//            atomSymbolMap.put(atom.getSymbol(), atomSymbolMap.get(atom.getSymbol()) + 1);
//        }
//
//        return new Point3d(atomSymbolMap.get("C"), // x
//                           atomSymbolMap.get("O"), // y
//                           atomSymbolMap.get("N")); // z
//    }
//
//
//    private List<Integer> getPoint(IAtomContainer molecule) {
//        CDKAtomTyper.typeAtoms(molecule);
//        try {
//            CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance()).addImplicitHydrogens(
//                    molecule);
//        } catch (CDKException ex) {
//        }
//        AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
//
//        Map<String, Integer> atomSymbolMap = new HashMap<String, Integer>();
//        Map<String, Integer> bondSymbolMap = new HashMap<String, Integer>();
//
//        // make sure no null pointer exceptions on return
//        for (String symbol : Arrays.asList("C", "O", "N", "P", "H", "S")) {
//            atomSymbolMap.put(symbol, 0);
//            bondSymbolMap.put(symbol, 0);
//        }
//
//        Iterator<IAtom> atomIt = molecule.atoms().iterator();
//        while (atomIt.hasNext()) {
//            IAtom atom = atomIt.next();
//            if (atomSymbolMap.containsKey(atom.getSymbol()) == Boolean.FALSE) {
//                atomSymbolMap.put(atom.getSymbol(), 0);
//            }
//            atomSymbolMap.put(atom.getSymbol(), atomSymbolMap.get(atom.getSymbol()) + 1);
//        }
//        Iterator<IBond> bondIt = molecule.bonds().iterator();
//        while (bondIt.hasNext()) {
//            IBond bond = bondIt.next();
//            for (int i = 0; i < bond.getAtomCount(); i++) {
//                if (bondSymbolMap.containsKey(bond.getAtom(i).getSymbol()) == Boolean.FALSE) {
//                    bondSymbolMap.put(bond.getAtom(i).getSymbol(), 0);
//                }
//                bondSymbolMap.put(bond.getAtom(i).getSymbol(), bondSymbolMap.get(bond.getAtom(i).
//                        getSymbol()) + 1);
//            }
//        }
//
//        List syms = new ArrayList();
//        for (String sym : Arrays.asList("C", "O", "N", "H", "P")) {
//            syms.add(atomSymbolMap.get(sym) + bondSymbolMap.get(sym));
//        }
//
//        return syms;
//
//    }
}
