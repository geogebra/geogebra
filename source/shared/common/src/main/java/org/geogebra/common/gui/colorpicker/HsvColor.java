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

import java.util.Objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.util.StringUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Immutable color in the HSV color space, used as the color picker's source of truth.
 * <p>
 * Unlike {@link GColor}, it keeps hue and saturation for gray and black colors and is not
 * quantized to 8 bits, so picker handles stay exactly where the user put them.
 */
public final class HsvColor {

	private final double hue;
	private final double saturation;
	private final double value;

	/**
	 * @param hue hue in degrees; normalized to [0, 360), NaN is mapped to 0
	 * @param saturation saturation; clamped to [0, 1], NaN is mapped to 0
	 * @param value value (brightness); clamped to [0, 1], NaN is mapped to 0
	 */
	public HsvColor(double hue, double saturation, double value) {
		this.hue = normalizeHue(hue);
		this.saturation = clamp(saturation);
		this.value = clamp(value);
	}

	private static double normalizeHue(double hue) {
		if (Double.isNaN(hue)) {
			return 0;
		}
		double normalizedHue = hue % 360;
		double positiveHue = normalizedHue < 0 ? normalizedHue + 360 : normalizedHue;
		// tiny negative input can round up to exactly 360 in the addition above;
		// -0.0 (e.g. from -360 % 360) is mapped to 0.0 so that equals() treats them alike
		return positiveHue >= 360 || positiveHue == 0 ? 0 : positiveHue;
	}

	/**
	 * Converts an RGB color to HSV. Components that are undefined for the given color are taken
	 * from {@code previous}: the hue for grays and black, and the saturation for black.
	 * @param color the RGB color (alpha is ignored)
	 * @param previous the color to take undefined components from, or {@code null} to use 0
	 * @return the HSV color
	 */
	public static @NonNull HsvColor fromGColor(@NonNull GColor color, @Nullable HsvColor previous) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int max = Math.max(red, Math.max(green, blue));
		int min = Math.min(red, Math.min(green, blue));
		int delta = max - min;
		double previousHue = previous == null ? 0 : previous.hue;
		double previousSaturation = previous == null ? 0 : previous.saturation;
		if (max == 0) {
			return new HsvColor(previousHue, previousSaturation, 0);
		}
		if (delta == 0) {
			return new HsvColor(previousHue, 0, max / 255.0);
		}
		double hue;
		if (max == red) {
			hue = 60.0 * (green - blue) / delta;
		} else if (max == green) {
			hue = 60.0 * (blue - red) / delta + 120;
		} else {
			hue = 60.0 * (red - green) / delta + 240;
		}
		return new HsvColor(hue, (double) delta / max, max / 255.0);
	}

	/** @return hue in degrees, in [0, 360) */
	public double getHue() {
		return hue;
	}

	/** @return saturation in [0, 1] */
	public double getSaturation() {
		return saturation;
	}

	/** @return value (brightness) in [0, 1] */
	public double getValue() {
		return value;
	}

	/**
	 * @param newHue the new hue in degrees
	 * @return a copy with the given hue
	 */
	public @NonNull HsvColor withHue(double newHue) {
		return new HsvColor(newHue, saturation, value);
	}

	/**
	 * @param newSaturation the new saturation
	 * @param newValue the new value
	 * @return a copy with the given saturation and value
	 */
	public @NonNull HsvColor withSaturationAndValue(double newSaturation, double newValue) {
		return new HsvColor(hue, newSaturation, newValue);
	}

	/** @return the opaque RGB color */
	public @NonNull GColor toGColor() {
		return GColor.newColorHSB(hue / 360.0, saturation, value);
	}

	/** @return the color as uppercase {@code #RRGGBB} */
	public @NonNull String toHex() {
		return StringUtil.toHtmlColor(toGColor());
	}

	private static double clamp(double component) {
		if (Double.isNaN(component)) {
			return 0;
		}
		// adding 0.0 turns -0.0 into 0.0
		return Math.max(0, Math.min(1, component)) + 0.0;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof HsvColor that)) {
			return false;
		}
		return Double.compare(hue, that.hue) == 0
				&& Double.compare(saturation, that.saturation) == 0
				&& Double.compare(value, that.value) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hue, saturation, value);
	}

	@Override
	public @NonNull String toString() {
		return "HsvColor[hue=" + hue + ", saturation=" + saturation + ", value=" + value + "]";
	}
}
