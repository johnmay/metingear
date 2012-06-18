/**
 * @author John May
 */
public class StereoTest {

//    private static final Logger LOGGER = Logger.getLogger(StereoTest.class);
//
//    public static void main(String[] args) throws IOException, CDKException {
//
//        File file = new File(args[0]);
//        String name = file.getName();
//
//        if (name.endsWith(".sdf")) {
//            benchmark(file);
//        } else if (name.endsWith(".mol")) {
//            debug(file);
//        }
//
//    }
//
//    private static void debug(File molFile) throws IOException, CDKException {
//
//        MDLV2000Reader mdlReader = new MDLV2000Reader(new FileReader(molFile));
//        IAtomContainer structure = mdlReader.read(SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
//        mdlReader.close();
//
//
//        MolImport molImport = new MolImport();
//        molImport.initMolImport(new MolInputStream(new FileInputStream(molFile)));
//
//        Molecule m = new Molecule();
//        molImport.readMol(m);
//
//        CMLMolecule molecule = getCMLMolecule(structure);
//
//
//        for (int i = 0; i < molecule.getAtomCount(); i++) {
//
//
//            String chemaxon = getChiralityString(m.getChirality(i));
//
//            AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(structure);
//            CDKHueckelAromaticityDetector.detectAromaticity(structure);
//
//            String cdk = ChiralityCalculator.getChirality(structure, structure.getAtom(i)).toString();
//
//            System.out.println("chemaxon=" + chemaxon + " cdk=" + cdk + " Cahn-Ingold-Prelog priority=" + ChiralityCalculator.getConnectedString(structure, structure.getAtom(i)));
//
//
//        }
//        MolecularHashFactory.getInstance().setDepth(2);
//        MolecularHashFactory.getInstance().setSeedMethods(SeedFactory.getInstance().getSeeds(AtomicNumberSeed.class, ConnectedAtomSeed.class));
//        MolecularHashFactory.getInstance().setSeedMethods(SeedFactory.getInstance().getSeeds(BondOrderSumSeed.class));
//        MolecularHashFactory.getInstance().setSeedMethods(SeedFactory.getInstance().getSeeds(ChargeSeed.class));
//        MolecularHashFactory.getInstance().setSeedMethods(SeedFactory.getInstance().getSeeds(StereoSeed.class));
//
//
//        System.out.println(MolecularHashFactory.getInstance().getHash(structure));
//
//
////        ByteArrayOutputStream out = new ByteArrayOutputStream();
////
////        Document doc = new Document(molecule);
////
////        Serializer serializer = null;
////        serializer = new CustomSerializer(out, "ISO-8859-1");
////        serializer.setIndent(2);
////        molecule.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
////
////        serializer.write(doc);
////
////        System.out.println(out.toString("UTF-8"));
//
//    }
//
//    private static String getChiralityString(int value) {
//        return value == StereoConstants.CHIRALITY_R
//               ? "R" : value == StereoConstants.CHIRALITY_S
//                       ? "S" : "NONE";
//    }
//
//    private static Molecule getChemAxonMolecule(IAtomContainer container) {
//
//        StringWriter writer = new StringWriter();
//        MDLV2000Writer mdlv2000Writer = new MDLV2000Writer(writer);
//        try {
//            mdlv2000Writer.write(container);
//            MolImport molImport = new MolImport();
//            molImport.initMolImport(new MolInputStream(new ByteArrayInputStream(writer.toString().getBytes())));
//            Molecule m = new Molecule();
//            molImport.readMol(m);
//            return m;
//        } catch (CDKException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (MolFormatException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        return new Molecule();
//
//    }
//
//    private static CMLMolecule getCMLMolecule(IAtomContainer structure) {
//
//        Convertor convertor = new Convertor(true, null);
//
//
//        CMLMolecule cmlStructure = convertor.cdkAtomContainerToCMLMolecule(structure);
//
//        // fix CDK conversion
//        for (CMLBond bond : cmlStructure.getBonds()) {
//            if (bond.getBondStereo() != null) {
//                CMLBondStereo stereo = bond.getBondStereo();
//                stereo.setXMLContent(stereo.getDictRef().substring(4));
//            }
//        }
//
//        return cmlStructure;
//
//    }
//
//    private static void benchmark(File sdfFile) throws IOException {
//
//        IteratingMDLReader sdf = new IteratingMDLReader(new FileReader(sdfFile), SilentChemObjectBuilder.getInstance(), true);
//
//
//        int myMatched = 0;
//        int gMatched = 0;
//        int myMismatch = 0;
//        int gMismatch = 0;
//        int tested = 0;
//
//        File errorFile = File.createTempFile("chirality-errors", ".tsv");
//        CSVWriter errorWriter = new CSVWriter(new FileWriter(errorFile), '\t', '\0');
//
//
//        while (sdf.hasNext()) {
//
//            IAtomContainer structure = sdf.next();
//
//            CMLMolecule molecule = getCMLMolecule(structure);
//
////            // do the default stuff
//            try {
//                AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(structure);
//                //CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(structure);
//                //CDKHueckelAromaticityDetector.detectAromaticity(structure);
//            } catch (CDKException e) {
//                continue;
//            }
//
//            Chirality2DTool cdkTool = new Chirality2DTool();
//
//            Molecule m = getChemAxonMolecule(structure);
//
//            for (int i = 0; i < structure.getAtomCount(); i++) {
//                Integer readParity = structure.getAtom(i).getStereoParity();
//
//                if (structure.getAtomCount() > 50)
//                    continue;
//
//                IAtom atom = structure.getAtom(i);
//
//                // only looking at carbon atoms ATM
//                if (!atom.getSymbol().equals("C"))
//                    continue;
//
//                String expected = getChiralityString(m.getChirality(i));
//                if (expected != null) {
//
//                    tested++;
//
//                    try {
////                        StereochemistryTool cmlTool = new StereochemistryTool(molecule);
//
//                        String myValue = ChiralityCalculator.getChirality(structure, structure.getAtom(i)).toString();
//
//                        if (expected != null && !expected.equals(myValue)) {
//                            //System.out.println("Missmatch in structure " + structure.getProperty("ChEBI ID") + " expected " + expected + " but got " + myValue);
//                            myMismatch++;
//                            errorWriter.writeNext(
//                                    new String[]{
//                                            structure.getProperty("ChEBI ID").toString(),
//                                            Integer.toString(i + 1),
//                                            expected,
//                                            myValue
//                                    });
//                        } else {
//                            myMatched++;
//                        }
//
//
//                    } catch (RuntimeException ex) {
//                        LOGGER.error("Problem with " + structure.getProperty("ChEBI ID") + " atom " + structure.getAtom(i).getID());
//                    }
//
//
//                    if ((tested % 1000) == 0) {
//                        System.out.println("final values");
//                        System.out.println(myMatched + " matched");
//                        System.out.println(myMismatch + " mismatched");
//                    }
//                }
//
//
//            }
//
//
////            Map<IAtom, IStereo> map;
////            try {
////                map = cdkTool.getTetrahedralChiralities(new AtomContainer(structure));
////            } catch (NullPointerException ex) {
////                continue;
////            }
////
////            // g tool
////            for (int i = 0; i < structure.getAtomCount(); i++) {
////
////                if (structure.getAtomCount() > 50)
////                    continue;
////
////                String expected = getChiralityString(m.getChirality(i));
////                if (expected != null) {
////                    try {
////                        String gValue = map.get(structure.getAtom(i)).toString();
////                        if (!expected.equals(gValue)) {
////                            gMismatch++;
////                        } else {
////                            gMatched++;
////                        }
////                    } catch (RuntimeException ex) {
////                        LOGGER.error("Problem with " + structure.getProperty("ChEBI ID") + " atom " + structure.getAtom(i).getID());
////                    }
////
////
////                }
////
////
////            }
//
//
//        }
//
//        System.out.println("final values (mine)");
//        System.out.println(myMatched + " matched");
//        System.out.println(myMismatch + " mismatched");
//
//        System.out.println("final values (g)");
//        System.out.println(gMatched + " matched");
//        System.out.println(gMismatch + " mismatched");
//
//        errorWriter.close();
//
//        System.out.println("Written errors to: " + errorFile);
//
//    }
//
//    /**
//     * Does exactly what it says on the tin.  In the returned list the first atom
//     * has the highest priority, the last has the lowest priority.
//     * <p/>
//     * Currently only works for C atoms with 4 ligands.
//     *
//     * @param centralAtom
//     *
//     * @return ligands
//     */
//    public static List<CMLAtom> getLigandsInCahnIngoldPrelogOrder(CMLAtom centralAtom) {
//        List<CMLAtom> ligandList = centralAtom.getLigandAtoms();
//        List<CMLAtom> orderedLigandList = new ArrayList<CMLAtom>();
//        orderedLigandList.add(ligandList.get(0));
//
//        for (CMLAtom atom : ligandList) {
//
//
//            for (int i = 0; i < orderedLigandList.size(); i++) {
//                if (orderedLigandList.get(i) == atom) continue;
//                CMLAtomSet markedAtoms = new CMLAtomSet();
//                CMLAtomSet otherMarkedAtoms = new CMLAtomSet();
//                markedAtoms.addAtom(centralAtom);
//                otherMarkedAtoms.addAtom(centralAtom);
//                int value = compareRecursivelyByAtomicNumber(orderedLigandList.get(i),
//                                                             atom, markedAtoms, otherMarkedAtoms);
//
//                //LOG.debug(orderedLigandList.get(i).getId()+"/"+atom.getId()+" = "+value);
//                if (value == 1) {
//                    if (i + 1 == orderedLigandList.size()) {
//                        orderedLigandList.add(i + 1, atom);
//                        break;
//                    } else {
//                        continue;
//                    }
//                } else if (value == -1) {
//                    orderedLigandList.add(i, atom);
//                    break;
//                } else {
//                    throw new RuntimeException("Error getting ligands in CIP order.");
//                }
//            }
//        }
//        return orderedLigandList;
//    }
//
//    /**
//     * Calculates R or S.
//     * uses calculateAtomParity(CMLAtom atom)
//     *
//     * @param atom
//     *
//     * @return R or S or null if this atom isnt a chiral centre or
//     *         there isnt enough stereo information to calculate parity
//     */
//    public static String calculateCIPRS(CMLAtom atom) {
//        CMLAtomParity atomParity = calculateAtomParityForLigandsInCIPOrder(atom);
//        String rs = null;
//        if (atomParity != null) {
//            rs = (atomParity.getXMLContent() > 0) ? "R" : "S";
//        }
//        return rs;
//    }
//
//    /**
//     * Calculates the atom parity of this atom using the coords of either 4
//     * explicit ligands or 3 ligands and this atom. If only 2D coords are
//     * specified then the parity is calculated using bond wedge/hatch
//     * information.
//     *
//     * @param atom
//     *
//     * @return the CMLAtomParity, or null if this atom isnt a chiral centre or
//     *         there isnt enough stereo information to calculate parity. Note that
//     *         the atomParity is not necessarily created as a child of the atom
//     */
//    public static CMLAtomParity calculateAtomParityForLigandsInCIPOrder(CMLAtom atom) {
//        if (!isChiralCentre(atom)) {
//            return null;
//        }
//        List<CMLAtom> ligandList = getLigandsInCahnIngoldPrelogOrder(atom);
//
//        if (ligandList.size() == 3) {
//            ligandList.add(atom); // use this atom as 4th atom
//        }
//        double[][] parityMatrix = new double[4][4];
//        String[] atomRefs4 = new String[4];
//        for (int i = 0; i < 4; i++) { // build matrix
//            parityMatrix[0][i] = 1;
//            if (ligandList.get(i).hasCoordinates(CMLElement.CoordinateType.CARTESIAN)) {
//                parityMatrix[1][i] = ligandList.get(i).getX3();
//                parityMatrix[2][i] = ligandList.get(i).getY3();
//                parityMatrix[3][i] = ligandList.get(i).getZ3();
//            } else if (ligandList.get(i).hasCoordinates(CMLElement.CoordinateType.TWOD)) {
//                parityMatrix[1][i] = ligandList.get(i).getX2();
//                parityMatrix[2][i] = ligandList.get(i).getY2();
//                parityMatrix[3][i] = 0;
//                // get z-coord from wedge/hatch bond
//                CMLBond ligandBond = atom.getMolecule().getBond(atom,
//                                                                ligandList.get(i));
//
//                if (ligandBond != null) {
//                    CMLBondStereo ligandBondStereo = ligandBond.getBondStereo();
//                    if (ligandBondStereo != null) {
//                        if (ligandBondStereo.getXMLContent().equals(
//                                CMLBond.WEDGE)) {
//                            parityMatrix[3][i] = 1.0;
//                        } else if (ligandBondStereo.getXMLContent().equals(
//                                CMLBond.HATCH)) {
//                            parityMatrix[3][i] = -1.0;
//                        }
//                    }
//                }
//            } else {
//                System.out.println("No coordinates " + ligandList.get(i).getId() + ligandList.get(i).getX2Attribute() + ligandList.get(i).getY2Attribute());
//                // no coordinates!
//                throw new RuntimeException(
//                        "insufficient coordinates on ligands to determine parity");
//            }
//            atomRefs4[i] = ligandList.get(i).getId();
//        }
//
//        double parityDeterminant = determinant(parityMatrix);
//        CMLAtomParity atomParity = new CMLAtomParity();
//        if (Math.abs(parityDeterminant) > atomParity.minChiralDeterminant) {
//            atomParity.setAtomRefs4(atomRefs4);
//            atomParity.setXMLContent(parityDeterminant);
//            return atomParity;
//        } else {
//            return null;
//        }
//    }
//
//    private static double determinant(double[][] matrix) {
//        double determinant = 0;
//        int matrixSize = matrix.length;
//        double[][] minorMatrix = new double[matrixSize - 1][matrixSize - 1];
//        if (matrixSize == 2) {
//            return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
//        } else {
//            for (int j1 = 0; j1 < matrixSize; j1++) {
//                for (int i = 1; i < matrixSize; i++) {
//                    int j2 = 0;
//                    for (int j = 0; j < matrixSize; j++) {
//                        if (j == j1)
//                            continue;
//                        minorMatrix[i - 1][j2] = matrix[i][j];
//                        j2++;
//                    }
//                }
//                // sum (+/-)cofactor * minor
//                determinant = determinant + Math.pow(-1.0, j1) * matrix[0][j1]
//                        * determinant(minorMatrix);
//            }
//        }
//        return determinant;
//    }
//
//    /**
//     * Determines whether this atom is a chiral centre, currently only works for
//     * carbon atoms with 4 ligands (or 3 + an implicit hydrogen).
//     *
//     * @param atom
//     *
//     * @return true unless this atom has 2 or more identical ligands
//     */
//    public static boolean isChiralCentre(CMLAtom atom) {
//        boolean mayBeChiral = false;
//        List<CMLAtom> ligandList = atom.getLigandAtoms();
//        if (ChemicalElement.AS.C.equals(atom.getElementType())) {
//            // skip atoms with too few ligands
//            boolean c3h = ligandList.size() == 3 &&
//                    atom.getHydrogenCountAttribute() != null &&
//                    atom.getHydrogenCount() == 1;
//            if (ligandList.size() == 4 || c3h) {
//                mayBeChiral = true;
//                for (CMLAtom firstLigand : ligandList) {
//                    if (c3h && ChemicalElement.AS.H.equals(firstLigand.getElementType())) {
//                        // also have one implicit hydrogen, so not chiral
//                        mayBeChiral = false;
//                        return mayBeChiral;
//                    }
//                    for (CMLAtom secondLigand : ligandList) {
//                        if (firstLigand == secondLigand) {
//                            continue;
//                        }
//                        AtomTree firstAtomTree = new AtomTree(atom, firstLigand);
//                        AtomTree secondAtomTree = new AtomTree(atom, secondLigand);
//                        firstAtomTree.expandTo(5);
//                        secondAtomTree.expandTo(5);
//                        if (firstAtomTree.toString().equals(
//                                secondAtomTree.toString())) {
//                            // identical ligands
//                            mayBeChiral = false;
//                            return mayBeChiral;
//                        }
//                    }
//                }
//            }
//        } else {
//            mayBeChiral = false;
//        }
//        return mayBeChiral;
//    }
//
//    private static int compareRecursivelyByAtomicNumber(CMLAtom atom, CMLAtom otherAtom,
//                                                        CMLAtomSet markedAtoms, CMLAtomSet otherMarkedAtoms) {
//        // compare on atomic number
//        int comp = atom.compareByAtomicNumber(otherAtom);
//
//        if (comp == 0) {
//            markedAtoms.addAtom(atom);
//            otherMarkedAtoms.addAtom(otherAtom);
//            CMLAtom[] thisSortedLigands = getNewLigandsSortedByAtomicNumber(
//                    atom, markedAtoms);
//            CMLAtom[] otherSortedLigands = getNewLigandsSortedByAtomicNumber(
//                    otherAtom, otherMarkedAtoms);
//
//
//            int length = Math.max(thisSortedLigands.length,
//                                  otherSortedLigands.length);
//
//            for (int i = 0; i < length; i++) {
//
//                CMLAtom thisLigand = i < thisSortedLigands.length ? thisSortedLigands[i] : null;
//                CMLAtom otherLigand = i < otherSortedLigands.length ? otherSortedLigands[i] : null;
//
//                comp = compareByAtomicNumber(thisLigand, otherLigand);
//
//                if (comp != 0) {
//                    break;
//                }
//
//            }
//
//            if (comp == 0) {
//                for (int i = 0; i < length; i++) {
//
//                    CMLAtom thisLigand = thisSortedLigands[i];
//                    CMLAtom otherLigand = otherSortedLigands[i];
//                    comp = compareRecursivelyByAtomicNumber(thisLigand, otherLigand, markedAtoms, otherMarkedAtoms);
//
//                    if (comp != 0) {
//                        break;
//                    }
//                }
//            }
//
//        }
//
//        return comp;
//
//    }
//
//    private static int compareByAtomicNumber(CMLAtom atom, CMLAtom otherAtom) {
//
//        int a = atom != null ? atom.getAtomicNumber() : -1;
//        int b = otherAtom != null ? otherAtom.getAtomicNumber() : -1;
//
//        if (a > b) {
//            return 1;
//        } else if (a < b) {
//            return -1;
//        } else {
//            return 0;
//        }
//    }
//
//
//    /**
//     * Acts as to create ghost bonds (but we can't do that so we actually add bonds in)
//     *
//     * @param atom
//     */
//    private static void expandBonds(CMLAtom atom) {
//
//        CMLMolecule molecule = atom.getMolecule();
//        List<CMLBond> bondlist = new ArrayList<CMLBond>(atom.getLigandBonds());
//        for (CMLBond bond : bondlist) {
//            String order = bond.getOrder();
//            if (order.equals("D")) {
//                // add ghost atoms
//                CMLAtom a1 = new CMLAtom(bond.getAtom(1).getId() + "-GHOST",
//                                         bond.getAtom(1).getChemicalElement());
//                CMLAtom a2 = new CMLAtom(bond.getAtom(0).getId() + "-GHOST",
//                                         bond.getAtom(0).getChemicalElement());
//                molecule.addAtom(a1);
//                molecule.addAtom(a2);
//                molecule.addBond(new CMLBond(bond.getAtom(0), a1, "S"));
//                molecule.addBond(new CMLBond(bond.getAtom(1), a2, "S"));
//                bond.setOrder("S");
//
//            }
//        }
//    }
//
//    private static CMLAtom[] getNewLigandsSortedByAtomicNumber(CMLAtom atom,
//                                                               CMLAtomSet markedAtoms) {
//        List<CMLAtom> newLigandVector = new ArrayList<CMLAtom>();
//
//        expandBonds(atom);
//
//        List<CMLAtom> ligandList = atom.getLigandAtoms();
//        for (CMLAtom ligandAtom : ligandList) {
//            if (!markedAtoms.contains(ligandAtom)) {
//                newLigandVector.add(ligandAtom);
//            }
//        }
//        CMLAtom[] newLigands = new CMLAtom[newLigandVector.size()];
//        int count = 0;
//        while (newLigandVector.size() > 0) {
//            int heaviest = getHeaviestAtom(newLigandVector);
//            CMLAtom heaviestAtom = newLigandVector.get(heaviest);
//            newLigands[count++] = heaviestAtom;
//            newLigandVector.remove(heaviest);
//        }
//        return newLigands;
//    }
//
//    private static int getHeaviestAtom(List<CMLAtom> newAtomVector) {
//        int heaviestAtNum = -1;
//        int heaviest = -1;
//        for (int i = 0; i < newAtomVector.size(); i++) {
//            CMLAtom atom = newAtomVector.get(i);
//            int atnum = atom.getAtomicNumber();
//            if (atnum > heaviestAtNum) {
//                heaviest = i;
//                heaviestAtNum = atnum;
//            }
//        }
//        return heaviest;
//    }


}
