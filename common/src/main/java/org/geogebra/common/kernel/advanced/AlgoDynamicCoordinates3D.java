/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Michael
 */
public class AlgoDynamicCoordinates3D extends AlgoDynamicCoordinates {

	private GeoNumberValue z; // input

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param arg
	 *            moveable point
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public AlgoDynamicCoordinates3D(Construction cons, String label,
			GeoPointND arg, GeoNumberValue x, GeoNumberValue y,
			GeoNumberValue z) {
		super(cons);
		this.P = arg;
		this.x = x;
		this.y = y;
		this.z = z;
		// create new Point
		M = kernel.getManager3D().point3D(0, 0, 0, false);
		setInputOutput();

		compute();
		M.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = P.toGeoElement();
		input[1] = x.toGeoElement();
		input[2] = y.toGeoElement();
		input[3] = z.toGeoElement();

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	@Override
	public GeoPointND getPoint() {
		return M;
	}

	@Override
	public final void compute() {
		double xCoord = x.getDouble();
		double yCoord = y.getDouble();
		double zCoord = z.getDouble();

		if (Double.isNaN(xCoord) || Double.isInfinite(xCoord)
				|| Double.isNaN(yCoord) || Double.isInfinite(yCoord)
				|| Double.isNaN(zCoord) || Double.isInfinite(zCoord)) {
			M.setUndefined();
			return;
		}

		M.setCoords(xCoord, yCoord, zCoord, 1.0);
	}

}
