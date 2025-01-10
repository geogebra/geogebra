package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGradientPaint;

import com.himamis.retex.renderer.web.graphics.JLMContext2d;

import elemental2.dom.CanvasGradient;

/**
 * Gradient paint for canvas.
 */
public class GGradientPaintW implements GGradientPaint {
	private GColor color1;
	private GColor color2;
	private double x1;
	private double x2;
	private double y1;
	private double y2;

	/**
	 * Use the gradient in a context.
	 * 
	 * @param c
	 *            context
	 */
	public void apply(JLMContext2d c) {
		CanvasGradient gradient = c.createLinearGradient(x1, y1, x2, y2);
		gradient.addColorStop(0, GColor.getColorString(color1));
		gradient.addColorStop(1, GColor.getColorString(color2));
		c.setFillStyle(gradient);
	}

	/**
	 * @param x1
	 *            initial x
	 * @param y1
	 *            initial y
	 * @param color1
	 *            initial color
	 * @param x2
	 *            terminal x
	 * @param y2
	 *            terminal y
	 * @param color2
	 *            terminal color
	 */
	public GGradientPaintW(double x1, double y1, GColor color1, double x2,
			double y2, GColor color2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.color1 = color1;
		this.color2 = color2;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param gpaint
	 *            source
	 */
	public GGradientPaintW(GGradientPaintW gpaint) {
		this.x1 = gpaint.x1;
		this.x2 = gpaint.x2;
		this.y1 = gpaint.y1;
		this.y2 = gpaint.y2;
		this.color1 = gpaint.color1;
		this.color2 = gpaint.color2;
	}
}
