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

package org.geogebra.web.html5.awt;

import org.geogebra.web.awt.GGraphics2DW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Position;

public class LayeredGGraphicsW extends GGraphics2DW {

	private int currentLayer = 0;
	private final Style parentStyle;

	/**
	 * @param canvas Primary canvas
	 */
	public LayeredGGraphicsW(Canvas canvas) {
		super(canvas);
		Style style = canvas.getCanvasElement().getStyle();
		style.setPosition(Position.RELATIVE);
		parentStyle = canvas.getParent().getElement().getStyle();
	}

	/**
	 * @return z-index for embedded item
	 */
	@Override
	public int embed() {
		parentStyle.setZIndex(currentLayer + 1);
		return currentLayer++;
	}

	@Override
	public void resetLayer() {
		currentLayer = 0;
	}
}
