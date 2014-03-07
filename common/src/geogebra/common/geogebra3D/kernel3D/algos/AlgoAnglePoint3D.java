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

import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;


/**
 *
 * @author  mathieu
 * @version 
 */
public class AlgoAnglePoint3D extends AlgoAngleElement3D{

	

	public AlgoAnglePoint3D(Construction cons, String label, GeoPoint3D vec) {
		super(cons, label, vec);
	}
	
	protected final Coords getVectorCoords(){
		
		Coords v = ((GeoPoint3D) vec).getCoordsInD(3).copyVector();
		v.setW(0);
		return v;
	}
	
	protected final Coords getOrigin(){
		return Coords.O;
	}
	
	protected final void setOrigin(){
		// nothing to do here
	}
	
	

}
