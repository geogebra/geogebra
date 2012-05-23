/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLines extends AlgoIntersectAbstract implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo {

    private GeoLine g, h; // input
    private GeoPoint2 S; // output       
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
    /** Creates new AlgoJoinPoints */
    public AlgoIntersectLines(Construction cons, String label, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        S = new GeoPoint2(cons);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        setIncidence();
        
        S.setLabel(label);
    }

    private void setIncidence() {
		S.addIncidence(g);
		S.addIncidence(h);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoIntersectLines;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = h;

        super.setOutputLength(1);
        super.setOutput(0, S);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint2 getPoint() {
        return S;
    }
    
    GeoLine geth() {
        return g;
    }
    
    GeoLine getg() {
        return h;
    }

    // calc intersection S of lines g, h
    @Override
	public final void compute() {   	
        GeoVec3D.cross(g, h, S); 
              
        // test the intersection point
        // this is needed for the intersection of segments
        if (S.isDefined()) {
        	if (!(g.isIntersectionPointIncident(S, Kernel.MIN_PRECISION) &&
			      h.isIntersectionPointIncident(S, Kernel.MIN_PRECISION)) )
				S.setUndefined();
        }
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IntersectionPointOfAB",g.getLabel(tpl),h.getLabel(tpl));

    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			int[] degree1 = g.getFreeVariablesAndDegrees(variables);
			int[] degree2 = h.getFreeVariablesAndDegrees(variables);
			return SymbolicParameters.crossDegree(degree1, degree2);
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(
			final HashMap<Variable, BigInteger> values)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			BigInteger[] coords1 = g.getExactCoordinates(values);
			BigInteger[] coords2 = h.getExactCoordinates(values);
			return SymbolicParameters.crossProduct(coords1, coords2);
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			Polynomial[] coords1 = g.getPolynomials();
			Polynomial[] coords2 = h.getPolynomials();
			polynomials = Polynomial.crossProduct(coords1, coords2);
			return polynomials;
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars() {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials() throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		// We cannot decide a statement properly if any of the inputs is a segment:
		if (g != null && h != null && !g.isGeoSegment() && !h.isGeoSegment()) {
			if (botanaVars==null){
				botanaVars = new Variable[2];
				botanaVars[0]=new Variable();
				botanaVars[1]=new Variable();
			}
			Variable[] fv = g.getBotanaVars();
			botanaPolynomials = new Polynomial[2];
			botanaPolynomials[0] = Polynomial.collinear(fv[0], fv[1], fv[2], fv[3], botanaVars[0], botanaVars[1]); 
			fv = h.getBotanaVars();
			botanaPolynomials[1] = Polynomial.collinear(fv[0], fv[1], fv[2], fv[3], botanaVars[0], botanaVars[1]); 
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}
}
