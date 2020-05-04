package org.geogebra.web.html5.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

public class LayeredGGraphicsW extends GGraphics2DW {

	private int currentLayer = 0;

	/**
	 * @param canvas Primary canvas
	 */
	public LayeredGGraphicsW(Canvas canvas) {
		super(canvas);
		Style style = canvas.getCanvasElement().getStyle();
		style.setPosition(Style.Position.RELATIVE);
	}

	/**
	 * @return z-index for embedded item
	 */
	@Override
	public int embed() {
		canvas.getCanvasElement().getStyle().setZIndex(currentLayer + 1);
		return currentLayer++;
	}

	@Override
	public void resetLayer() {
		currentLayer = 0;
	}
}
