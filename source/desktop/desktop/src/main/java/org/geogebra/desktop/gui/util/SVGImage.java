/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Objects;

import org.geogebra.common.awt.GColor;

/**
 * Class to load and paint SVGs.
 * Note that links within SVG are replaced to blank images for security reasons.
 */
public final class SVGImage {

	private final SVGModel model;

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
		model.paint(g);
		model.setTransform(oldTransform);
	}

	/**
	 * Paints the SVG to the graphics.
	 * @param g to paint to.
	 */
	public void paint(Graphics2D g) {
		model.paint(g);
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

	/**
	 * @param color fill color
	 * @return tinted image
	 */
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
 