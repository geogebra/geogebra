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

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.RestrictionAlgoForLocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
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
public class AlgoIntersectLines extends AlgoIntersectAbstract implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo, RestrictionAlgoForLocusEquation {

    private GeoLine g, h; // input
    private GeoPoint S; // output       
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private Variable[] botanaVars;
	
    /** Creates new AlgoJoinPoints */
    public AlgoIntersectLines(Construction cons, String label, GeoLine g, GeoLine h) {
        super(cons);
        this.g = g;
        this.h = h;
        S = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();

        S.setLabel(label);
        addIncidence();
        
    }

	 /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
		S.addIncidence(g);
		S.addIncidence(h);
	}

    @Override
	public Commands getClassName() {
        return Commands.Intersect;
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

    public GeoPoint getPoint() {
        return S;
    }

    // Made public for LocusEqu
    public GeoLine geth() {
        return g;
    }
    
    // Made public for LocusEqu
    public GeoLine getg() {
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
        return loc.getPlain("IntersectionPointOfAB",g.getLabel(tpl),h.getLabel(tpl));

    }

	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			g.getFreeVariables(variables);
			h.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees()
			throws NoSymbolicParametersException {
		if ((g instanceof GeoSegment) || (h instanceof GeoSegment)){
			throw new NoSymbolicParametersException();
		}
		if (g != null && h != null) {
			int[] degree1 = g.getDegrees();
			int[] degree2 = h.getDegrees();
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

	public Variable[] getBotanaVars(GeoElement geo) {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException {
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
			Variable[] fv = g.getBotanaVars(g);
			botanaPolynomials = new Polynomial[2];
			botanaPolynomials[0] = Polynomial.collinear(fv[0], fv[1], fv[2], fv[3], botanaVars[0], botanaVars[1]); 
			fv = h.getBotanaVars(h);
			botanaPolynomials[1] = Polynomial.collinear(fv[0], fv[1], fv[2], fv[3], botanaVars[0], botanaVars[1]); 
					
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnIntersectLines(geo, this, scope);
	}
}
