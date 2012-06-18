/**
 * LoadXLSModel.java
 *
 * 2011.08.30
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
 *          LoadXLSModel – 2011.08.30 <br>
 *          Class description
 * @version $Rev$ : Last Changed $Date$
 * @author  johnmay
 * @author  $Author$ (this version)
 */
public class LoadXLSModel  {

//    private static final Logger LOGGER = Logger.getLogger(LoadXLSModel.class);
//
//
//    public static void main(String[] args) throws IOException {
//        new LoadXLSModel(args).process();
//    }
//
//
//    public LoadXLSModel(String[] args) {
//        super(args);
//    }
//
//
//    @Override
//    public void setupOptions() {
//        add(new Option("m", "xls-model", true, "Excel 2007 Metabolic Model"));
//        add(new Option("p", "properties", true, "XML Properties file"));
//    }
//
//
//    @Override
//    public void process() {
//
//        try {
//
//            File modelFile = getFileOption("m");
//            File propFile = getFileOption("p");
//
//
//            // load the properties from XML
//            ExcelModelProperties properties = new ExcelModelProperties();
//            properties.loadFromXML(new FileInputStream(propFile));
//
//            InputStream stream = new FileInputStream(modelFile);
//            HSSFWorkbook workbook = new HSSFWorkbook(stream);
//            HSSFSheet sheet = workbook.getSheetAt(0);
//            PreparsedSheet rxnSht = new HSSFPreparsedSheet(sheet,
//                                                           properties,
//                                                           ReactionColumn.DATA_BOUNDS);
//            PreparsedSheet entSht = new HSSFPreparsedSheet(workbook.getSheetAt(2),
//                                                           properties,
//                                                           EntityColumn.DATA_BOUNDS);
//
//            ChemicalDBWebService webservice =
//                                 new CachedChemicalWS(new ChEBIWebServiceConnection(
//                    StarsCategory.THREE_ONLY, 10));
//
//            CandidateFactory factory = null;//new CandidateFactory(ChEBINameService.getInstance(),
//                                            //                new ChemicalFingerprintEncoder());
//
//            ExcelEntityResolver entitySheet =
//                                new ExcelEntityResolver(entSht, new AutomatedReconciler(factory,
//                                                                                        new ChEBIIdentifier()), DefaultEntityFactory.getInstance());
//
//            ReactionParser parser = new ReactionParser(entitySheet);
//
//            Map<XrefLevel, Integer> stats = new EnumMap(XrefLevel.class);
//            stats.put(XrefLevel.ALL, 0);
//            stats.put(XrefLevel.SOME, 0);
//            stats.put(XrefLevel.NONE, 0);
//
//            while (rxnSht.hasNext()) {
//                PreparsedReaction ppRxn = (PreparsedReaction) rxnSht.next();
//                MetabolicReaction rxn = parser.parseReaction(ppRxn);
//                XrefLevel level = score(rxn);
//                stats.put(level, stats.get(level) + 1);
//            }
//
//            System.out.println("Counts:" + stats);
//
//            System.out.println(map.size());
//            // so slow but can't use tree map as it will collapse values
//            List<Integer> values = new ArrayList(new HashSet(map.values()));
//            Collections.sort(values);
//            for (Integer value : values) {
//                for (String key : map.keySet()) {
//                    if (map.get(key).equals(value)) {
//                        System.out.println(key + " = " + value);
//                    }
//                }
//            }
//
//        } catch (UnparsableReactionError ex) {
//            ex.printStackTrace();
//        } catch (InvalidPropertiesFormatException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//
//    private enum XrefLevel {
//
//        ALL,
//        SOME,
//        NONE;
//    };
//
//
//    public XrefLevel score(MetabolicReaction rxn) {
//
//        List<ParticipantImplementation<Metabolite, Double, CompartmentImplementation>> participants = (List) rxn.getParticipants();
//
//        int missingXref = 0;
//        for (int i = 0; i < participants.size(); i++) {
//            Metabolite m = participants.get(i).getMolecule();
//            // have has no cross references...
//            if (m.getAnnotationsExtending(CrossReference.class).isEmpty()) {
//
////                if( m.getName().equals("Coenzyme A") == false &&
////                    m.getName().equals("Diphosphate") == false &&
////                    m.getName().equals("Phosphate") == false ) {
//
//                missingXref++;
//                String name = m.getName();
//                map.put(name, map.containsKey(name) ? map.get(name) + 1 : 1);
////                }
//
//            }
//        }
//
//        if (missingXref == participants.size()) {
//            return XrefLevel.NONE;
//        } else if (missingXref == 0) {
//            return XrefLevel.ALL;
//        } else {
//            return XrefLevel.SOME;
//        }
//
//    }
//
//    private Map<String, Integer> map = new HashMap();
}
