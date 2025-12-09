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

		setOnlyOutput(getAngle());
		setDependencies(); // done by AlgoElement

	}

}
