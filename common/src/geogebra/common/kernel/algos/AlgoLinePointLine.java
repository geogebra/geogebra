/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointLine.java
 *
 * line through P parallel to l
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
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoLinePointLine extends AlgoElement implements SymbolicParametersAlgo{

    private GeoPoint2 P; // input
    private GeoLine l; // input
    private GeoLine g; // output       
	private Polynomial[] polynomials;

    /** Creates new AlgoLinePointLine */
    public AlgoLinePointLine(Construction cons, String label, GeoPoint2 P, GeoLine l) {
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
        return Algos.AlgoLinePointLine;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_PARALLEL;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        super.setOutputLength(1);
        super.setOutput(0, g);
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

    // calc the line g through P and parallel to l   
    @Override
	public final void compute() {
        // homogenous:
        GeoVec3D.cross(P, l.y, -l.x, 0.0, g);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("LineThroughAParallelToB",P.getLabel(tpl),l.getLabel(tpl));

    }

    // Simon Weitzhofer 2012-05-07
    
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		
		if (P != null && l != null){
			int[] degreeP = P.getFreeVariablesAndDegrees(variables);
			int[] degreeL = l.getFreeVariablesAndDegrees(variables);
			int[] degrees = new int[3];
			degrees[0]=degreeL[0]+degreeP[2];
			degrees[1]=degreeL[1]+degreeP[2];
			degrees[2]=Math.max(degreeL[0]+degreeP[0],degreeL[1]+degreeP[1]);
			return degrees;
			
		}
		
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		
		if (P != null && l != null){
			BigInteger[] coordsP = P.getExactCoordinates(values);
			BigInteger[] coordsL = l.getExactCoordinates(values);
			BigInteger[] coords=new BigInteger[3];
			coords[0]=coordsL[0].multiply(coordsP[2]);
			coords[1]=coordsL[1].multiply(coordsP[2]);
			coords[2]=coordsL[0].multiply(coordsP[0]).add(coordsL[1].multiply(coordsP[1])).negate();
			return coords;
		}
		
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		
		if (polynomials!=null){
			return polynomials;
		}
		
		if (P != null && l != null){
			Polynomial[] coordsP = P.getPolynomials();
			Polynomial[] coordsl = l.getPolynomials();
			polynomials=new Polynomial[3];
			polynomials[0]=coordsl[0].multiply(coordsP[2]);
			polynomials[1]=coordsl[1].multiply(coordsP[2]);
			polynomials[2]=coordsl[0].multiply(coordsP[0]).add(coordsl[1].multiply(coordsP[1])).negate();
			return polynomials;
		}
		
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars() {
		// TODO Auto-generated method stub
		return null;
	}

	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		// TODO Auto-generated method stub
		return null;
	}
}
