
package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.geogebra.common.awt.GColor;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImage {

	private final JSVGModel model;

	public JSVGImage(JSVGModel model) {
		this.model = model;

	}

	/**
	 * Method to paint the image using Graphics2D.
	 * @param g the graphics context used for drawing
	 * @param x the X coordinate of the top left corner of the image
	 * @param y the Y coordinate of the top left corner of the image
	 * @param scaleX the X scaling to be applied to the image before drawing
	 * @param scaleY the Y scaling to be applied to the image before drawing
	 */
	public void paint(Graphics2D g, int x, int y, double scaleX, double scaleY) {
		if (isInvalid()) {
			return;
		}
		AffineTransform oldTransform = g.getTransform();
		AffineTransform transform = new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
		model.node.setTransform(transform);
		model.node.paint(g);
		model.node.setTransform(oldTransform);
	}

	private boolean isInvalid() {
		return model.node == null;
	}

	/**
	 * Paints the SVG to the graphics.
	 * @param g to paint to.
	 */
	public void paint(Graphics2D g) {
		if (isInvalid()) {
			return;
		}

		model.node.paint(g);
	}

	/**
	 * @return width of the whole SVG.
	 */
	public float getWidth() {
		return model.getWidth();
	}

	/**
	 * @return height of the whole SVG.
	 */
	public float getHeight() {
		return model.getHeight();
	}

	public JSVGImage tint(GColor color) {
		model.setFill(color);
		return new JSVGImage(model);
	}

	public String getContent() {
		return model.content;
	}
}
