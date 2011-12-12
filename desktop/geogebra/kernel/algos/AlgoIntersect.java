/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.AlgoIntersectND;
import geogebra.common.kernel.kernelND.GeoPointND;


public abstract class AlgoIntersect extends AlgoIntersectND {

    public AlgoIntersect(Construction c) {
        super(c);
    }
    
	/**
	 * Avoids two intersection points at same position. 
	 * This is only done as long as the second intersection point doesn't have a label yet.
	 */
	@Override
	protected void avoidDoubleTangentPoint() {
		GeoPoint2 [] points = getIntersectionPoints();
	    if (!points[1].isLabelSet() && points[0].isEqual(points[1])) {
	    	points[1].setUndefined();	        
	    }
	} 

    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the coordinates (xRW, yRW)
     */
    public int getClosestPointIndex(double xRW, double yRW) {
        GeoPoint2[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
            x = (P[i].inhomX - xRW);
            y = (P[i].inhomY - yRW);
            lengthSqr = x * x + y * y;
            if (lengthSqr < mindist) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }
    
    /**
     * Returns the index in output[] of the intersection point
     * that is closest to the GeoPoint refPoint
     */
    int getClosestPointIndex(GeoPoint2 refPoint) {
        GeoPoint2[] P = getIntersectionPoints();
        double x, y, lengthSqr, mindist = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < P.length; i++) {
            x = (P[i].inhomX - refPoint.getInhomX());
            y = (P[i].inhomY - refPoint.getInhomY());
            lengthSqr = x * x + y * y;
            //if two distances are equal, smaller index gets priority
            if (AbstractKernel.isGreater(mindist, lengthSqr)) {
                mindist = lengthSqr;
                minIndex = i;
            }
        }

        return minIndex;
    }

    @Override
	protected abstract GeoPoint2[] getIntersectionPoints();
    
    @Override
	protected abstract GeoPoint2[] getLastDefinedIntersectionPoints();

    @Override
	protected void setCoords(GeoPointND destination, GeoPointND source){
    	((GeoPoint2) destination).setCoords((GeoPoint2) source);
    }

}
