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
