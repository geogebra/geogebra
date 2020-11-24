package org.geogebra.common.euclidian.plot;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;

/**
 * Class to query basic information for a segment about to draw
 * like it is on screen, the distance is not "big" enough
 * or has an angle that is not suitable to draw.
 *
 * @author laszlo
 *
 */
public class CurveSegmentInfo {
	public static final int MAX_PIXEL_DISTANCE = 10; // pixels
	private static final double MAX_ANGLE = 10; // degrees
	private static final double MAX_ANGLE_OFF_SCREEN = 45; // degrees
	public static final double MAX_BEND = Math.tan(MAX_ANGLE * Kernel.PI_180);
	private static final double MAX_BEND_OFF_SCREEN = Math
			.tan(MAX_ANGLE_OFF_SCREEN * Kernel.PI_180);

	private final EuclidianView view;
	private boolean distanceOK;
	private boolean angleOK;
	private boolean offScreen;

	/**
	 * Constructor
	 *
	 * @param view {@link EuclidianView}
 	 */
	public CurveSegmentInfo(EuclidianView view) {
		this.view = view;
	}

	/**
	 *
	 * @return if segment is off the screen
	 */
	public boolean isOffScreen() {
		return offScreen;
	}

	/**
	 * updates info
	 *
	 * @param evalLeft left value.
	 * @param evalRight right value.
	 * @param diff left-right difference in pixels.
	 * @param prevDiff the
	 */
	public void update(double[] evalLeft, double[] evalRight, double[] diff, double[] prevDiff) {
		offScreen = view.isSegmentOffView(evalLeft, evalRight);
		distanceOK = offScreen || isDistanceOK(diff);
		angleOK = isAngleOK(prevDiff, diff, offScreen
				? MAX_BEND_OFF_SCREEN : MAX_BEND);

	}

	/**
	 *
	 * @return true if distance or angle is not valid for drawing.
	 */
	public boolean isDistanceOrAngleInvalid() {
		return !angleOK || !distanceOK;
	}

	/**
	 * Returns whether the pixel distance from the last point is smaller than
	 * MAX_PIXEL_DISTANCE in all directions.
	 */
	private static boolean isDistanceOK(double[] diff) {
		for (double d : diff) {
			if (Math.abs(d) > MAX_PIXEL_DISTANCE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether the angle between the vectors (vx, vy) and (wx, wy) is
	 * smaller than MAX_BEND, where MAX_BEND = tan(MAX_ANGLE).
	 */
	private static boolean isAngleOK(double[] v, double[] w, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = 0;
		for (int i = 0; i < v.length; i++) {
			innerProduct += v[i] * w[i];
		}
		if (CurveSegmentPlotter.isUndefined(innerProduct)) {
			return true;
		} else if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		} else {
			// angle < 90 degrees
			// small angle: |det(v, w)| < MAX_BEND * (v . w)
			double det;
			if (v.length < 3) {
				det = Math.abs(v[0] * w[1] - v[1] * w[0]);
			} else {
				double d1 = v[0] * w[1] - v[1] * w[0];
				double d2 = v[1] * w[2] - v[2] * w[1];
				double d3 = v[2] * w[0] - v[0] * w[2];
				det = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
			}
			return det < bend * innerProduct;
		}
	}
}