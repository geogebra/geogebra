/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoTangentLine extends AlgoTangentLineND {

    private GeoVector direction;

    /** Creates new AlgoTangentLine */
    AlgoTangentLine(Construction cons, String label, GeoLineND g, GeoConicND c) {
        super(cons, label, g,c);         
    }
    
    public AlgoTangentLine(Construction cons, String [] labels, GeoLineND g, GeoConicND c) {
       super(cons, labels, g,c);     
    }
    
    
    @Override
	protected void initDiameterAndDirection(){
    	// the tangents are computed by intersecting the
        // diameter line of g with c
        diameter = new GeoLine(cons);        
        direction = new GeoVector(cons);
        ((GeoLine) g).getDirection(direction);
        c.diameterLine(direction, diameter);
        algoIntersect = new AlgoIntersectLineConic(cons, diameter, (GeoConic) c);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        cons.removeFromConstructionList(algoIntersect); 
        tangentPoints = algoIntersect.getIntersectionPoints();
    }
    
    @Override
	protected void setTangents(){
    	 tangents = new GeoLine[2];
         tangents[0] = new GeoLine(cons);
         tangents[1] = new GeoLine(cons);
         ((GeoLine) tangents[0]).setStartPoint((GeoPoint) tangentPoints[0]);
         ((GeoLine) tangents[1]).setStartPoint((GeoPoint) tangentPoints[1]);
    }

    
    
    /**
     * Inits the helping interesection algorithm to take
     * the current position of the lines into account.
     * This is important so the the tangent lines are not
     * switched after loading a file
     */
    @Override
	public void initForNearToRelationship() {
    	// if first tangent point is not on first tangent,
    	// we switch the intersection points
    	
    	GeoPoint firstTangentPoint = (GeoPoint) tangentPoints[0];
    	
    	if (!((GeoLine) tangents[0]).isOnFullLine(firstTangentPoint, Kernel.MIN_PRECISION)) {
        	algoIntersect.initForNearToRelationship();
        	
        	// remember first point
    		double px = firstTangentPoint.x;
    		double py = firstTangentPoint.y;
    		double pz = firstTangentPoint.z;
    		
    		// first = second
    		algoIntersect.setIntersectionPoint(0, tangentPoints[1]);
    		
    		// second = first
    		tangentPoints[1].setCoords(px, py, pz);
    		algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
     	}		    	
    }
    
    
    @Override
	protected void updateDiameterLine(){
    	((GeoLine) g).getDirection(direction);
        c.diameterLine(direction, diameter);
    }
    
    @Override
	protected void updateTangent(int index){
    	GeoLine tangent = (GeoLine) tangents[index];
    	GeoLine line = (GeoLine) g;
    	GeoPoint point = (GeoPoint) tangentPoints[index];
    	 tangent.x = line.x;
         tangent.y = line.y;                
         tangent.z = -( point.inhomX * line.x +
                                     point.inhomY * line.y);
    }
}
