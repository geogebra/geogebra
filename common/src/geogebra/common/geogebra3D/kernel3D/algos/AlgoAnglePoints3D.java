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
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAnglePoints3D extends AlgoAnglePoints{
	
	private Coords vn;

	AlgoAnglePoints3D(Construction cons, String label, GeoPointND A, GeoPointND B,
			GeoPointND C) {
		super(cons, label, A, B, C);
	}
	
	AlgoAnglePoints3D(Construction cons) {
		super(cons);
	}
	
	
    @Override
	final protected GeoAngle newGeoAngle(Construction cons){
    	return new GeoAngle3D(cons);
    }
    
    private Coords center, v1, v2;
	
    @Override
	public final void compute() {
    	center = getB().getInhomCoordsInD(3);
    	v1 = getA().getInhomCoordsInD(3).sub(center);
    	v2 = getC().getInhomCoordsInD(3).sub(center);
    	
    	v1.calcNorm();
    	double l1 = v1.getNorm();
    	v2.calcNorm();
    	double l2 = v2.getNorm();
    	
    	if (Kernel.isZero(l1) || Kernel.isZero(l2)){
    		getAngle().setUndefined();
        	return;
    	}
    	
    	double c = v1.dotproduct(v2)/(l1*l2); //cosinus of the angle
    	
    	getAngle().setValue(acos(c));
    	
    	//normal vector
    	vn = forceNormalVector(v1, v2);

    }
	

    @Override
	public Coords getVn(){
    	return vn;
    }
    
	@Override
	public boolean getCoordsInD3(Coords[] drawCoords){
		drawCoords[0] = center;
		drawCoords[1] = v1;
		drawCoords[2] = v2;
		
		return true;
	}
	
	
	/**
	 * acos, values can be a bit greater than 1 (or lower than 0)
	 * @param c cosinus of an angle
	 * @return angle between 0 and PI
	 */
	protected static final double acos(double c){
		if (Kernel.isEqual(c, 1)){ // case where c is a bit more than 1
    		return 0;
    	}

		if (Kernel.isEqual(c, -1)){  // case where c is a bit less than -1
    		return Math.PI;
    	}
    	
		return Math.acos(c);
    	
	}

	/**
	 * @param v1 first vector
	 * @param v2 second vector
	 * @return vector normal to v1, v2
	 */
	protected static final Coords forceNormalVector(Coords v1, Coords v2){
		Coords vn = v1.crossProduct4(v2);

		if (vn.isZero()){ // v1 and v2 are dependent
			vn = v1.crossProduct4(Coords.VX);
			if (vn.isZero()){
				vn = v1.crossProduct4(Coords.VY);
			}
		}

		vn.normalize();

		return vn;

	}
	
	private AlgoAnglePoints3D(GeoPointND A, GeoPointND B, GeoPointND C, Coords center, Coords v1, Coords v2, Coords vn) {
		super(A,B,C);
		this.center = center;
		this.v1 = v1;
		this.v2 = v2;
		this.vn = vn;
	}
    
	
	@Override
	public AlgoAnglePoints copy() {
		return new AlgoAnglePoints3D(
				getA().copy(), getB().copy(), getC().copy(), 
				center.copyVector(), v1.copyVector(), v2.copyVector(), vn.copyVector());
	}
	
	
}
