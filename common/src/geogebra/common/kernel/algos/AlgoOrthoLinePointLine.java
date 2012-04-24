/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoLinePointLine.java
 *
 * line through P orthogonal to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.FreeVariable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrthoLinePointLine extends AlgoElement implements SymbolicParametersAlgo{

    private GeoPoint2 P; // input
    private GeoLine l; // input
    private GeoLine g; // output       
	private Polynomial[] polynomials;

    /** Creates new AlgoOrthoLinePointLine 
     * @param cons 
     * @param label 
     * @param P 
     * @param l */
    public AlgoOrthoLinePointLine(
        Construction cons,
        String label,
        GeoPoint2 P,
        GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        setIncidence();
        g.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence(g);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoOrthoLinePointLine;
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }  

    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        setOutputLength(1);
        setOutput(0,g);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getLine() {
        return g;
    }
    
    GeoPoint2 getP() {
        return P;
    }
    
    GeoLine getl() {
        return l;
    }

    // calc the line g through P and normal to l   
    @Override
	public final void compute() {
        GeoVec3D.cross(P, l.x, l.y, 0.0, g);
    }

    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAPerpendicularToB",P.getLabel(tpl),l.getLabel(tpl));

    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables)
			throws NoSymbolicParametersException {
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<FreeVariable, BigInteger> values) {
		// TODO Auto-generated method stub
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (P != null && l != null) {
			Polynomial[] pP = P.getPolynomials();
			Polynomial[] pL = l.getPolynomials();
			polynomials = new Polynomial[3];
			polynomials[0] = pL[1].multiply(pP[2]).negate();
			polynomials[1] = pL[0].multiply(pP[2]);
			polynomials[2] = pL[0].multiply(pP[1]).add(
					pL[1].multiply(pP[0]).negate());
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}
}
