/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
