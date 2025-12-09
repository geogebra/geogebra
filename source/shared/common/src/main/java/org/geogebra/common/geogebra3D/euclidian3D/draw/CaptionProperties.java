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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.matrix.Coords;

public class CaptionProperties {
	private @CheckForNull CaptionText caption;
	private final Coords color = new Coords(0, 0, 0, 1);
	private final EuclidianView3D view;
	private Coords backgroundColor;

	public CaptionProperties(EuclidianView3D view) {
		this.view = view;
	}

	/**
	 * Set the caption.
	 * @param caption new caption
	 */
	public void update(CaptionText caption) {
		this.caption = caption;
	}

	/**
	 * @return whether caption has a subscript
	 */
	public boolean hasSubscript() {
		return caption != null && caption.text().contains("_");
	}

	/**
	 * update caption colors
	 */
	public void update() {
		updateColor();
		updateBackgroundColor();
	}

	private void updateColor() {
		if (caption == null) {
			return;
		}
		GColor convertColor = caption.foregroundColor();
		if (view.isAdditiveDisplay()
				&& convertColor.isDarkerThan(Drawable3D.DARKEST_ADDITIVE_COLOR)) {
			convertColor = Drawable3D.DARKEST_ADDITIVE_COLOR
					.deriveWithAlpha(convertColor.getAlpha());
		}

		color.set((double) convertColor.getRed() / 255,
				(double) convertColor.getGreen() / 255,
				(double) convertColor.getBlue() / 255,
				1);

		if (view.isGrayScaled()) {
			color.convertToGrayScale();
		}
	}

	Coords foregroundColorNormalized() {
		updateColor();
		return color;
	}

	Coords backgroundColorNormalized() {
		updateBackgroundColor();
		return backgroundColor;
	}

	private void updateBackgroundColor() {
		if (caption == null) {
			return;
		}
		GColor bgColor = caption.backgroundColor();
		this.backgroundColor = bgColor == null
				? null
				: new Coords(
				(double) bgColor.getRed() / 255,
				(double) bgColor.getGreen() / 255,
				(double) bgColor.getBlue() / 255, 1);
	}

	/**
	 * @return whether caption has a background color
	 */
	public boolean hasBackgroundColor() {
		return caption != null && caption.backgroundColor() != null;
	}
}
