/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAnglePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.geogebra3D.kernel3D.algos;

import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAngleLines;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoLineND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAngleLines3D extends AlgoAngleLines{
	
	private Coords vn, o, v1, v2;

	AlgoAngleLines3D(Construction cons, String label, GeoLineND g, GeoLineND h) {
		super(cons, label, g, h);
	}
	
	
    @Override
	final protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle3D(cons);
    }
	
    @Override
	public final void compute() {
    	
    	// lines origins and directions
    	Coords o1 = getg().getStartInhomCoords();
    	v1 = getg().getDirectionInD3();   	
    	Coords o2 = geth().getStartInhomCoords();
    	v2 = geth().getDirectionInD3();
    	
    	// normal vector
    	vn = v1.crossProduct4(v2).normalized();    	
    	if (!vn.isDefined()){ // parallel lines
    		getAngle().setValue(0);
    		return;
    	}
   	
    	// nearest points
    	Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(o1, v1, o2, v2);
    	
    	if (!points[0].equalsForKernel(points[1])){ // lines are not coplanar
    		getAngle().setUndefined();
        	return;
    	}
    	
    	o = points[0];
    	
    	v1.calcNorm();
    	double l1 = v1.getNorm();
    	v2.calcNorm();
    	double l2 = v2.getNorm();  	
    	
    	double c = v1.dotproduct(v2)/(l1*l2); //cosinus of the angle
    	
    	getAngle().setValue(Math.acos(c));
    	

    }
    
	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {
		
		if (drawable == null){ // TODO : this is a pgf / asymptote / pstricks call
			return false;
		}
		
		Coords ov = drawable.getCoordsInView(o);
		if (!drawable.inView(ov)) {
			return false;
		}
		
		m[0] = ov.get()[0];
		m[1] = ov.get()[1];
		
		
		
		Coords v1v = drawable.getCoordsInView(v1);
		if (!drawable.inView(v1v)) {
			return false;
		}
		
		Coords v2v = drawable.getCoordsInView(v2);
		if (!drawable.inView(v2v)) {
			return false;
		}

		firstVec[0] = v1v.get()[0];
		firstVec[1] = v1v.get()[1];
		
		
		return true;
	}
	

    @Override
	public Coords getVn(){
    	return vn;
    }
    
    
	
	
}
