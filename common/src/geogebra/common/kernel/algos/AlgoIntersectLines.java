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
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.FreeVariable;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLines extends AlgoIntersectAbstract implements SymbolicParametersAlgo{

    private GeoLine g, h; // input
    private GeoPoint2 S; // output       
	private Polynomial[] polynomials;
	private Polynomial[] botanaPolynomials;
	private FreeVariable[] botanaVars;
	
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

	public int[] getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables) throws NoSymbolicParametersException {
		
		if (input[0] != null && input[1] != null && (input[0] instanceof SymbolicParametersAlgo) && (input[1] instanceof SymbolicParametersAlgo)){
			int[] degree1=((SymbolicParametersAlgo)input[0]).getFreeVariablesAndDegrees(freeVariables);
			int[] degree2=((SymbolicParametersAlgo)input[1]).getFreeVariablesAndDegrees(freeVariables);
			return SymbolicParameters.crossDegree(degree1, degree2);
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(final HashMap<FreeVariable,BigInteger> values) throws NoSymbolicParametersException {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates(values);
			BigInteger[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getExactCoordinates(values);
			if (coords1 != null && coords2 != null) {
				return SymbolicParameters.crossProduct(coords1, coords2);
			}
		}
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			Polynomial[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getPolynomials();
			Polynomial[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getPolynomials();
			if (coords1 != null && coords2 != null) {
				polynomials = SymbolicParameters.crossProduct(coords1, coords2);
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public FreeVariable[] getBotanaVars() {
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials() throws NoSymbolicParametersException {
		if (botanaPolynomials != null) {
			return botanaPolynomials;
		}
		if (input[0] != null && input[0] instanceof GeoLine){
			if (botanaVars==null){
				botanaVars = new FreeVariable[2];
				botanaVars[0]=new FreeVariable();
				botanaVars[1]=new FreeVariable();
			}
			// Storing determinant:
			FreeVariable[] fv = ((SymbolicParametersAlgo) input[0]).getBotanaVars();
			// a*d-b*c:
			Polynomial a = new Polynomial(fv[0]);
			Polynomial b = new Polynomial(fv[1]);
			Polynomial c = new Polynomial(fv[2]);
			Polynomial d = new Polynomial(fv[3]);
			botanaPolynomials = new Polynomial[2];
			botanaPolynomials[0] = a.multiply(d).subtract(b.multiply(c))
					// + e*(b-d)
					.add(new Polynomial(botanaVars[0]).multiply(b.subtract(d)))
					// - f*(a-c)
					.subtract(new Polynomial(botanaVars[1]).multiply(a.subtract(c)));
			
			fv = ((SymbolicParametersAlgo) input[1]).getBotanaVars();
			// a*d-b*c:
			a = new Polynomial(fv[0]);
			b = new Polynomial(fv[1]);
			c = new Polynomial(fv[2]);
			d = new Polynomial(fv[3]);
			botanaPolynomials[1] = a.multiply(d).subtract(b.multiply(c))
					// + e*(b-d)
					.add(new Polynomial(botanaVars[0]).multiply(b.subtract(d)))
					// - f*(a-c)
					.subtract(new Polynomial(botanaVars[1]).multiply(a.subtract(c)));
			return botanaPolynomials;
		}
		throw new NoSymbolicParametersException();
	}
}
