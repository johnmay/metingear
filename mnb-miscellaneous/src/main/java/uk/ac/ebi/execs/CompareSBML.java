/**
 * CompareSBML.java
 *
 * Version $Revision$
 *
 * 2011.08.16
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
 * @author johnmay
 * @author $Author$ (this version)
 * @version $Rev$ : Last Changed $Date$
 * @name CompareSBML – 2011.08.16
 * Displays similarities based on a given set of SBML files in a directory
 */
public class CompareSBML   {
//        extends CommandLineMain {
//
//    private static final Logger LOGGER = Logger.getLogger(CompareSBML.class);
//    private File rxnRenderDirectory;
//    private BasicFilter rxnFilter = new BasicFilter();
//    private ChEBIWebServiceConnection chebiConn = new ChEBIWebServiceConnection();
//
//
//    public static void main(String[] args) {
//        new CompareSBML(args).process();
//    }
//
//
//    private CompareSBML(String[] args) {
//        super(args);
//    }
//
//
//    @Override
//    public void setupOptions() {
//        add(new Option("d", "directory", true, "an input directory"));
//    }
//
//
//    /**
//     * @inheritDoc
//     */
//    @Override
//    public void process() {
//
//        File directory = getFileOption("d");
//        rxnRenderDirectory = new File(directory, "rxns");
//        rxnRenderDirectory.mkdir();
//
//        if (directory == null || !directory.exists() || !directory.isDirectory()) {
//            System.err.println(directory + " is not a directory/doesn't exists");
//            printHelp();
//            System.exit(0);
//        }
//
//        String[] sbmlFilenames = directory.list(new FilenameFilter() {
//
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".xml") || name.endsWith(".sbml");
//            }
//
//
//        });
//
//        // loads all the reactions into a hash Rxn -> [ model names ]
//        Map<AtomContainerReaction, Set> rxnMap = buildReactionMap(directory, sbmlFilenames);
//
//
//        writeRxnTable(rxnMap, sbmlFilenames);
//
//        String[] names = sbmlFilenames;
//
//        Integer[][] simMx = new Integer[names.length][names.length];
//
//        for (Entry<AtomContainerReaction, Set> e : rxnMap.entrySet()) {
//
//            // More then one occurance.. write to reaction file
//            if (e.getValue().size() > 1) {
//                // write rxns
//                File rxnFile =
//                        new File(rxnRenderDirectory, e.getKey().getIdentifier().toString() + ".rxn");
//                IReaction rxn = e.getKey().getCDKReaction();
//                try {
//                    MDLRXNWriter rxnWriter = new MDLRXNWriter(new FileOutputStream(rxnFile));
//                    rxnWriter.write(rxn);
//                    rxnWriter.close();
//                } catch (Exception ex) {
//                    LOGGER.error("Could not write reaction: " + ex.getMessage());
//                }
//
//                for (int i = 0; i < names.length; i++) {
//                    for (int j = i + 1; j < names.length; j++) {
//                        if (e.getValue().contains(names[i]) &&
//                                e.getValue().contains(names[j])) {
//                            simMx[i][j] = (simMx[i][j] == null) ? 1 : simMx[i][j] + 1;
//                        }
//                    }
//                }
//            }
//        }
//
//        try {
//            CSVWriter csv =
//                    new CSVWriter(new FileWriter("/Users/johnmay/Desktop/sim-matrix.csv"));
//            csv.writeNext(names);
//            for (int i = 0; i < names.length; i++) {
//                String[] row = new String[names.length];
//                for (int j = 0; j < names.length; j++) {
//                    row[j] = simMx[i][j] == null ? "" : simMx[i][j].toString();
//                }
//                csv.writeNext(row);
//            }
//            csv.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        // print compound map
//        for (AtomContainerReaction rxn : rxnMap.keySet()) {
//            // find those that cross a compartment
//            for (ParticipantImplementation rp : rxn.getReactantParticipants()) {
//                AtomContainerParticipant rpNorm =
//                        new AtomContainerParticipant(((AtomContainerParticipant) rp).
//                                getMolecule());
//                for (ParticipantImplementation pp : rxn.getProductParticipants()) {
//                    AtomContainerParticipant ppNorm =
//                            new AtomContainerParticipant(((AtomContainerParticipant) pp).
//                                    getMolecule());
//                    if (rpNorm.hashCode() == ppNorm.hashCode()) {
//                        if (rpNorm.equals(ppNorm) &&
//                                rp.getCompartment() != pp.getCompartment()) {
//                            if (!compoundMap.containsKey(rpNorm)) {
//                                compoundMap.put(rpNorm, new HashSet<String>());
//                            }
//                            compoundMap.get(rpNorm).addAll(rxnMap.get(rxn));
//
//                        }
//                    }
//                }
//            }
//        }
//
//        // write a table of compounds being transported
//        FileWriter fw = null;
//        try {
//            fw = new FileWriter(new File(getFileOption("d"), "compounds.tsv"));
//            fw.write(StringUtils.join(names, '\t'));
//            fw.write("\tName\n");
//            for (Entry<AtomContainerParticipant, Set<String>> e : compoundMap.entrySet()) {
//                for (String modelName : names) {
//                    fw.write(e.getValue().contains(modelName) ? "1\t" : "0\t");
//                }
//                fw.write("\t" + e.getKey().getMolecule().getProperty("Name") + "\n");
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (fw != null) {
//                    fw.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//
//    public void writeRxnTable(Map<AtomContainerReaction, Set> rxnMap, String[] names) {
//        CSVWriter csv = null;
//        try {
//
//            csv = new CSVWriter(
//                    new FileWriter(new File(getFileOption("d"), "rxn-table.csv")));
//
//            String[] header = new String[names.length + 3];
//            header[0] = "rxn-id";
//            header[1] = "rxn-equation";
//            header[2] = "rxn-occurances";
//            for (int i = 0; i < names.length; i++) {
//                header[i + 3] = names[i];
//            }
//            csv.writeNext(header);
//
//            for (Entry<AtomContainerReaction, Set> e : rxnMap.entrySet()) {
//
//                AtomContainerReaction rxn = e.getKey();
//
//                String[] row = new String[names.length + 3];
//
//                row[0] = rxn.getIdentifier().toString();
//                row[1] = rxn.toString();
//                row[2] = Integer.toString(e.getValue().size());
//
//                for (int i = 0; i < names.length; i++) {
//                    row[i + 3] = e.getValue().contains(names[i]) ? "1" : "0";
//                }
//
//                csv.writeNext(row);
//
//            }
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                csv.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//
//    private Map<AtomContainerParticipant, Set<String>> compoundMap = new HashMap(1000, 0.9f);
//
//
//    /**
//     * Constructs a reaction Map(Keys=Reaction,Values=Models)
//     *
//     * @param directory
//     * @param fnames
//     *
//     * @return
//     */
//    public Map buildReactionMap(File directory, String[] fnames) {
//
//        Map<AtomContainerReaction, Set<String>> rxnMap = new HashMap(1000, 0.9f);
//
//        List<AtomContainerReaction> grxnMap = new ArrayList(500);
//        try {
//            rxnFilter.addRejection(chebiConn.getAtomContainer(15422)); // ATP
//            rxnFilter.addRejection(chebiConn.getAtomContainer(16761)); // ADP
//            rxnFilter.addRejection(chebiConn.getAtomContainer(18367)); // Phosphate(3-)
//        } catch (Exception ex) {
//        }
//
//
//        int total = 0;
//        try {
//
//            for (String fname : fnames) {
//
//                FileInputStream stream = new FileInputStream(new File(directory, fname));
//                // we use a BasicFilter to remove H+, H2O and CO2 here
//                SBMLReactionReader reader = new SBMLReactionReader(stream); // can turn on or off
//
//                int modelRXNCount = 0;
//
//                while (reader.hasNext()) {
//
//
//                    MetabolicReaction rxn = reader.next();
//
//                    if (true) throw new UnsupportedOperationException("Old code... please update!");
//                    //                        if (rxn.isGeneric()) {
//                    //                            grxnMap.add(rxn);
//                    //
//                    //                        } else if (rxnMap.containsKey(rxn)) {
//                    //
//                    //                            // add to existing entry
//                    //                            rxnMap.get(rxn).add(fname);
//                    //
//                    //                        } else {
//                    //                            // create a new entrys
//                    //                            rxnMap.put(rxn, new HashSet(Arrays.asList(fname)));
//                    //                        }
//
//
//                    modelRXNCount++;
//                    total++;
//
//
//                }
//
//                System.out.println(fname + ": " + modelRXNCount);
//
//            }
//        } catch (XMLStreamException ex) {
//            LOGGER.error("Error loading SBML files " + ex.getMessage());
//        } catch (FileNotFoundException ex) {
//            LOGGER.error(ex.getMessage());
//        }
//
//
//        System.out.println(total + " total reactions succesfully loaded");
//        System.out.println(grxnMap.size() + " generic reactions succesfully loaded");
//        System.out.println(rxnMap.size() + " non-generic unique reactions succesfully loaded");
//
//
//        return rxnMap;
//
//    }


}

