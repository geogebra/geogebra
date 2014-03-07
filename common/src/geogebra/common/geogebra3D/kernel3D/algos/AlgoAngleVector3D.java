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

import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAngleVector3D extends AlgoAngleElement3D{

	
	private Coords o;

	public AlgoAngleVector3D(Construction cons, String label, GeoVector3D vec) {
		super(cons, label, vec);
	}
	
	protected final Coords getVectorCoords(){
		return ((GeoVector3D) vec).getCoordsInD(3).copyVector();
	}
	
	protected final Coords getOrigin(){
		return o;
	}
	
	protected final void setOrigin(){
		GeoPointND start = ((GeoVector3D) vec).getStartPoint();
		if (centerIsNotDrawable(start)){ 
    		o = Coords.UNDEFINED; 
    	}else{
    		o = start.getInhomCoordsInD(3);
    	}
	}
	

}
