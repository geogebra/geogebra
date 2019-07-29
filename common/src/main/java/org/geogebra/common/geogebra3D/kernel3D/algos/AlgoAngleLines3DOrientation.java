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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoAngle3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author mathieu
 */
public class AlgoAngleLines3DOrientation extends AlgoAngleLines3D {

	private GeoDirectionND orientation;

	/**
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param h
	 *            line
	 * @param orientation
	 *            orientation
	 */
	AlgoAngleLines3DOrientation(Construction cons, GeoLineND g,
			GeoLineND h, GeoDirectionND orientation) {
		super(cons, g, h, orientation);
	}

	@Override
	protected void setInput(GeoLineND g, GeoLineND h,
			GeoDirectionND orientation) {
		super.setInput(g, h, orientation);
		this.orientation = orientation;
	}

	@Override
	protected GeoAngle newGeoAngle(Construction cons1) {
		return GeoAngle3D.newAngle3DWithDefaultInterval(cons1);
	}

	@Override
	public void compute() {

		super.compute();

		if (orientation == kernel.getSpace()) { // no orientation with space
			return;
		}

		if (!getAngle().isDefined() || DoubleUtil.isZero(getAngle().getValue())) {
			return;
		}

		checkOrientation(vn, orientation, getAngle(), false);
	}

	@Override
	public String toString(StringTemplate tpl) {

		// return loc.getPlain("AngleBetweenABOrientedByC",
		// getg().getLabel(tpl),
		// geth().getLabel(tpl), orientation.getLabel(tpl));

		// clearer just as "angle between u and v"
		return getLoc().getPlain("AngleBetweenAB", getg().getLabel(tpl),
				geth().getLabel(tpl));
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) getg();
		input[1] = (GeoElement) geth();
		input[2] = (GeoElement) orientation;

		setOutputLength(1);
		setOutput(0, getAngle());
		setDependencies(); // done by AlgoElement

	}

}
