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

package org.geogebra.common.euclidian;

public class LatexRendererSettings implements TextRendererSettings {
	public static final int BOTTOM_OFFSET = 9;
	private final int fixMargin;
	private final int rightMargin;
	private int bottomOffset;
	private double fontMultiplier = 1.0;
	private int baseFontSize;

	/**
	 * @param fixMargin fix vertical margin.
	 * @param rightMargin right margin.
	 * @param bottomOffset offset of bottom
	 */
	public LatexRendererSettings(int fixMargin, int rightMargin, int bottomOffset) {
		this.fixMargin = fixMargin;
		this.rightMargin = rightMargin;
		this.bottomOffset = bottomOffset;
	}

	/**
	 * Creates an instance with a given parameters
	 * @return a new instance
	 */
	public static LatexRendererSettings create() {
		return new LatexRendererSettings(2, 8, 10);
	}

	/**
	 * Like create(baseFontSize), but no bottomOffset, as input boxes require it.
	 * @param baseFontSize the base baseFontSize.
	 * @param fontMultiplier font size multiplier
	 * @return a new instance
	 */
	public static LatexRendererSettings createForInputBox(int baseFontSize, double fontMultiplier) {
		LatexRendererSettings settings = create();
		settings.bottomOffset = 0;
		settings.baseFontSize = baseFontSize;
		settings.fontMultiplier = fontMultiplier;
		return settings;
	}

	@Override
	public int getFixMargin() {
		return fixMargin;
	}

	@Override
	public int getMinHeight() {
		return multiply(30) + BOTTOM_OFFSET;
	}

	private int multiply(double value) {
		return (int) Math.round(value * fontMultiplier);
	}

	@Override
	public int getRightMargin() {
		return rightMargin;
	}

	@Override
	public int getBottomOffset() {
		return bottomOffset;
	}

	@Override
	public int getRendererFontSize() {
		return multiply(baseFontSize);
	}

	@Override
	public int getEditorFontSize() {
		return getRendererFontSize() + 3;
	}
}
