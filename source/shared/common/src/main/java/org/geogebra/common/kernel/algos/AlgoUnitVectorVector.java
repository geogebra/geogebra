/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 */
public class AlgoUnitVectorVector extends AlgoUnitVector2D {

	/** Creates new AlgoOrthoVectorVector */
	public AlgoUnitVectorVector(Construction cons, VectorNDValue v,
			boolean normalize) {
		super(cons, (GeoElement) v, normalize);
	}

	@Override
	final protected void setXY() {
		x = ((GeoVec3D) inputGeo).x;
		y = ((GeoVec3D) inputGeo).y;
	}

	@Override
	final protected GeoPointND getInputStartPoint() {
		if (inputGeo instanceof GeoVector) {
			return ((GeoVector) inputGeo).getStartPoint();
		}
		return null;
	}

}
