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

import geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAngleVectors;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAngleVectors3D extends AlgoAngleVectors{
	
	private Coords vn, o, v1, v2;

	AlgoAngleVectors3D(Construction cons, String label, GeoVectorND v, GeoVectorND w) {
		super(cons, label, v, w);
	}
	
	
    @Override
	final protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle3D(cons);
    }
	
    @Override
	public final void compute() {
    	
    	// vectors directions
    	v1 = getv().getCoordsInD(3);
    	v2 = getw().getCoordsInD(3);
    	
   	
    	// calc angle    	
    	v1.calcNorm();
    	double l1 = v1.getNorm();
    	v2.calcNorm();
    	double l2 = v2.getNorm();  	
    	
    	double c = v1.dotproduct(v2)/(l1*l2); //cosinus of the angle
    	
    	getAngle().setValue(AlgoAnglePoints3D.acos(c));
    	
    	
    	// normal vector
    	vn = AlgoAnglePoints3D.forceNormalVector(v1, v2);
   	
    	
    	// start point
    	GeoPointND start = getv().getStartPoint();
    	if (centerIsNotDrawable(start)){ 
    		o = Coords.UNDEFINED; 
    	}else{
    		o = start.getInhomCoordsInD(3);
    	}

    }
	

    @Override
	public Coords getVn(){
    	return vn;
    }
    
	@Override
	public boolean getCoordsInD3(Coords[] drawCoords){
		
		if (!o.isDefined()){
			return false;
		}
		
		drawCoords[0] = o;
		drawCoords[1] = v1;
		drawCoords[2] = v2;
		
		return true;
	}
	
	
}
