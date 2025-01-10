package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * Checks if the original screen was bigger when file was saved or not. If so,
 * some widgets needs to be adjusted to fit the smaller screen.
 * 
 * @author laszlo
 *
 */
public class AdjustScreen {

	/**
	 * Adjust the coordinate system to the screen size
	 * 
	 * @param view
	 *            {@link EuclidianView}
	 */
	public static void adjustCoordSystem(EuclidianView view) {
		EuclidianSettings s = view.getSettings();
		double rX = (double) view.getWidth() / s.getFileWidth();
		double rY = (double) view.getHeight() / s.getFileHeight();

		double ox = s.getFileXZero() * rX;
		double oy = s.getFileYZero() * rY;
		double scale = s.getFileYScale() * rY;
		view.setCoordSystem(ox, oy, scale, scale);
	}
}
