/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra.common.kernel.algos;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.AbstractApplication;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsSegment extends AlgoElement implements AlgoJoinPointsSegmentInterface,
	SymbolicParametersBotanaAlgo, SymbolicParametersAlgo {

	private GeoPoint2 P, Q; // input
    private GeoSegment s; // output: GeoSegment subclasses GeoLine 

    private GeoPolygon poly; // for polygons
    
    private Variable[] botanaVars;
	private Polynomial[] polynomials;

    /** Creates new AlgoJoinPoints */
    public AlgoJoinPointsSegment(
        Construction cons,
        String label,
        GeoPoint2 P,
        GeoPoint2 Q) {
        this(cons, P, Q, null);
        s.setLabel(label);
    }

    public AlgoJoinPointsSegment(
        Construction cons,        
        GeoPoint2 P,
        GeoPoint2 Q,
        GeoPolygon poly) {
    	super(cons);
    	    	 
        // make sure that this helper algorithm is updated right after its parent polygon
    	if (poly != null) {
    		setUpdateAfterAlgo(poly.getParentAlgorithm());    		    		
    	}
    		
        this.poly = poly;                
        this.P = P;
        this.Q = Q;
          
        s = new GeoSegment(cons, P, Q);
        s.setFromMeta(poly);
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();             
        setIncidence();
    }   
    
    private void setIncidence() {
    	P.addIncidence(s);
    	Q.addIncidence(s);
	}
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoJoinPointsSegment;
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SEGMENT_FIXED;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
    	GeoElement [] efficientInput = new GeoElement[2];
    	efficientInput[0] = P;
    	efficientInput[1] = Q;
    	
    	if (poly == null) {
    		input = efficientInput;    		
    	} else {
    		input = new GeoElement[3];
    		input[0] = P;
            input[1] = Q;
            input[2] = poly;
//    		input = new GeoElement[2];
//    		input[0] = P;
//            input[1] = Q;               
    	}            	
    	
        super.setOutputLength(1);
        super.setOutput(0, s);
          
        //setDependencies();
        setEfficientDependencies(input, efficientInput);
    }
    
    public void modifyInputPoints(GeoPoint2 A, GeoPoint2 B){
    	for (int i=0;i<input.length;i++)
    		input[i].removeAlgorithm(this);
    	
    	P=A;
    	Q=B;   	
    	s.setPoints(P, Q);
    	setInputOutput(); 
    	
    	compute();
    	
    }

    public GeoSegment getSegment() {
        return s;
    }
    GeoPoint2 getP() {
        return P;
    }
    GeoPoint2 getQ() {
        return Q;
    }
    
    public GeoPolygon getPoly() {//protected
    	return poly;
    }

    // calc the line g through P and Q    
    @Override
	public final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
    	GeoVec3D.lineThroughPoints(P, Q, s);      	    
    	s.calcLength();
    }
    
    @Override
	public void remove() {
    	if(removed)
			return;
        super.remove();
        if (poly != null)
            poly.remove();
    }       
    
    /**
     * Only removes this segment and does not remove parent polygon (if poly != null)
     */
    public void removeSegmentOnly() {
    	super.remove();    	
    }

    @Override
	public int getConstructionIndex() {
        if (poly != null) {
			return poly.getConstructionIndex();
        }
		return super.getConstructionIndex();
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (poly != null) {
        	return app.getPlain("SegmentABofC",P.getLabel(tpl),Q.getLabel(tpl),poly.getNameDescription());
        }
		return app.getPlain("SegmentAB",P.getLabel(tpl),Q.getLabel(tpl));
    }

	public Variable[] getBotanaVars() {
		if (botanaVars != null)
			return botanaVars;
		
		botanaVars = new Variable[4];
		Variable[] line1vars = new Variable[2];
		Variable[] line2vars = new Variable[2];
		line1vars = ((SymbolicParametersBotanaAlgo) input[0]).getBotanaVars();
		line2vars = ((SymbolicParametersBotanaAlgo) input[1]).getBotanaVars();
		botanaVars[0] = line1vars[0];
		botanaVars[1] = line1vars[1];
		botanaVars[2] = line2vars[0];
		botanaVars[3] = line2vars[1];
		
		return botanaVars;
	}

	public Polynomial[] getBotanaPolynomials()
			throws NoSymbolicParametersException {
		// It's OK, polynomials for lines/segments are only created when a third point is lying on them, too:
		return null;
	}
	
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	public int[] getFreeVariablesAndDegrees(HashSet<Variable> variables) throws NoSymbolicParametersException {
		if (P != null && Q != null) {
			int[] degree1=P.getFreeVariablesAndDegrees(variables);
			int[] degree2=Q.getFreeVariablesAndDegrees(variables);
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
					
				return polynomials;
			}
		}
		throw new NoSymbolicParametersException();
	}
	
}
