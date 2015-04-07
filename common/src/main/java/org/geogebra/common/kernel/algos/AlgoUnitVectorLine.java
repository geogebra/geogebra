/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoOrthoVectorLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoUnitVectorLine extends AlgoUnitVector2D {

	/** Creates new AlgoOrthoVectorLine */
	public AlgoUnitVectorLine(Construction cons, String label, GeoLineND g) {
		super(cons, label, (GeoElement) g);
	}

	@Override
	final protected void setXY() {
		x = ((GeoLine) inputGeo).y;
		y = -((GeoLine) inputGeo).x;
	}

	@Override
	final protected GeoPointND getInputStartPoint() {
		return ((GeoLine) inputGeo).getStartPoint();
	}

}
