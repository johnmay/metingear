/**
 * CustomXLStoSBML.java
 *
 * 2011.08.15
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
 * @name    CustomXLStoSBML – 2011.08.15
 *          Sampling the loading of reactions using structures (protonation)
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class CustomXLStoSBML {

//    private static final Logger LOGGER = Logger.getLogger(CustomXLStoSBML.class);
//
//    private static final File file = new File("/Users/johnmay/Desktop/transport-reactions/intial-additonal-dict/tr-tagged.tsv");
//
//    private static Map<String, Integer> columns = new HashMap<String, Integer>() {
//
//        {
//            put("organism.name", 0);
//            put("model.year", 3);
//
//            put("reaction.id", 4);
//            put("reaction.equation", 8);
//            put("direction", 13);
//            put("side", 14);
//            put("coef", 15);
//            put("db.id", 16);
//            put("name", 16);
//            put("compartment", 18);
//
//        }
//    };
//
//    private final Integer SBML_LEVEL = 2;
//
//    private final Integer SBML_VERSION = 2;
//
//    private Map<String, Species> moleculeCache = new HashMap<String, Species>();
//
//    private Map<AbstractReaction, List<String>> reactionModelMap = new HashMap<AbstractReaction, List<String>>();
//
//    private Map<uk.ac.ebi.core.CompartmentImplementation, Compartment> compartmentMap =
//                                                                       new EnumMap<uk.ac.ebi.core.CompartmentImplementation, Compartment>(
//            uk.ac.ebi.core.CompartmentImplementation.class) {
//
//        {
//            Compartment e = new Compartment("Extracellular", SBML_LEVEL, SBML_VERSION);
//            e.setSize(1);
//            put(uk.ac.ebi.core.CompartmentImplementation.EXTRACELLULA, e);
//            Compartment c = new Compartment("Cytoplasm", SBML_LEVEL, SBML_VERSION);
//            c.setSize(1);
//            put(uk.ac.ebi.core.CompartmentImplementation.CYTOPLASM, c);
//            Compartment p = new Compartment("Periplasm", SBML_LEVEL, SBML_VERSION);
//            p.setSize(1);
//            put(uk.ac.ebi.core.CompartmentImplementation.PERIPLASM, p);
//        }
//    };
//
//
//    public static void main(String[] args) throws FileNotFoundException, IOException, SBMLException,
//                                                  XMLStreamException, ChebiWebServiceFault_Exception {
//        new CustomXLStoSBML().proces();
//    }
//
//
//    public void proces() throws FileNotFoundException, IOException, XMLStreamException, SBMLException,
//                                ChebiWebServiceFault_Exception {
//
//        CSVReader reader = new CSVReader(new FileReader(file), '\t', '"');
//
//        String[] row = reader.readNext();
//        String prevEq = "";
//        String prevModelId = "";
//        AbstractReaction<CompartmentalisedParticipant<String, Integer, Compartment>> rxn = null;
//        HashMap<String, SBMLDocument> modelToSBML = new HashMap<String, SBMLDocument>();
//        Map<String, Map<Species, Boolean>> modelIdSpecies = new HashMap<String, Map<Species, Boolean>>();
//
//        while (row != null) {
//
//            String modelId = getModelId(row);
//
//            if (modelToSBML.containsKey(modelId) == false) {
//                System.out.println("Creating Model: " + modelId);
//                modelToSBML.put(modelId, new SBMLDocument(SBML_LEVEL, SBML_VERSION));
//                Model model = new Model();
//                modelToSBML.get(modelId).setModel(model);
//                for (Compartment c : compartmentMap.values()) {
//                    model.addCompartment(c);
//                }
//                modelIdSpecies.put(modelId, new HashMap<Species, Boolean>());
//            }
//
//            String equation = row[columns.get("reaction.equation")];
//
//            if (prevEq.equals(equation) == Boolean.FALSE) {
//                if (rxn != null) {
////                    org.sbml.jsbml.Reaction sbmlReaction = new org.sbml.jsbml.Reaction(UniqueIdentifier.createUniqueIdentifer().toString());
//
//
//                    Set<Species> toAdd = new HashSet<Species>();
//                    int error = 0;
//                    for (CompartmentalisedParticipant<String, Integer, uk.ac.ebi.interfaces.reaction.Compartment> p : rxn.getReactantParticipants()) {
//                        String dbidsbml = p.getMolecule();
//                        String comp = "[" + p.getCompartment().getAbbreviation() + "]";
//                        //
//                        if (modelIdSpecies.get(modelId).containsKey(moleculeCache.get(dbidsbml))
//                            == false) {
//                            if (moleculeCache.get(dbidsbml + comp) == null) {
//                                error = 1;
//                            }
//                            toAdd.add(moleculeCache.get(dbidsbml + comp));
//                        }
//                    }
//                    for (CompartmentalisedParticipant<String, Integer, uk.ac.ebi.interfaces.reaction.Compartment> p : rxn.getProductParticipants()) {
//                        String dbidsbml = p.getMolecule();
//                        String comp = "[" + p.getCompartment().getAbbreviation() + "]";
//
//                        if (modelIdSpecies.get(modelId).containsKey(moleculeCache.get(dbidsbml + comp))
//                            == false) {
//                            if (moleculeCache.get(dbidsbml + comp) == null) {
//                                error = 1;
//                            }
//                            toAdd.add(moleculeCache.get(dbidsbml + comp));
//                        }
//                    }
//
//                    // XXX quick hack
//                    if (error == 0) {
//                        Model model = modelToSBML.get(modelId).getModel();
//                        for (Species sp : toAdd) {
//                            model.addSpecies(sp);
//                            modelIdSpecies.get(modelId).put(sp, Boolean.TRUE);
//                        }
//                        org.sbml.jsbml.Reaction sbmlReaction = new org.sbml.jsbml.Reaction(new BasicReactionIdentifier().toString(), SBML_LEVEL, SBML_VERSION);
//                        for (CompartmentalisedParticipant<String, Integer, uk.ac.ebi.interfaces.reaction.Compartment> p : rxn.getReactantParticipants()) {
//                            String spid = p.getMolecule() + p.getCompartment().toString();
//                            sbmlReaction.addReactant(new SpeciesReference(moleculeCache.get(spid)));
//                        }
//                        for (CompartmentalisedParticipant<String, Integer, uk.ac.ebi.interfaces.reaction.Compartment> p : rxn.getProductParticipants()) {
//                            String spid = p.getMolecule() + p.getCompartment().toString();
//
//                            sbmlReaction.addProduct(new SpeciesReference(moleculeCache.get(spid)));
//                        }
//                        model.addReaction(sbmlReaction);
//                    } else {
//                        LOGGER.error("Skipping reaction: " + rxn + " [Missing identifiers for some compounds]");
//                    }
//                }
//                rxn = new AbstractReaction<CompartmentalisedParticipant<String, Integer, uk.ac.ebi.interfaces.reaction.Compartment>>();
//            }
//
//
//            String dbid = row[columns.get("db.id")];
//            String comp = row[columns.get("compartment")];
//
//            if (moleculeCache.containsKey(dbid + comp)) {
//                //next
//            } else {
//                Species sp = getSpecies(dbid, comp);
//                moleculeCache.put(dbid + comp, sp);
//            }
//
//
//            Species sp = moleculeCache.get(dbid + comp);
//            if (sp != null) {
//                ParticipantImplementation p = new ParticipantImplementation<String, Integer, uk.ac.ebi.core.CompartmentImplementation>(dbid, 1,
//                                                                                                                                       uk.ac.ebi.core.CompartmentImplementation.getCompartment(row[columns.get("compartment")]));
//                if (row[columns.get("side")].equals("left")) {
//                    rxn.addReactant(p);
//                } else {
//                    rxn.addProduct(p);
//                }
//            }
//
//
//            row = reader.readNext();
//            prevModelId = modelId;
//            prevEq = equation;
//
//        }
//
//        reader.close();
//
//        SBMLWriter writer = new SBMLWriter();
//
//        // write the SBML files
//        for (Entry<String, SBMLDocument> e : modelToSBML.entrySet()) {
//            writer.write(e.getValue(), "/Users/johnmay/Desktop/transport models/" + e.getKey() + ".xml");
//        }
//
//    }
//
//    private Pattern pattern = Pattern.compile("[^A-z0-9]");
//
//    private static ChebiWebServiceClient chebiClient = new ChebiWebServiceClient();
//
//
//    public Species getSpecies(String dbid, String comp) throws ChebiWebServiceFault_Exception {
//        uk.ac.ebi.core.CompartmentImplementation c = uk.ac.ebi.core.CompartmentImplementation.getCompartment(comp);
//
//        String formatedid = pattern.matcher(dbid).replaceAll("_") + c.getAbbreviation();
//
//
//        Species sp = new Species(formatedid, SBML_LEVEL, SBML_VERSION);
//        sp.setCompartment(compartmentMap.get(c));
//
//        if (dbid.contains("CHEBI")) {
//            String name = chebiClient.getLiteEntity(dbid, SearchCategory.CHEBI_ID, 1, StarsCategory.ALL).
//                    getListElement().get(0).getChebiAsciiName();
//            sp.setName(name);
//            sp.setMetaId(dbid.substring(6) + "_" + c.getAbbreviation());
//            Resource rdf = new ChEBIIdentifier().getResource();
//            sp.addCVTerm(new CVTerm(CVTerm.Qualifier.BQB_IS, rdf.getURN(dbid)));
//            return sp;
//        } else if (dbid.startsWith("C")) {
//            sp.setMetaId(dbid.substring(1) + "_" + c.getAbbreviation());
//            Resource rdf = new KEGGCompoundIdentifier().getResource();
//            sp.addCVTerm(new CVTerm(CVTerm.Qualifier.BQB_IS, rdf.getURN(dbid)));
//            // kegg
////            kegg.
//            return sp;
//        } else {
//            // dudd id
//            return null;
//        }
//
//    }
//
//
//    public String getModelId(String[] row) {
//        return (row[columns.get("organism.name")] + "-" + row[columns.get("model.year")]).toLowerCase().replace(
//                " ", "-");
//    }
}
