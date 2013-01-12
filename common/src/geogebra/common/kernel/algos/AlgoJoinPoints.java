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

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.App;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPoints extends AlgoElement implements SymbolicParametersAlgo,
	SymbolicParametersBotanaAlgo {

    private GeoPoint P, Q;  // input
    private GeoLine  g;     // output       
	private Polynomial[] polynomials;
	private Variable[] botanaVars;
        
    /** Creates new AlgoJoinPoints */
    public AlgoJoinPoints(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        this(cons, P, Q);
        g.setLabel(label);
    }   
    
    public AlgoJoinPoints(Construction cons, GeoPoint P, GeoPoint Q) {
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
    
    /**
     * @author Tam
     * 
     * for special cases of e.g. AlgoIntersectLineConic
     */
    private void addIncidence() {
        P.addIncidence(g);
        Q.addIncidence(g);
	}

    @Override
	public Commands getClassName() {
		return Commands.Line;
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
    // Made public for LocusEqu
    public GeoPoint getP() { return P; }
    //Made public for LocusEqu
    public GeoPoint getQ() { return Q; }
    
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
		return new SymbolicParameters(this);
	}

	public void getFreeVariables(HashSet<Variable> variables) throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			P.getFreeVariables(variables);
			Q.getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
		
	}
	
	public int[] getDegrees() throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			int[] degree1=P.getDegrees();
			int[] degree2=Q.getDegrees();
			return SymbolicParameters.crossDegree(degree1, degree2);
		}
		throw new NoSymbolicParametersException();
		
	}

	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			BigInteger[] coords1 = P.getExactCoordinates(values);
			BigInteger[] coords2 = Q.getExactCoordinates(values);
			if (coords1 != null && coords2 != null) {
				return SymbolicParameters.crossProduct(coords1, coords2);
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (polynomials != null) {
			return polynomials;
		}
		if (P != null && Q != null) {
			Polynomial[] coords1 = P.getPolynomials();
			Polynomial[] coords2 = Q.getPolynomials();
			if (coords1 != null && coords2 != null) {
				polynomials = Polynomial.crossProduct(coords1, coords2);
				App.debug("polys(" + g.getLabelSimple()
					+ "): "
					+ polynomials[0].toString() + ","
					+ polynomials[1].toString() + ","
					+ polynomials[2].toString());
					
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}

	public Variable[] getBotanaVars(GeoElement geo) {
		if (botanaVars != null)
			return botanaVars;
		botanaVars = SymbolicParameters.addBotanaVarsJoinPoints(input);
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials(GeoElement geo)
			throws NoSymbolicParametersException {
		// It's OK, polynomials for lines are only created when a third point is lying on them, too:
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
	
	public EquationElementInterface buildEquationElementForGeo(GeoElement geo, EquationScopeInterface scope) {
		return LocusEquation.eqnJoinPoints(geo, this, scope);
	}
}
