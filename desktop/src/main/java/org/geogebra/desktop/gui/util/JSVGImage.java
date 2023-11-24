
package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.geogebra.common.awt.GColor;
import org.w3c.dom.svg.SVGDocument;

import io.sf.carte.echosvg.gvt.GraphicsNode;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class JSVGImage {

	private final SVGDocument doc;
	private final GraphicsNode node;
	private final int width;
	private final int height;

	public JSVGImage(SVGDocument doc, GraphicsNode node, float w, float h) {
		this.doc = doc;
		this.node = node;
		this.width = (int) w;
		this.height = (int) h;
	}

	/**
		 * Method to paint the image using Graphics2D.
		 *
		 * @param g the graphics context used for drawing
		 * @param x the X coordinate of the top left corner of the image
		 * @param y the Y coordinate of the top left corner of the image
		 * @param scaleX the X scaling to be applied to the image before drawing
		 * @param scaleY the Y scaling to be applied to the image before drawing
		 */
		public void paint (Graphics2D g,int x, int y, double scaleX, double scaleY){
			if (isInvalid()) {
				return;
			}
			AffineTransform oldTransform = g.getTransform();
			AffineTransform transform = new AffineTransform(scaleX, 0.0, 0.0, scaleY, x, y);
			node.setTransform(transform);
			node.paint(g);
			node.setTransform(oldTransform);
		}

		private boolean isInvalid () {
			return node == null;
		}

		/**
		 * Paints the SVG to the graphics.
		 *
		 * @param g to paint to.
		 */
		public void paint (Graphics2D g){
			if (isInvalid()) {
				return;
			}

			node.paint(g);
		}

		/**
		 *
		 * @return width of the whole SVG.
		 */
		public float getWidth () {
			return width;
		}

		/**
		 *
		 * @return height of the whole SVG.
		 */
		public float getHeight () {
			return height;
		}

		public JSVGImage tint(GColor color) {
			return JSVGImageBuilder.tint(doc, color);
		}
	}
