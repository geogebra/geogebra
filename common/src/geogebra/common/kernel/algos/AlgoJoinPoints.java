/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPoints.java
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
import geogebra.common.main.AbstractApplication;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPoints extends AlgoElement implements SymbolicParametersAlgo {

    private GeoPoint2 P, Q;  // input
    private GeoLine  g;     // output       
	private Polynomial[] polynomials;
        
    /** Creates new AlgoJoinPoints */
    public AlgoJoinPoints(Construction cons, String label, GeoPoint2 P, GeoPoint2 Q) {
        this(cons, P, Q);
        g.setLabel(label);
    }   
    
    public AlgoJoinPoints(Construction cons, GeoPoint2 P, GeoPoint2 Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        g = new GeoLine(cons); 
        g.setStartPoint(P);
        g.setEndPoint(Q);
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        addIncidence();
    }   
    
    private void addIncidence() {
        P.addIncidence(g);
        Q.addIncidence(g);
	}

	@Override
	public Algos getClassName() {
        return Algos.AlgoJoinPoints;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_JOIN;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
         
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    GeoPoint2 getP() { return P; }
    GeoPoint2 getQ() { return Q; }
    
    // calc the line g through P and Q    
    @Override
	public final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        GeoVec3D.lineThroughPoints(P, Q, g);
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
     
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("LineThroughAB",P.getLabel(tpl),Q.getLabel(tpl));

    }

    //Simon Weitzhofer 2012-04-03
	public SymbolicParameters getSymbolicParameters() {
		// only makes sense if the predecessors also have SymbolicParameters
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			return new SymbolicParameters(this);
		}
		return null;
	}

	public int[] getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables) throws NoSymbolicParametersException {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			int[] degree1=((SymbolicParametersAlgo) input[0]).getFreeVariablesAndDegrees(freeVariables);
			int[] degree2=((SymbolicParametersAlgo) input[1]).getFreeVariablesAndDegrees(freeVariables);
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
				AbstractApplication.debug("polys(" + g.getLabelSimple()
					+ "): "
					+ polynomials[0].toString() + ","
					+ polynomials[1].toString() + ","
					+ polynomials[2].toString());
					
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public FreeVariable[] getBotanaVars() {
		FreeVariable[] vars = new FreeVariable[4];
		FreeVariable[] line1vars = new FreeVariable[2];
		FreeVariable[] line2vars = new FreeVariable[2];
		line1vars = ((SymbolicParametersAlgo) input[0]).getBotanaVars();
		line2vars = ((SymbolicParametersAlgo) input[1]).getBotanaVars();
		vars[0] = line1vars[0];
		vars[1] = line1vars[1];
		vars[2] = line2vars[0];
		vars[3] = line2vars[1];
		return vars;
	}

	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		return null;
	}
}
