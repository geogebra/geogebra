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
