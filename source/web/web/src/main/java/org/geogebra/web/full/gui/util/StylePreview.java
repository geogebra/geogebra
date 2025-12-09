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
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.web.awt.GGraphics2DW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.Composite;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public abstract class StylePreview extends Composite {
	/** The value canvas */
	private final GGraphics2DW g2;
	private final int marginY;
	private final int width;

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public StylePreview(int width, int height) {
		Canvas canvas = Canvas.createIfSupported();
		initWidget(canvas);
		g2 = new GGraphics2DW(canvas);
		g2.setCoordinateSpaceSize(width, height);
		marginY = height / 2 - 1;
		this.width = width;
	}

	/**
	 * Clears the preview.
	 */
	public void clear() {
		g2.clearAll();
	}

	protected void drawStylePreview(GColor color, int thickness, int style) {
		g2.setStroke(EuclidianStatic.getStroke(thickness,
				style));
		g2.setColor(color);
		g2.drawStraightLine(0, marginY, width, marginY);
	}
}
