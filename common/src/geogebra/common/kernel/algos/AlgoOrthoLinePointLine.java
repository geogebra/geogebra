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

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
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
public class AlgoOrthoLinePointLine extends AlgoElement implements SymbolicParametersAlgo,
SymbolicParametersBotanaAlgo {

    private GeoPoint P; // input
    private GeoLine l; // input
    private GeoLine g; // output       
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;

    /** Creates new AlgoOrthoLinePointLine 
     * @param cons 
     * @param label 
     * @param P 
     * @param l */
    public AlgoOrthoLinePointLine(
        Construction cons,
        String label,
        GeoPoint P,
        GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();

        g.setLabel(label);
        addIncidence();
    }

    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
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
    
    // Made public for LocusEqu
    public GeoPoint getP() {
        return P;
    }
    
    // Made public for LocusEqu
    public GeoLine getl() {
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

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (P != null && l != null) {
			P.getFreeVariables(variables);
			l.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if (P != null && l != null) {
			int[] degreeP = P.getDegrees();
			int[] degreeL = l.getDegrees();
			
			int[] result =new int[3];
			result[0]=degreeL[1]+degreeP[2];
			result[1] = degreeL[0]+degreeP[2];
			result[2] = Math.max(degreeL[0]+degreeP[1],degreeL[1]+degreeP[0]);
			return result;
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			HashMap<Variable, BigInteger> values) throws NoSymbolicParametersException {
		if (P != null && l != null) {
			BigInteger[] pP = P.getExactCoordinates(values);
			BigInteger[] pL = l.getExactCoordinates(values);
			BigInteger[] coords = new BigInteger[3];
			coords[0] = pL[1].multiply(pP[2]).negate();
			coords[1] = pL[0].multiply(pP[2]);
			coords[2] = pL[0].multiply(pP[1]).negate().add(
					pL[1].multiply(pP[0]));
			return coords;
		}
		throw new NoSymbolicParametersException();
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
			polynomials[2] = pL[0].multiply(pP[1]).negate().add(
					pL[1].multiply(pP[0]));
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {

		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (P != null && l != null){
			Variable[] vP = P.getBotanaVars(P);
			Variable[] vL = l.getBotanaVars(l);
			
			if (botanaVars==null){
				botanaVars = new Variable[4]; // storing 2 new variables, plus the coordinates of P
				botanaVars[0]=new Variable();
				botanaVars[1]=new Variable();
				botanaVars[2]=vP[0];
				botanaVars[3]=vP[1];
			}
			
			botanaPolynomials = new Polynomial[2];
			// The two points of line and the intersection (Botana) point of the perpendicular are collinear:  
			botanaPolynomials[0] = Polynomial.collinear(vL[0], vL[1], vL[2], vL[3], botanaVars[0], botanaVars[1]);
			
			// The perpendicularity condition:
			botanaPolynomials[1] = Polynomial.perpendicular(botanaVars[0], botanaVars[1], vP[0], vP[1], vL[0], vL[1], vL[2], vL[3]); 
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
