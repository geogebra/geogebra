package org.geogebra.web.full.gui.layout;

import elemental2.dom.CanvasRenderingContext2D;

/**
 * Component that can be painted on canvas.
 */
public interface PaintToCanvas {
	/**
	 * Paint content of this component to a context.
	 * @param context2d context
	 * @param counter view counter, should be increased when painting done
	 * @param x horizontal offset
	 * @param y vertical offset
	 */
	void paintToCanvas(CanvasRenderingContext2D context2d, ViewCounter counter, int x, int y);
}
