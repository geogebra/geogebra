package org.geogebra.web.full.gui.util;

import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;
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
	protected GGraphics2DWI g2;
	private int marginX = 0;
	private int marginY;
	protected AppW app;

	/**
	 * @param app
	 *            application
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public StylePreview(AppW app, int width, int height) {
		this.app = app;
		Canvas canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		initWidget(canvas);
		g2 = new GGraphics2DW(canvas);
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
		g2.clearAll();
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
