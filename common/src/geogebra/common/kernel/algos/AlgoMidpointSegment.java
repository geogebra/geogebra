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

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
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
public class AlgoMidpointSegment extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo, RestrictionAlgoForLocusEquation {

    private GeoSegment segment; // input
    private GeoPoint M; // output        
    private GeoPoint P, Q; // endpoints of segment

	private Polynomial[] polynomials;
	private Variable[] botanaVars;
	private Polynomial[] botanaPolynomials;

    
	/** Creates new AlgoVector */
    public AlgoMidpointSegment(Construction cons, String label, GeoSegment segment) {
    	this(cons, segment);
    	M.setLabel(label);
    }
	
    AlgoMidpointSegment(Construction cons, GeoSegment segment) {
        super(cons);
        this.segment = segment;
        
        // create new Point
        M = new GeoPoint(cons);
        setInputOutput();
        
        P = segment.getStartPoint();
    	Q = segment.getEndPoint();

        // compute M = (P + Q)/2
        compute();        
    }

    @Override
	public Algos getClassName() {
        return Algos.AlgoMidpointSegment;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_MIDPOINT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = segment;        

        super.setOutputLength(1);
        super.setOutput(0, M);
        setDependencies(); // done by AlgoElement
    }

    // Created for LocusEqu
    public GeoPoint getP() {
    	return P;
    }
    
    // Created for LocusEqu
    public GeoPoint getQ() {
    	return Q;
    }
    
    public GeoPoint getPoint() {
        return M;
    }

    // calc midpoint
    @Override
	public final void compute() {
        boolean pInf = P.isInfinite();
        boolean qInf = Q.isInfinite();

        if (!pInf && !qInf) {
            // M = (P + Q) / 2          
            M.setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
        } else if (pInf && qInf)
            M.setUndefined();
        else if (pInf)
            M.setCoords(P);
        else // qInf
            M.setCoords(Q);
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("MidpointOfA",segment.getLabel(tpl));

    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			P.getFreeVariables(variables);
			Q.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
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
