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
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAngleVectorND;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAngleElement3D extends AlgoAngleVectorND{
	
	private Coords vn, o, v2;

	public AlgoAngleElement3D(Construction cons, String label, GeoElement vec) {
		super(cons, label, vec);
	}
	
	
    @Override
	final protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle3D(cons);
    }
	
    @Override
	public final void compute() {
    	
    	// vectors directions
    	if (vec.isGeoVector()){
    		v2 = ((GeoVector3D) vec).getCoordsInD(3).copyVector();
    	}else{
    		v2 = ((GeoPoint3D) vec).getCoordsInD(3).copyVector();
    		v2.setW(0);
    	}
    	
   	
    	// calc angle  
    	v2.calcNorm();
    	double l2 = v2.getNorm();  	
    	
    	double c = v2.getX()/l2; //cosinus of the angle
    	
    	getAngle().setValue(AlgoAnglePoints3D.acos(c));
    	
    	
    	// normal vector
    	vn = AlgoAnglePoints3D.forceNormalVector(Coords.VX, v2);
   	
    	
    	// start point
    	if (vec.isGeoVector()){
    		GeoPointND start = ((GeoVector3D) vec).getStartPoint();
    		if (centerIsNotDrawable(start)){ 
        		o = Coords.UNDEFINED; 
        	}else{
        		o = start.getInhomCoordsInD(3);
        	}
    	}else{
    		o = Coords.O;
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
		drawCoords[1] = Coords.VX;
		drawCoords[2] = v2;
		
		return true;
	}
	

	@Override
	public boolean updateDrawInfo(double[] m, double[] firstVec, DrawAngle drawable) {

		if (vec.isGeoVector()){
			if (!o.isDefined()){
				return false;
			}
			if (!drawable.inView(o)) {
				return false;
			}
		}
		
		
		if (!drawable.inView(v2)) {
			return false;
		}

		// origin
		m[0] = o.get()[0];
		m[1] = o.get()[1];		

		// first vec
		firstVec[0] = 1;
		firstVec[1] = 0;
		
		return true;

	}
	
	
}
