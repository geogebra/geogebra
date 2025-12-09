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

package org.geogebra.web.full.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class LineStylePreview extends StylePreview {

	/**
	 * @param width
	 *            width of line
	 * @param height
	 *            height of line
	 */
	public LineStylePreview(int width, int height) {
		super(width, height);
	}

	/**
	 * Update preview
	 * 
	 * @param thickness
	 *            of the line.
	 * @param typeIdx
	 *            index of type.
	 * @param color
	 *            of the line.
	 */
	public void update(int thickness, int typeIdx, GColor color) {
		clear();
		drawStylePreview(color, thickness, EuclidianView.getLineType(typeIdx));
	}
}
