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
 * Fits the saved visible canvas area into the current viewport using uniform
 * scaling and centering. Objects outside the saved area may also become visible
 * when the viewport aspect ratio differs from the saved area.
 *
 * @author laszlo
 *
 */
public class AdjustScreen {

	record CoordinateSystem(double xZero, double yZero, double xScale, double yScale) {}
	/**
	 * Adjusts the coordinate system to fit and center the saved visible area in the
	 * current viewport, preserving its aspect ratio without cropping.
	 *
	 * @param view
	 *            view whose coordinate system is adjusted
	 */
	public static void adjustCoordSystem(EuclidianView view) {
		EuclidianSettings settings = view.getSettings();

		if (settings.getFileWidth() <= 0 || settings.getFileHeight() <= 0) {
			return;
		}

		CoordinateSystem adjustedCoordSys = fitToViewport(
				new CoordinateSystem(
						settings.getFileXZero(),
						settings.getFileYZero(),
						settings.getFileXScale(),
						settings.getFileYScale()),
				settings.getFileWidth(),
				settings.getFileHeight(),
				view.getWidth(),
				view.getHeight());
		view.setCoordSystem(
				adjustedCoordSys.xZero,
				adjustedCoordSys.yZero,
				adjustedCoordSys.xScale,
				adjustedCoordSys.yScale);
	}

	static CoordinateSystem fitToViewport(
			CoordinateSystem source,
			double sourceWidth,
			double sourceHeight,
			double targetWidth,
			double targetHeight) {
		double ratio = Math.min(targetWidth / sourceWidth, targetHeight / sourceHeight);
		double dx = (targetWidth - ratio * sourceWidth) / 2;
		double dy = (targetHeight - ratio * sourceHeight) / 2;
		return new CoordinateSystem(
				dx + ratio * source.xZero,
				dy + ratio * source.yZero,
				ratio * source.xScale,
				ratio * source.yScale);
	}
}
