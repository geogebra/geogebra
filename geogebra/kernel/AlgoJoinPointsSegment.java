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

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsSegment extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q; // input
    private GeoSegment s; // output: GeoSegment subclasses GeoLine 

    private GeoPolygon poly; // for polygons         

    /** Creates new AlgoJoinPoints */
    AlgoJoinPointsSegment(
        Construction cons,
        String label,
        GeoPoint P,
        GeoPoint Q) {
        this(cons, P, Q, null);
        s.setLabel(label);
    }

    public AlgoJoinPointsSegment(
        Construction cons,        
        GeoPoint P,
        GeoPoint Q,
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
        setInputOutput(); // for AlgoElement
               
        // compute line through P, Q
        compute();             
        setIncidence();
    }   
    
    private void setIncidence() {
    	P.addIncidence(s);
    	Q.addIncidence(s);
	}
    

    public String getClassName() {
        return "AlgoJoinPointsSegment";
    }

	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SEGMENT_FIXED;
	}
    
    // for AlgoElement
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
    	
        output = new GeoElement[1];
        output[0] = s;
          
        //setDependencies();
        setEfficientDependencies(input, efficientInput);
    }

    GeoSegment getSegment() {
        return s;
    }
    GeoPoint getP() {
        return P;
    }
    GeoPoint getQ() {
        return Q;
    }
    
    protected GeoPolygon getPoly() {
    	return poly;
    }

    // calc the line g through P and Q    
    protected final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
    	GeoVec3D.lineThroughPoints(P, Q, s);      	    
    	s.calcLength();
    }

    public void remove() {
        super.remove();
        if (poly != null)
            poly.remove();
    }       
    
    /**
     * Only removes this segment and does not remove parent polygon (if poly != null)
     */
    void removeSegmentOnly() {
    	super.remove();    	
    }

    public int getConstructionIndex() {
        if (poly != null)
			return poly.getConstructionIndex();
		else
			return super.getConstructionIndex();
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (poly != null)
        	return app.getPlain("SegmentABofC",P.getLabel(),Q.getLabel(),poly.getNameDescription());
        else
        	return app.getPlain("SegmentAB",P.getLabel(),Q.getLabel());

    }
}
