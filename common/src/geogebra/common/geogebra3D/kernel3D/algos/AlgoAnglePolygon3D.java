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
import geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.algos.AlgoAnglePolygon;
import geogebra.common.kernel.geos.GeoAngle;


/**
 *
 * @author  mathieu
 */
public class AlgoAnglePolygon3D extends AlgoAnglePolygon{


	/**
	 * @param cons construction
	 * @param labels labels
	 * @param poly polygon
	 */
	public AlgoAnglePolygon3D(Construction cons, String[] labels, GeoPolygon3D poly) {
		super(cons, labels, poly);
	}
	
    @Override
	protected AlgoAnglePoints newAlgoAnglePoints(Construction cons1){
    	return new AlgoAnglePoints3DOrientation(cons1, getPolygon());
    }
	
	
    @Override
	final protected GeoAngle newGeoAngle(Construction cons1){
    	return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
    }

	

}
