package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Composite;

/**
 * Widget to show line with thickness and style.
 * 
 * @author Laszlo Gal
 *
 */
public abstract class StylePreview extends Composite {
	/** The value canvas next to the slider */
	private Canvas canvas;

	protected GGraphics2DW g2;
	private int marginX = 0;
	private int marginY;
	protected AppW app;

	public StylePreview(AppW app, int width, int height) {
		setCanvas(Canvas.createIfSupported());
		getCanvas().setCoordinateSpaceWidth(width);
		getCanvas().setCoordinateSpaceHeight(height);
		initWidget(getCanvas());
		this.app = app;
		g2 = new GGraphics2DW(getCanvas());
		createPreviewGeo();
		setMarginY(height / 2 - 1);
	}

	/**
	 * Creates a geo used for preview only.
	 */
	protected abstract void createPreviewGeo();


	/**
	 * Clears the preview.
	 */
	public void clear() {
		g2.clearRect(0, 0, getOffsetWidth(), getOffsetHeight());
	}
	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
	}


}
