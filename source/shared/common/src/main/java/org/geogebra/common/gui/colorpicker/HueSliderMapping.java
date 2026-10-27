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

package org.geogebra.common.gui.colorpicker;

/**
 * Maps hue values to an inset slider track, and pointer positions back to hue values.
 * Use the same mapping for the thumb and the rainbow gradient: the gradient runs from
 * {@code toPosition(0)} to {@code toPosition(360)}, with red filling both end caps.
 */
public final class HueSliderMapping {

	private final double inset;
	private final double usableWidth;

	/**
	 * Creates a mapping for the current slider geometry.
	 * @param trackWidth full width of the track
	 * @param inset distance from each track edge to the thumb's outermost center position,
	 * usually the thumb's outer radius; limited to half the track width
	 */
	public HueSliderMapping(double trackWidth, double inset) {
		this.inset = Math.min(inset, trackWidth / 2);
		usableWidth = trackWidth - 2 * this.inset;
	}

	/**
	 * Maps a hue to a thumb or gradient position. Accepts 360 degrees to position the gradient's
	 * right endpoint, even though the color model normalizes that hue to zero.
	 * @param hue hue in degrees, clamped to [0, 360]; NaN is treated as zero
	 * @return position relative to the left edge of the full track
	 */
	public double toPosition(double hue) {
		return inset + clamp(hue / 360) * usableWidth;
	}

	/**
	 * Maps a pointer position to a hue.
	 * @param position pointer position relative to the left edge of the full track;
	 * NaN is treated as the left endpoint
	 * @return hue in [0, 360); the upper bound stays just below 360 so that applying it
	 * to {@link HsvColor} does not wrap the thumb to the left
	 */
	public double toHue(double position) {
		if (usableWidth == 0) {
			return 0;
		}
		return Math.min(Math.nextDown(360.0), clamp((position - inset) / usableWidth) * 360);
	}

	private static double clamp(double fraction) {
		return Double.isNaN(fraction) ? 0 : Math.max(0, Math.min(1, fraction));
	}
}
