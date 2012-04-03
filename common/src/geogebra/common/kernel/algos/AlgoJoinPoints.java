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
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.FreeVariable;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPoints extends AlgoElement implements SymbolicParametersAlgo {

    private GeoPoint2 P, Q;  // input
    private GeoLine  g;     // output       
        
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

	public void getFreeVariablesAndDegrees(HashSet<FreeVariable> freeVariables,
			int[] degrees) {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			int[] degree1=null, degree2=null;
			((SymbolicParametersAlgo) input[0]).getFreeVariablesAndDegrees(freeVariables, degree1);
			((SymbolicParametersAlgo) input[0]).getFreeVariablesAndDegrees(freeVariables, degree2);
			degrees=SymbolicParameters.addDegree(degree1, degree2);
		} else {
			degrees=null;
			freeVariables=null;
		}
		
	}

	public BigInteger[] getExactCoordinates() {
		if (input[0] != null && input[1] != null
				&& input[0] instanceof SymbolicParametersAlgo
				&& input[1] instanceof SymbolicParametersAlgo) {
			BigInteger[] coords1 = ((SymbolicParametersAlgo) input[0])
					.getExactCoordinates();
			BigInteger[] coords2 = ((SymbolicParametersAlgo) input[1])
					.getExactCoordinates();
			if (coords1 != null && coords2 != null) {
				return SymbolicParameters.crossProduct(coords1, coords2);
			}
		}
		return null;
	}
}
