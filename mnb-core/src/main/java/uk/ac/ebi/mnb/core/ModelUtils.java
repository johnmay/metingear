/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.mnb.core;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;


/**
 *
 * @author johnmay
 * @date   Apr 26, 2011
 */
public class ModelUtils {

    private static final org.apache.log4j.Logger logger =
                                                 org.apache.log4j.Logger.getLogger(ModelUtils.class);
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();


    private ModelUtils() {
    }


    public static ModelUtils getInstance() {
        return UtilsHolder.INSTANCE;
    }


    private static class UtilsHolder {

        private static final ModelUtils INSTANCE = new ModelUtils();
    }


    public static IMolecule makeProton() {

        IMolecule mol = builder.newInstance(IMolecule.class);
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.setProperty("name", "Proton");
        mol.setID("Proton");
        return mol;
    }


    public static IMolecule makeWater() {
        IMolecule mol = SilentChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addAtom(builder.newInstance(IAtom.class, "O"));
        mol.addAtom(builder.newInstance(IAtom.class, "H"));
        mol.addBond(builder.newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1)));
        mol.addBond(builder.newInstance(IBond.class, mol.getAtom(1), mol.getAtom(2)));
        mol.setProperty("name", "Water");
        mol.setID("Water");
        return mol;
    }


    public static IMolecule makeOxygen() {
        IMolecule mol = SilentChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        mol.addAtom(builder.newInstance(IAtom.class, "O"));
        mol.addAtom(builder.newInstance(IAtom.class, "O"));
        mol.addBond(builder.newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1)));
        mol.setProperty("name", "Oxygen");
        mol.setID("Oxygen");
        return mol;
    }


}

