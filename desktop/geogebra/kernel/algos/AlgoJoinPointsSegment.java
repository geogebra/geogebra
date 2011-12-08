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

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.AbstractConstruction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoJoinPointsSegmentInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.kernel.geos.GeoPolygon;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPointsSegment extends AlgoElement implements AlgoJoinPointsSegmentInterface {

	private GeoPoint2 P, Q; // input
    private GeoSegment s; // output: GeoSegment subclasses GeoLine 

    private GeoPolygon poly; // for polygons         

    /** Creates new AlgoJoinPoints */
    public AlgoJoinPointsSegment(
        AbstractConstruction cons,
        String label,
        GeoPoint2 P,
        GeoPoint2 Q) {
        this(cons, P, Q, null);
        s.setLabel(label);
    }

    public AlgoJoinPointsSegment(
        AbstractConstruction cons,        
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
	public String getClassName() {
        return "AlgoJoinPointsSegment";
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
    	for (int i=0;i<2;i++)
    		input[i].removeAlgorithm(this);
    	
    	P=A;
    	Q=B;   	
    	s.setPoints(P, Q);
    	setInputOutput();   	
    	
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
    
    protected GeoPolygon getPoly() {
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
        } else {
			return super.getConstructionIndex();
        }
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        if (poly != null) {
        	return app.getPlain("SegmentABofC",P.getLabel(),Q.getLabel(),poly.getNameDescription());
        } else {
        	return app.getPlain("SegmentAB",P.getLabel(),Q.getLabel());
        }
    }
}
