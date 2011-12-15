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

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Joins two GeoPoint3Ds in a GeoSegment3D, GeoLine3D, ... regarding to geoClassType
 * 
 */
public class AlgoJoinPoints3D extends AlgoElement3D {
	
	//inputs
	/** first point */
	private GeoPointND P;
	/** second point */
	private GeoPointND Q;
	/** polygon or polyhedron (when segment is part of) */
	private GeoElement poly;
	
	//output
	/** 1D coord sys */
    protected GeoCoordSys1D cs; 
    /** the output is a segment, a line, ... */
    protected GeoClass geoClassType;

    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param label name of the segment/line/...
     * @param P first point
     * @param Q second point
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    public AlgoJoinPoints3D(Construction cons, String label, GeoPointND P, GeoPointND Q, GeoClass geoClassType) {
    	this(cons,label,P,Q,null,geoClassType);
    }
    
    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param label name of the segment/line/...
     * @param P first point
     * @param Q second point
     * @param poly poly polygon or polyhedron (when segment is part of) 
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    AlgoJoinPoints3D(Construction cons, String label, 
    		GeoPointND P, GeoPointND Q, GeoElement poly, GeoClass geoClassType) {
    	this(cons,P,Q,poly,geoClassType);
    	cs.setLabel(label);
    }
    

    /** Creates new AlgoJoinPoints3D 
     * @param cons the construction
     * @param P first point
     * @param Q second point
     * @param poly polygon or polyhedron (when segment is part of) 
     * @param geoClassType type (GeoSegment3D, GeoLine3D, ...) */    
    AlgoJoinPoints3D(Construction cons, 
    		GeoPointND P, GeoPointND Q, GeoElement poly, GeoClass geoClassType) {
    	super(cons);

    	this.P = P;
    	this.Q = Q;
    	this.poly = poly;
    	this.geoClassType = geoClassType;
    	
    	switch(geoClassType){
    	case SEGMENT3D:
    		cs = new GeoSegment3D(cons, P, Q);
    		if (poly!=null)
    			((GeoSegment3D) cs).setGeoParent(poly);
    		break;
    	case LINE3D:
    		cs = new GeoLine3D(cons, P, Q); 
    		break;
    	case RAY3D:
    		cs = new GeoRay3D(cons, P, Q);
    		break;
    	default:
    		cs = null;
    	}
    	
    	if (poly==null) {
    		setInputOutput(new GeoElement[] {(GeoElement) P,(GeoElement) Q}, new GeoElement[] {cs});
    	} else {
    		setInputOutput(new GeoElement[] {(GeoElement) P,(GeoElement) Q, poly},
    				new GeoElement[] {(GeoElement) P,(GeoElement) Q},
    				new GeoElement[] {cs});
    	}
    }        
    
    /**
     * return the first point
     * @return the first point
     */
    GeoPointND getP() {
        return P;
    }
    
    /**
     * return the second point
     * @return the second point
     */   
    GeoPointND getQ() {
        return Q;
    }
    
    
    /** return the 1D coord sys
     * @return the 1D coord sys
     */
    public GeoCoordSys1D getCS(){
    	return cs;
    }
    

    public void compute() {
    	    
    	if (poly!=null) {
    		if (!poly.isDefined())
    			cs.setUndefined();
    	}

    	if ((((GeoElement) P).isDefined()||P.isInfinite())&&(((GeoElement) Q).isDefined()||Q.isInfinite())) {
    		cs.setCoord(P,Q);
    	} else {
    		cs.setUndefined();
    	}
    }
    
    public void remove() {
        super.remove();
        //if segment is part of a polygon, remove it
        if (poly != null) {
            poly.remove();
        }
    }  
    
    /**
     * modify input points
     * @param A first point
     * @param B second point
     */
    public void modifyInputPoints(GeoPointND A, GeoPointND B){
    	for (int i=0;i<2;i++)
    		input[i].removeAlgorithm(this);
    	
    	P=A;
    	Q=B;   	
    	compute();
    	setInputOutput();   	
    	
    }
    
	public String getClassName() {
		String s = 	"AlgoJoinPoints3D";
    	switch(geoClassType){
    	case SEGMENT3D:
    		s+="Segment";
    		break;
    	case LINE3D:
    		s+="Line";
    		break;
    	case RAY3D:
    		s+="Ray";
    		break;
    	}		
    	
    	return s;
	}
	
    final public String toString() {
        StringBuilder sb = new StringBuilder();

        switch(geoClassType){
    	case SEGMENT3D:
    		if (poly!=null)
                sb.append(app.getPlain("SegmentABofC",P.getLabel(),Q.getLabel(),poly.getNameDescription()));
    		else
    			sb.append(app.getPlain("SegmentAB",((GeoElement) P).getLabel(),((GeoElement) Q).getLabel()));
    		break;
    	case LINE3D:
    		sb.append(app.getPlain("LineThroughAB",((GeoElement) P).getLabel(),((GeoElement) Q).getLabel()));
    		break;
    	case RAY3D:
    		sb.append(app.getPlain("RayThroughAB",((GeoElement) P).getLabel(),((GeoElement) Q).getLabel()));
    		break;
    	}	

        return sb.toString();
    }   

}
