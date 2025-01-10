/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 */
public class AlgoDiameterLine extends AlgoDiameterLineND {

	private GeoVector v;

	/**
	 * Creates new 2D algo for Diameter
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param c
	 *            conic
	 * @param g
	 *            parallel line
	 */
	public AlgoDiameterLine(Construction cons, String label, GeoConicND c,
			GeoLineND g) {
		super(cons, label, c, g);
	}

	@Override
	protected void createOutput(Construction cons1) {
		diameter = new GeoLine(cons1);
		v = new GeoVector(cons1);
	}

	// calc diameter line of v relative to c
	@Override
	public final void compute() {
		((GeoLine) g).getDirection(v);
		c.diameterLine(v, (GeoLine) diameter);
	}

}
