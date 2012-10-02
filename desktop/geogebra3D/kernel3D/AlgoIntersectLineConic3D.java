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

package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;




/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectLineConic3D extends AlgoIntersectConic3D {

    /**
     * 
     * @param cons
     * @param label
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, String label, GeoLineND g, GeoConicND c) {
        this(cons, g,c);
        GeoElement.setLabels(label, P);            
    }
    
    /**
     * 
     * @param cons
     * @param labels
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, String [] labels, GeoLineND g, GeoConicND c) {
        this(cons, g,c);
        GeoElement.setLabels(labels, P);            
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoIntersectLineConic;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }

    
    /**
     * 
     * @param cons
     * @param g
     * @param c
     */
    AlgoIntersectLineConic3D(Construction cons, GeoLineND g, GeoConicND c) {
        super(cons,(GeoElement) g,c);              
                      
    }   
    
    

    
    
    /**
     * 
     * @return line input
     */
    GeoLineND getLine() { return (GeoLineND) getFirtGeo(); }
    
    


	@Override
	protected Coords getFirstGeoStartInhomCoords() {
		return getLine().getStartInhomCoords();
	}

	@Override
	protected Coords getFirstGeoDirectionInD3() {
		return getLine().getDirectionInD3();
	}

	@Override
	protected boolean getFirstGeoRespectLimitedPath(Coords p) {
		return getLine().respectLimitedPath(p, Kernel.EPSILON);
	}

	@Override
	protected void checkIsOnFirstGeo(GeoPoint3D p) {
		if (!p.isDefined())
			return;
		if (!getLine().respectLimitedPath(p.getCoords(),Kernel.MIN_PRECISION))
			p.setUndefined();
	}
}
