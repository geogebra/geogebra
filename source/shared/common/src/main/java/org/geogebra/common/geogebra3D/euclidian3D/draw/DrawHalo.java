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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.RenderingHints;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Class for drawing labels of 3D elements
 *
 * @author mathieu
 *
 */
public class DrawHalo extends DrawableTexture3D {

	private final Coords color = new Coords(4);

	/**
	 * common constructor
	 *
	 * @param view
	 *            3D view
	 */
	public DrawHalo(EuclidianView3D view) {
		this.view = view;
	}

	/**
	 * Update the halo.
	 *
	 * @param v
	 *            coordinates
	 * @param xOffset0
	 *            abs offset in x
	 * @param yOffset0
	 *            abs offset in y
	 * @param zOffset0
	 *            abs offset in z
	 * @param fgColor color
	 * @param radius halo radius
	 */
	public void update(Coords v,
			float xOffset0, float yOffset0, float zOffset0, GColor fgColor, int radius) {
		if (!view.drawsLabels()) {
			return;
		}
		this.origin = v;
		CaptionProperties.updateColor(fgColor, view, color);
		setIsVisible(true);

		width = 2 * radius;
		height = 2 * radius;
		xOffset2 = -radius;
		yOffset2 = -radius;

		// creates the buffered image
		GBufferedImage bimg = createImage(2 * radius);

		// creates the texture
		view.getRenderer().createAlphaTexture(this, bimg);

		waitForReset = false;

		this.xOffset = xOffset0;
		this.yOffset = yOffset0;
		this.zOffset = zOffset0;
	}

	/**
	 * create graphics2D instance from buffered image
	 *
	 * @param bimg
	 *            buffered image
	 * @return graphics2D
	 */
	protected GGraphics2D createGraphics2D(GBufferedImage bimg) {
		GGraphics2D g2d = bimg.createGraphics();
		g2d.setColor(GColor.BLACK);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return g2d;
	}

	/**
	 * @return buffered image with label drawn in it
	 */
	protected GBufferedImage createImage(double size) {
		GBufferedImage img = view.getRenderer().createBufferedImage(this);
		GGraphics2D g2d = createGraphics2D(img);
		double thickness = 3;
		g2d.setStroke(AwtFactory.getPrototype().newBasicStroke(thickness));
		g2d.draw(AwtFactory.getPrototype().newEllipse2DDouble(thickness / 2, thickness / 2,
				size - thickness, size - thickness));

		return img;
	}

	@Override
	protected void drawContent(Renderer renderer) {
		// draw text
		renderer.setColor(color);
		renderer.getRendererImpl().enableTextures();
		renderer.getRendererImpl().setLayer(Renderer.LAYER_FOR_TEXTS);
		renderer.getTextures().setTextureLinear(textureIndex);
		renderer.getGeometryManager().drawLabel(textIndex);
		renderer.getRendererImpl().setLayer(Renderer.LAYER_DEFAULT);
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	protected Coords getBackgroundColor() {
		return null;
	}
}
