package uk.ac.ebi.optimise;

//import org.openscience.cdk.tools.MFAnalyser;

/**
 * The ReactionBalancer tries to modify the stoichiometric
 * factors of a reaction until the elemental and energy balance
 * are correct. Molecules may be added in the process. Standard
 * molecules to be added are Proton, Water and Oxygen(O2).
 *
 * @author Kai Hartmann
 * @author Pablo Moreno
 * @author John May
 *
 */
public class ReactionBalancer {

//    IMolecule[] balancingMols = null;
//    double[] balancingWeights = null;
//    int balancingLength;
//    double upper;
//    long timeout;
//    boolean modifyOriginalMols;
//    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( ReactionBalancer.class );
//    private Double chargeTolerance = 0.0;
//    private Double tolerance = 1e-5;
//
//    /**
//     * Singleton class access as this class is used to act on other objects
//     * only one instance is needed
//     * @return The instance of the reaction balancer
//     */
//    public static ReactionBalancer getInstance() {
//        return ReactionBalancerHolder.INSTANCE;
//    }
//
//    /**
//     * The singleton class holder for the instance
//     */
//    private static class ReactionBalancerHolder {
//
//        private static ReactionBalancer INSTANCE = new ReactionBalancer();
//    }
//
//    private HashMap<String , Double> getSymbolOccurrenceMapInReaction( IMoleculeSet reactants , IMoleculeSet products ) {
//
//        HashMap<String , Double> symbolOccMap = new HashMap<String , Double>();
//
//        for ( int i = 0; i < reactants.getAtomContainerCount(); ++i ) {
//            //MFAnalyser mfa = new MFAnalyser(educts.getMolecule(i));
//            //Map formula = mfa.getFormulaHashtable();
//            //Set set = formula.keySet();
//            IMolecularFormula molFormula = MolecularFormulaManipulator.getMolecularFormula( reactants.getMolecule( i ) );
//            logger.debug( "Reactant Mol formula:" + MolecularFormulaManipulator.getString( molFormula ) );
//            List<IElement> elements = MolecularFormulaManipulator.elements( molFormula );
//            double coeff = reactants.getMultiplier( i );
//
//            Iterator iter = elements.iterator();
//            while ( iter.hasNext() ) {
//                IElement element = ( IElement ) iter.next();
//
//                double toadd = -( ( Integer ) MolecularFormulaManipulator.getElementCount( molFormula , element ) ).doubleValue() * coeff;
//
//                if ( !symbolOccMap.containsKey( element.getSymbol() ) ) {
//                    symbolOccMap.put( element.getSymbol() , new Double( toadd ) );
//                } else {
//                    double cur = ( ( Double ) symbolOccMap.get( element.getSymbol() ) ).doubleValue();
//                    cur += toadd;
//                    symbolOccMap.put( element.getSymbol() , new Double( cur ) );
//                }
//            }
//
//
//        }
//
//        for ( int i = 0; i < products.getAtomContainerCount(); ++i ) {
//            //MFAnalyser mfa = new MFAnalyser(products.getMolecule(i));
//            //Map formula = mfa.getFormulaHashtable();
//            //Set set = formula.keySet();
//            IMolecularFormula molFormula = MolecularFormulaManipulator.getMolecularFormula( products.getMolecule( i ) );
//            logger.debug( "Product Mol formula:" + MolecularFormulaManipulator.getString( molFormula ) );
//            List<IElement> elements = MolecularFormulaManipulator.elements( molFormula );
//            double coeff = products.getMultiplier( i );
//            for ( Iterator iter = elements.iterator(); iter.hasNext(); ) {
//                //String symbol = (String)iter.next();
//                IElement element = ( IElement ) iter.next();
//                //double toadd = ((Integer)formula.get(symbol)).doubleValue() * coeff;
//                double toadd = ( ( Integer ) MolecularFormulaManipulator.getElementCount( molFormula , element ) ).doubleValue() * coeff;
//                if ( !symbolOccMap.containsKey( element.getSymbol() ) ) {
//                    symbolOccMap.put( element.getSymbol() , new Double( toadd ) );
//                } else {
//                    double cur = ( ( Double ) symbolOccMap.get( element.getSymbol() ) ).doubleValue();
//                    cur += toadd;
//                    symbolOccMap.put( element.getSymbol() , new Double( cur ) );
//                }
//                //System.out.println(element + ", new value: " + ((Double)symbolOccMap.get(element)).toString());
//                //System.out.println(element + ", new value: " + ((Double)symbolOccMap.get(element)).toString());
//            }
//        }
//        //System.out.println(symbolOccMap);
//        //System.out.println(symbolOccMap);
//        return symbolOccMap;
//    }
//
//    /**
//     * Constructor for ReactionBalancer
//     *
//     */
//    private ReactionBalancer() {
//        balancingMols = new IMolecule[ 3 ];
//        balancingWeights = new double[ 3 ];
//        balancingLength = 0;
//        upper = 20;
//        timeout = 0;
//        modifyOriginalMols = true;
//    }
//
//    /**
//     * Compare the charge of the reactants with the products
//     * @param reaction The reaction to compare charges of
//     * @return Whether the difference is greater then the charge tolerance (default: 0.0)
//     */
//    public boolean isChargedBalanced( IReaction reaction ) {
//
//        IMoleculeSet reactants = reaction.getReactants();
//        IMoleculeSet products = reaction.getProducts();
//
//        if ( Math.abs( AtomContainerSetManipulator.getTotalFormalCharge( reactants ) - AtomContainerSetManipulator.getTotalFormalCharge( products ) ) > chargeTolerance ) {
//            logger.debug( "Reactants charge:" + AtomContainerSetManipulator.getTotalFormalCharge( reactants ) );
//            logger.debug( "Products charge:" + AtomContainerSetManipulator.getTotalFormalCharge( products ) );
//            logger.debug( "Charge difference:" + Math.abs( AtomContainerSetManipulator.getTotalFormalCharge( reactants ) - AtomContainerSetManipulator.getTotalFormalCharge( products ) ) );
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Returns the charge tolerance used in {@see isChargedBalanced(IReaction)}
//     * @return Current value for charge tolerance
//     */
//    public Double getChargeTolerance() {
//        return chargeTolerance;
//    }
//
//    /**
//     * Set the charge tolerance value used in {@see isChargedBalanced(IReaction)}
//     * @param chargeRolerance The new charge tolerance
//     */
//    public void setChargeTolerance( Double chargeRolerance ) {
//        this.chargeTolerance = chargeRolerance;
//    }
//
//    /**
//     * Determine whether the reaction reactants and products are balanced. Method uses
//     * {@see isChargedBalanced(IReaction)} and {@see isMassBalanced(IReaction)}.
//     *
//     * @param reaction  Reaction to be checked for elemental and charge balance.
//     * @return          true if reaction is balanced, false otherwise.
//     */
//    public boolean isBalanced( IReaction reaction ) {
//        return isChargedBalanced( reaction ) && isMassBalanced( reaction ) ? true : false;
//    }
//
//    public HashMap<String , Double> getBalanceStatusPerElement( IReaction reaction ) {
//        return getSymbolOccurrenceMapInReaction( reaction.getReactants() , reaction.getProducts() );
//    }
//
//    /**
//     * Determines whether the mass of the reactants and products is balance within
//     * the mass balance tolerance (default: 1e-5). The elements of the reaction products
//     * and reactants are counted (and multipled by any coefficients)
//     *
//     * @param reaction The reaction to check the mass balance
//     * @return Whether the masses of the reaction compounds are balanced
//     */
//    public boolean isMassBalanced( IReaction reaction ) {
//
//        IMoleculeSet reactants = reaction.getReactants();
//        IMoleculeSet products = reaction.getProducts();
//
//        HashMap symbolOccMap = getSymbolOccurrenceMapInReaction( reactants , products );
//
//        Iterator iter = symbolOccMap.values().iterator();
//
//        while ( iter.hasNext() ) {
//            double cur = ( ( Double ) iter.next() ).doubleValue();
//            if ( Math.abs( cur ) > tolerance ) {
//                for ( Object symbol : symbolOccMap.keySet() ) {
//                    logger.debug( "Symbol: " + symbol + " Net:" + symbolOccMap.get( symbol ) );
//                }
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Set the tolerance used in {@see isMassBalanced(IReaction)}
//     * @return The current mass tolerance (default: 1e-5)
//     */
//    public Double getMassBalanceTolerance() {
//        return tolerance;
//    }
//
//    /**
//     * Set the tolerance used in {@see isMassBalanced(IReaction)}
//     * @param massTolerance the new massTolerance
//     */
//    public void setMassBalanceTolerance( Double massTolerance ) {
//        this.tolerance = massTolerance;
//    }
//
//    private HashMap<String , Integer> buildHashMap( IMolecule molecule ) {
//        IMolecularFormula molFormula = MolecularFormulaManipulator.getMolecularFormula( molecule );
//        List<IElement> elements = MolecularFormulaManipulator.elements( molFormula );
//        HashMap<String , Integer> molHash = new HashMap<String , Integer>();
//        //moleculeHashs.add(molHash);
//        //Set set = molHash.keySet();
//        for ( Iterator iter = elements.iterator(); iter.hasNext(); ) {
//            //String symbol = (String)iter.next();
//            //symbols.add(symbol);
//            IElement element = ( IElement ) iter.next();
//            molHash.put( element.getSymbol() , MolecularFormulaManipulator.getElementCount( molFormula , element ) );
//        }
//        return molHash;
//    }
//
//    /**
//     *
//     * @param reaction  Reaction to be balanced.
//     * @return 0 if balancing succeeded.
//     */
//    public int balance( IReaction reaction ) {
//        int ret = 99;
//
//        HashSet symbols = new HashSet();
////		int position = 1; // position 0 for charges
//        int reactantCount = reaction.getReactantCount();
//        int productCount = reaction.getProductCount();
//
//        int originalMoleculeCount = reactantCount + productCount;
//        int maxMoleculeCount = originalMoleculeCount + 2 * balancingLength;
//
//        List<HashMap> moleculeHashs = new ArrayList<HashMap>();
//
//        // array to store matches of molecules and balancing molecules
//        boolean[] originalAndBalancingMatch = new boolean[ originalMoleculeCount ];
//        IMoleculeSet reactants = reaction.getReactants();
//
//        for ( int i = 0; i < reactantCount; ++i ) {
//            IMolecule reactant = reactants.getMolecule( i );
//            //System.out.print(MolecularFormulaManipulator.getHTML(MolecularFormulaManipulator.getMolecularFormula(mol))+" + ");
//
//            // check if any of the reactants are isomorphs of the balancing molecules
//            for ( int j = 0; j < balancingLength; ++j ) {
//                if ( checkIsomorphism( reactant , balancingMols[j] ) ) {
//                    originalAndBalancingMatch[i] = true;
//                }
//            }
//            moleculeHashs.add( buildHashMap( reactant ) );
//        }
//
//        IMoleculeSet products = reaction.getProducts();
//        //System.out.println("\n\nProducts\n");
//        for ( int i = 0; i < productCount; ++i ) {
//            IMolecule product = products.getMolecule( i );
//            //System.out.print(MolecularFormulaManipulator.getHTML(MolecularFormulaManipulator.getMolecularFormula(mol))+" + ");
//            for ( int j = 0; j < balancingLength; ++j ) {
//                if ( checkIsomorphism( product , balancingMols[j] ) ) {
//                    originalAndBalancingMatch[i + reactantCount] = true;
//                }
//            }
//            moleculeHashs.add( buildHashMap( product ) );
//        }
//
//        for ( int i = 0; i < balancingLength; ++i ) {
//            IMolecule mol = balancingMols[i];
//            moleculeHashs.add( buildHashMap( mol ) );
//        }
//
//
//        // add all the symbols to the symbols hashset
//        for ( HashMap<String , Integer> map : moleculeHashs ) {
//            symbols.addAll( map.keySet() );
//        }
//
//
//        // using the LPSolve library (linear programming)
//        try {
//
//            LpSolve solver = LpSolve.makeLp( 0 , maxMoleculeCount );
//            solver.setVerbose( 3 );
//            solver.setTimeout( timeout );
//            solver.setBbDepthlimit( -100 );
//
//            // set objective function
//            {
//                double[] row = new double[ maxMoleculeCount + 1 ]; // first entry reserved
//                int count = 1;
//                for ( int i = 0; i < reactantCount; ++i , ++count ) {
//                    row[count] = 1;
//                    solver.setInt( count , true );
//                }
//                for ( int i = 0; i < productCount; ++i , ++count ) {
//                    row[count] = 1;
//                    solver.setInt( count , true );
//                }
//                for ( int i = 0; i < balancingLength; ++i , ++count ) {
//                    row[count] = balancingWeights[i];
//                    solver.setInt( count , true );
//                    row[++count] = balancingWeights[i];
//                    solver.setInt( count , true );
//                }
//                solver.setObjFn( row );
//            }
//
//            // set rows (constraints)
//            solver.setAddRowmode( true );
//            for ( Iterator iter = symbols.iterator(); iter.hasNext(); ) {
//                String symbol = ( String ) iter.next();
//                double[] row = new double[ maxMoleculeCount + 1 ];
//                int count = 1;
//                for ( int i = 0; i < reactantCount; ++i , ++count ) {
//                    Integer value = ( Integer ) ( ( HashMap ) moleculeHashs.get( i ) ).get( symbol );
//                    if ( value != null ) {
//                        row[count] = -value.doubleValue();
//                    } else {
//                        row[count] = 0.0;
//                    }
//                }
//                for ( int i = reactantCount; i < originalMoleculeCount; ++i , ++count ) {
//                    Integer value = ( Integer ) ( ( HashMap ) moleculeHashs.get( i ) ).get( symbol );
//                    if ( value != null ) {
//                        row[count] = value.doubleValue();
//                    } else {
//                        row[count] = 0.0;
//                    }
//                }
//                for ( int i = 0; i < balancingLength; ++i , ++count ) {
//                    Integer value = ( Integer ) ( ( HashMap ) moleculeHashs.get( i + originalMoleculeCount ) ).get( symbol );
//                    if ( value != null ) {
//                        row[count] = -value.doubleValue();
//                        row[++count] = value.doubleValue();
//                    } else {
//                        row[count] = 0.0;
//                        row[++count] = 0.0;
//                    }
//                }
//                solver.addConstraint( row , LpSolve.EQ , 0.0 );
//            }
//
//            // add last row for charges
//            {
//                double[] row = new double[ maxMoleculeCount + 1 ]; // first entry reserved
//                int count = 1;
//                for ( int i = 0; i < reactantCount; ++i , ++count ) {
//                    row[count] = -AtomContainerManipulator.getTotalFormalCharge( reactants.getAtomContainer( i ) );
//                }
//                for ( int i = 0; i < productCount; ++i , ++count ) {
//                    row[count] = AtomContainerManipulator.getTotalFormalCharge( products.getAtomContainer( i ) );
//                }
//                for ( int i = 0; i < balancingLength; ++i , ++count ) {
//                    double charge = AtomContainerManipulator.getTotalFormalCharge( balancingMols[i] );
//                    row[count] = -charge;
//                    row[++count] = charge;
//                }
//                solver.addConstraint( row , LpSolve.EQ , 0.0 );
//            }
//            solver.setAddRowmode( false );
//
//
//            // Set the boundary conditions
//            if ( !this.modifyOriginalMols ) {
//                // if original mols should not be modified, reset the lower & upper bound to the stoich values.
//                Double[] eductStoich = reaction.getReactantCoefficients();
//                for ( int i = 0; i < reactantCount; ++i ) {
//                    if ( originalAndBalancingMatch[i] ) {
//                        solver.setLowbo( i + 1 , 0.0 );
//                        solver.setUpbo( i + 1 , upper );
//                    } else {
//                        solver.setLowbo( i + 1 , eductStoich[i] );
//                        solver.setUpbo( i + 1 , eductStoich[i] );
//                    }
//                }
//                Double[] productStoich = reaction.getProductCoefficients();
//                for ( int i = 0; i < productCount; ++i ) {
//                    if ( originalAndBalancingMatch[i + reactantCount] ) {
//                        solver.setLowbo( i + reactantCount + 1 , 0.0 ); // eductCount was missing here
//                        solver.setUpbo( i + reactantCount + 1 , upper ); // eductCount was missing here
//                    } else {
//                        solver.setLowbo( i + reactantCount + 1 , productStoich[i] );
//                        solver.setUpbo( i + reactantCount + 1 , productStoich[i] );
//                    }
//                }
//                for ( int i = originalMoleculeCount; i < maxMoleculeCount; ++i ) {
//                    solver.setUpbo( i + 1 , upper );
//                }
//            } else {
//                // set lower bounds for original molecules from 0 to 1
//                for ( int i = 0; i < originalMoleculeCount; ++i ) {
//                    // do not raise lower bound if original molecule has same sum
//                    // formula as one of the balancing molecules. Otherwise, it may
//                    // happen that the molcule appears with stoichiometry 1 on both
//                    // sides of the equation.
//                    if ( originalAndBalancingMatch[i] ) {
//                        continue;
//                    }
//                    solver.setLowbo( i + 1 , 1 );
//                }
//
//                // set upper bounds for all molecules to 30
//                for ( int i = 0; i < maxMoleculeCount; ++i ) {
//                    solver.setUpbo( i + 1 , upper );
//                }
//            }
//
//            ret = solver.solve();
//
//            //solver.printLp();
//            //solver.printObjective();
//            //solver.printSolution(totalMolCount);
//            //solver.printConstraints(totalMolCount);
//
//            if ( ret == 0 ) {
//                double[] vars = new double[ maxMoleculeCount ];
//                solver.getVariables( vars );
//
//                int count = 0;
//                for ( int i = 0; i < reactantCount; ++i , ++count ) {
//                    reactants.setMultiplier( i , vars[count] );
//                }
//                for ( int i = 0; i < productCount; ++i , ++count ) {
//                    products.setMultiplier( i , vars[count] );
//                }
//                for ( int i = 0; i < balancingLength; ++i , ++count ) {
//                    if ( vars[count] > 1e-5 ) {
//                        System.out.println( "adding reactant" );
//                        reactants.addAtomContainer( balancingMols[i] , vars[count] );
//                    }
//                    ++count;
//                    if ( vars[count] > 1e-5 ) {
//                        System.out.println( "Adding product" );
//                        products.addAtomContainer( balancingMols[i] , vars[count] );
//                    }
//                }
//
//                removeZeroStoichMolecules( reaction );
//
//            }
//        } catch ( LpSolveException e ) {
//            e.printStackTrace();
//        }
//
//        return ret;
//    }
//
//    /**
//     * However, it may modify the original molecules if they are isomorph
//     * to a balancing molecule.
//     * @param value
//     */
//    public void setModifyOriginalMols( boolean value ) {
//        this.modifyOriginalMols = value;
//    }
//
//    public void addMoleculeForBalancing( IMolecule ac ) {
//        addMoleculeForBalancing( ac , 5 );
//    }
//
//    public void addMoleculeForBalancing( IMolecule ac , double weight ) {
//        if ( balancingLength == balancingMols.length ) {
//            IMolecule[] tmpMols = new IMolecule[ balancingLength + 1 ];
//            double[] tmpWeights = new double[ balancingLength + 1 ];
//            System.arraycopy( balancingMols , 0 , tmpMols , 0 , balancingLength );
//            System.arraycopy( balancingWeights , 0 , tmpWeights , 0 , balancingLength );
//            balancingMols = tmpMols;
//            balancingWeights = tmpWeights;
//        }
//        balancingMols[balancingLength] = ac;
//        balancingWeights[balancingLength] = weight;
//        ++balancingLength;
//    }
//
//    public void initWithProtonOnly() {
//        IMolecule proton = new Molecule();
//        IAtom h = new Atom( "H" );
//        h.setFormalCharge( 1 );
//        proton.addAtom( h );
//        proton.setProperty( "name" , "Proton" );
//        addMoleculeForBalancing( proton );
//    }
//
//    public void initWithStandardMols() {
//        IMolecule proton = new Molecule();
//        IAtom h = new Atom( "H" );
//        h.setFormalCharge( 1 );
//        proton.addAtom( h );
//        proton.setProperty( "name" , "Proton" );
//        proton.setID( "Proton" );
//        IMolecule h2o = new Molecule();
//        IAtom h1 = new Atom( "H" );
//        IAtom h2 = new Atom( "H" );
//        IAtom o1 = new Atom( "O" );
//        IBond bond1 = new Bond( h1 , o1 , IBond.Order.SINGLE );
//        IBond bond2 = new Bond( h2 , o1 , IBond.Order.SINGLE );
//        h2o.addAtom( h1 );
//        h2o.addAtom( h2 );
//        h2o.addAtom( o1 );
//        h2o.addBond( bond1 );
//        h2o.addBond( bond2 );
//        h2o.setProperty( "name" , "Water" );
//        h2o.setID( "Water" );
//        IMolecule oxygen = new Molecule();
//        IAtom o2 = new Atom( "O" );
//        IAtom o3 = new Atom( "O" );
//        IBond bond3 = new Bond( o2 , o3 , IBond.Order.DOUBLE );
//        oxygen.addAtom( o2 );
//        oxygen.addAtom( o3 );
//        oxygen.addBond( bond3 );
//        oxygen.setProperty( "name" , "Oxygen" );
//        oxygen.setID( "Oxygen" );
//
//        addMoleculeForBalancing( proton );
//        addMoleculeForBalancing( h2o );
//        addMoleculeForBalancing( oxygen );
//    }
//
//    public void setTimeout( long value ) {
//        this.timeout = value;
//    }
//
//    /**
//     * Determine whether the structures are isomorphs (i.e. similar). This uses
//     * structure matching of CDK UniversialIsomorphismTester
//     * @param ac1
//     * @param ac2
//     * @return
//     */
//    private boolean checkIsomorphism( IAtomContainer ac1 , IAtomContainer ac2 ) {
//
//        if ( AtomContainerManipulator.getTotalFormalCharge( ac1 ) != AtomContainerManipulator.getTotalFormalCharge( ac2 ) ) {
//            return false;
//        }
//
//        try {
//            return UniversalIsomorphismTester.isIsomorph( ac1 , ac2 );
//        } catch ( CDKException e ) {
//            // should never throw
//        }
//        return false;
//    }
//
//    private void removeZeroStoichMolecules( IReaction reaction ) {
//        IMoleculeSet educts = reaction.getReactants();
//        for ( int i = 0; i < educts.getAtomContainerCount(); ++i ) {
//            if ( educts.getMultiplier( i ) == 0.0 ) {
//                educts.removeAtomContainer( i );
//                --i;
//            }
//        }
//        IMoleculeSet products = reaction.getProducts();
//        for ( int i = 0; i < products.getAtomContainerCount(); ++i ) {
//            if ( products.getMultiplier( i ) == 0.0 ) {
//                products.removeAtomContainer( i );
//                --i;
//            }
//        }
//
//    }
}
