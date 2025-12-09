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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Converts mouse movement to parameter change
 */
public interface CoordConverter {

	/**
	 * @param direction
	 *            movement direction
	 * @param rwTransVec
	 *            translation vector
	 * @param startValue
	 *            initial parameter value
	 * @param view
	 *            3D view
	 * @return new parameter value
	 */
	double translationToValue(Coords direction, Coords rwTransVec,
			double startValue, EuclidianView view);

	/**
	 * @param parent
	 *            changeable parent
	 * @param startPoint
	 *            start point
	 */
	void record(ChangeableParent parent, Coords startPoint);

	/**
	 * @param startPoint3D
	 *            start point
	 * @param direction
	 *            movement direction
	 * @param rayOrigin
	 *            hitting origin
	 * @param rayDirection
	 *            hitting direction
	 * @param translationVec3D
	 *            output parameter for translation
	 */
	void updateTranslation(Coords startPoint3D, Coords direction,
			Coords rayOrigin, Coords rayDirection,
			Coords translationVec3D);

	/**
	 * @param val
	 *            raw value
	 * @param view
	 *            view
	 * @return value snapped to closest "nice" value
	 */
	double snap(double val, EuclidianView view);

}
