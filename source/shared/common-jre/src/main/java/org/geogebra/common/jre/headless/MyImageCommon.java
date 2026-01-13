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

package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;

public class MyImageCommon implements MyImage {
	private final int height;
	private final int width;

	/**
	 * @param width pixel width
	 * @param height pixel height
	 */
	public MyImageCommon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isSVG() {
		return false;
	}

	@Override
	public GGraphics2D createGraphics() {
		return null;
	}

	@Override
	public String toLaTeXStringBase64() {
		return "";
	}

	@Override
	public String getSVG() {
		return "<svg/>";
	}

	@Override
	public boolean hasNonNullImplementation() {
		return false;
	}
}
