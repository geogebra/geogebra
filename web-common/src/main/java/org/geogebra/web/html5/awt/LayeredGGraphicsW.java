package org.geogebra.web.html5.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;

public class LayeredGGraphicsW extends GGraphics2DW {

	private int currentLayer = 0;
	private final Style parentStyle;

	/**
	 * @param canvas Primary canvas
	 */
	public LayeredGGraphicsW(Canvas canvas) {
		super(canvas);
		Style style = canvas.getCanvasElement().getStyle();
		style.setPosition(Style.Position.RELATIVE);
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
