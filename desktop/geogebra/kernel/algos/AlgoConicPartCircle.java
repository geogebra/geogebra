/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoConicPart;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.kernel.geos.GeoConicPart;



/**
 * Circular arc or sector defined by the circle's center, one point
 * on the circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle extends AlgoConicPart {
	
	private GeoPoint2 center, startPoint, endPoint;	

	private GeoPoint2 P, Q;			
	
    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    public AlgoConicPartCircle(Construction cons, String label,
    		 GeoPoint2 center, GeoPoint2 startPoint, GeoPoint2 endPoint,
    		int type) {
    	this(cons, center, startPoint, endPoint, type);
    	conicPart.setLabel(label);
    }
    
    public  AlgoConicPartCircle(Construction cons, 
  	   		 GeoPoint2 center, GeoPoint2 startPoint, GeoPoint2 endPoint, int type) {      
        super(cons, type);
        this.center = center;
        this.startPoint = startPoint;
        this.endPoint = endPoint;   
                
        // create circle with center through startPoint        
        AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, center, startPoint);
        cons.removeFromConstructionList(algo);
        conic = algo.getCircle();

        // temp Points
        P = new GeoPoint2(cons);
        Q = new GeoPoint2(cons);
        
        conicPart = new GeoConicPart(cons, type);
        conicPart.addPointOnConic(startPoint);
        
        setInputOutput(); // for AlgoElement      
        compute();               
        setIncidence();
    }    	
    
    private void setIncidence() {
    	startPoint.addIncidence((GeoConicPart)conicPart);
    	//endPoint.addIncidence(conicPart);
		
	}

	public GeoPoint2 getStartPoint() {
    	return startPoint;
    }
    
    public GeoPoint2 getEndPoint() {
    	return endPoint;
    }
    
    public GeoPoint2 getCenter() {
    	return center;
    }
    
	@Override
	public String getClassName() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return "AlgoCircleArc";
			default:
				return "AlgoCircleSector";
		}		
	}
	
	@Override
	public int getRelatedModeID() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
			default:
				return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
		}
	}

    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = center;      
        input[1] = startPoint;
        input[2] = endPoint;

        super.setOutputLength(1);
        super.setOutput(0, (GeoConicPart)conicPart);

        setDependencies();
    }
    
    @Override
	public final void compute() {
    	// the temp points P and Q should lie on the conic
    	P.setCoords(startPoint);
    	((GeoConic)conic).pointChanged(P);
    	
    	Q.setCoords(endPoint);
    	((GeoConic)conic).pointChanged(Q);
    	
    	// now take the parameters from the temp points
    	conicPart.set((GeoConic)conic);    	    	    	    	
    	conicPart.setParameters(P.getPathParameter().t, Q.getPathParameter().t, true);
    }
    
}
