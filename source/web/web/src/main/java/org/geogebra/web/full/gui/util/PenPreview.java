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

import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public class PenPreview extends StylePreview {
	private EuclidianPen pen;

	/**
	 * @param pen
	 *            euclidian pen
	 * @param width
	 *            pixel width
	 * @param height
	 *            pixel height
	 */
	public PenPreview(EuclidianPen pen, int width, int height) {
		super(width, height);
		this.pen = pen;
	}

	/**
	 * Update preview
	 */
	public void update() {
		clear();
		drawStylePreview(pen.getPenColorWithOpacity(), pen.getPenSize(),
				EuclidianStyleConstants.LINE_TYPE_FULL);
	}
}
