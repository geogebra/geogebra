/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.AlgoMidpointND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMidpoint extends AlgoMidpointND implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo, RestrictionAlgoForLocusEquation {
      
    private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
    
	public AlgoMidpoint(Construction cons, String label, GeoPoint P, GeoPoint Q) {
    	this(cons, P, Q);
    	getPoint().setLabel(label);
    }
	
    public AlgoMidpoint(Construction cons, GeoPoint P, GeoPoint Q) {
        super(cons,P,Q);
    }

	@Override
	protected GeoPointND newGeoPoint(Construction cons) {
		
		return new GeoPoint(cons);
	}
   
    @Override
	public GeoPoint getPoint() {
        return (GeoPoint) super.getPoint();
    }
    
    @Override
	protected void copyCoords(GeoPointND point){
    	getPoint().setCoords((GeoPoint) point);
    }
    
    // Made public for LocusEqu
    @Override
    public GeoPoint getP() { return (GeoPoint) super.getP(); }
    
    // Made public for LocusEqu
    @Override
    public GeoPoint getQ() { return (GeoPoint) super.getQ(); }
    
    @Override
	protected void computeMidCoords(){
    	
    	GeoPoint P = (GeoPoint) getP();
        GeoPoint Q = (GeoPoint) getQ();
        
    	getPoint().setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		GeoPoint P=(GeoPoint) getP();
		GeoPoint Q=(GeoPoint) getQ();
		if (P != null && Q != null) {
			P.getFreeVariables(variables);
			Q.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		GeoPoint P=(GeoPoint) getP();
		GeoPoint Q=(GeoPoint) getQ();
		if (P != null && Q != null) {
			int[] degreeP = P.getDegrees();
			int[] degreeQ = Q.getDegrees();
			
			int[] result =new int[3];
			result[0]=Math.max(degreeP[0]+degreeQ[2],degreeQ[0]+degreeP[2]);
			result[1] = Math.max(degreeP[1]+degreeQ[2],degreeQ[1]+degreeP[2]);
			result[2] = degreeP[2]+degreeQ[2];
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		GeoPoint P=getP();
		GeoPoint Q=getQ();
		if (P != null && Q != null) {
			BigInteger[] pP = P.getExactCoordinates(values);
			BigInteger[] pQ = Q.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = pP[0].multiply(pQ[2]).add(pQ[0].multiply(pP[2]));
			coords[1] = pP[1].multiply(pQ[2]).add(pQ[1].multiply(pP[2]));
			coords[2] = pP[2].multiply(pQ[2]).multiply(BigInteger.valueOf(2));
			return coords;
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		GeoPoint P=(GeoPoint) getP();
		GeoPoint Q=(GeoPoint) getQ();
		if (P != null && Q != null) {
			Polynomial[] pP = P.getPolynomials();
			Polynomial[] pQ = Q.getPolynomials();
			polynomials = new Polynomial[3];
			polynomials[0] = pP[0].multiply(pQ[2]).add(pQ[0].multiply(pP[2]));
			polynomials[1] = pP[1].multiply(pQ[2]).add(pQ[1].multiply(pP[2]));
			polynomials[2] = pP[2].multiply(pQ[2]).multiply(new Polynomial(2));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}

		GeoPoint P=(GeoPoint) getP();
		GeoPoint Q=(GeoPoint) getQ();

		if (P == null || Q == null)
			throw new NoSymbolicParametersException();
			
		if (botanaVars==null){
			botanaVars = new Variable[2];
			botanaVars[0]=new Variable();
			botanaVars[1]=new Variable();
		}

		botanaPolynomials = SymbolicParameters.botanaPolynomialsMidpoint(P,Q,botanaVars);
		return botanaPolynomials;
		
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
