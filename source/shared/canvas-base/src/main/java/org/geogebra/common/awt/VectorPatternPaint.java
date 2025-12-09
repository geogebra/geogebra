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

package org.geogebra.common.awt;

/**
 * Pattern for SVG painting
 *
 */
public class VectorPatternPaint implements GPaint {

	public enum VectorType {
		PDF, SVG
	}

	public final VectorType type;
	private final GGraphics2D patternGraphics;

	private final int startX;
	private final int startY;
	private final int width;
	private final int height;

	/**
	 * @param path0 fill path
	 * @param width width
	 * @param height height
	 * @param startX horizontal position of sub-image
	 * @param startY vertical position of sub-image
	 */
	public VectorPatternPaint(GGraphics2D path0, int width, int height, int startX, int startY,
			VectorType type) {
		this.patternGraphics = path0;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
		this.type = type;
	}

	/**
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return horizontal offset of the view box
	 */
	public int getStartX() {
		return startX;
	}

	/**
	 * @return vertical offset of the view box
	 */
	public int getStartY() {
		return startY;
	}

	/**
	 * @return graphics object with drawing of the pattern
	 */
	public GGraphics2D getPatternGraphics() {
		return patternGraphics;
	}

}
