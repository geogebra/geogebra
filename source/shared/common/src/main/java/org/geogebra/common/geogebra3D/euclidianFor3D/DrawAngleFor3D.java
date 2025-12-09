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

package org.geogebra.common.geogebra3D.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.algos.AlgoAngle;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * @author mathieu
 *
 */
public class DrawAngleFor3D extends DrawAngle {

	/**
	 * @param view
	 *            view where the drawable is created
	 * @param angle
	 *            angle
	 */
	public DrawAngleFor3D(EuclidianView view, GeoAngle angle) {
		super(view, angle);
	}

	@Override
	public boolean inView(Coords point) {
		// Coords p = view.getCoordsForView(point);
		return DoubleUtil.isZero(point.getZ());
	}

	@Override
	public Coords getCoordsInView(Coords point) {
		return view.getCoordsForView(point);
	}

	@Override
	protected double getAngleStart(double start, double extent) {

		if (view.getCompanion().goToZPlus(
				((AlgoAngle) getGeoElement().getDrawAlgorithm()).getVn())) {
			return super.getAngleStart(start, extent);
		}

		// reverse orientation
		return start - extent;

	}

}
