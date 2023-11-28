
package org.geogebra.desktop.gui.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class SVGImage {

	private final SVGModel model;
	private GGraphics2D tmpG;
	public SVGImage(SVGModel model) {
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
		if (model.isInvalid()) {
			return;
		}
		AffineTransform oldTransform = g.getTransform();
		AffineTransform transform = new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
		model.setTransform(transform);
		BufferedImage image = getTempImage();
		Graphics2D graphics = getGraphics2D(image);
		model.paint(graphics);
		g.drawImage(image, transform, null);
		model.setTransform(oldTransform);
	}

	private BufferedImage getTempImage() {
		return new BufferedImage((int) getWidth(), (int) getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	}

	/**
	 * Paints the SVG to the graphics.
	 * @param g to paint to.
	 */
	public void paint(Graphics2D g) {
		BufferedImage image = getTempImage();
		Graphics2D graphics = getGraphics2D(image);
		model.paint(graphics);
		g.drawImage(image, null, 0,0);
	}

	private static Graphics2D getGraphics2D(BufferedImage image) {
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		graphics.setBackground(new Color(255, 0, 0, 0));
		graphics.clearRect(0, 0, image.getWidth(), image.getHeight());
		return graphics;
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

	public SVGImage tint(GColor color) {
		model.setFill(color);
		return new SVGImage(model);
	}

	public String getContent() {
		return model.getContent();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SVGImage)) return false;
		SVGImage SVGImage = (SVGImage) o;
		return Objects.equals(model, SVGImage.model);
	}

	@Override
	public int hashCode() {
		return Objects.hash(model);
	}
}
 