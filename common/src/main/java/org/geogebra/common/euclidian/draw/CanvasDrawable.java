package org.geogebra.common.euclidian.draw;

import org.geogebra.common.euclidian.Drawable;

public abstract class CanvasDrawable extends Drawable {
	private boolean drawingOnCanvas;

	public boolean isDrawingOnCanvas() {
		return drawingOnCanvas;
	}

	public void setDrawingOnCanvas(boolean drawOnCanvas) {
		this.drawingOnCanvas = drawOnCanvas;
	}
}
