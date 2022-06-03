package org.geogebra.web.full.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.GGraphics2DWI;

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

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 */
	public StylePreview(int width, int height) {
		Canvas canvas = Canvas.createIfSupported();
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		initWidget(canvas);
		g2 = new GGraphics2DW(canvas);
		setMarginY(height / 2 - 1);
	}

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

	protected void drawStylePreview(GColor color, int thickness, int style, int width) {
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		g2.setStroke(EuclidianStatic.getStroke(thickness,
				style));
		g2.setColor(color);
		gp.reset();
		gp.moveTo(marginX, marginY);
		gp.lineTo(marginX + width, marginY);
		g2.draw(gp);
	}
}
