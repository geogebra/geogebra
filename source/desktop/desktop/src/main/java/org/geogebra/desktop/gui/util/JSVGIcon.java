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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.Icon;

/**
 * Class to create and render icons from SVGs.
 */
public final class JSVGIcon implements Icon {

	private final SVGImage image;
	private Object oldAliasHint;
	private Object oldInterpolationHint;
	private Dimension preferredSize = null;
	private boolean antiAlias;
	private final JSCGInterpolation interpolation = JSCGInterpolation.NEAREST_NEIGHBOR;

	/**
	 * Method to fetch the SVG icon from an url
	 * @param url the url from which to fetch the SVG icon
	 */
	public JSVGIcon(URL url) {
		image = JSVGImageBuilder.fromUrl(url);
	}

	@Override
	public int getIconWidth() {
		return hasPreferredSize() ? preferredSize.width : (int) image.getWidth();
	}

	private boolean hasPreferredSize() {
		return preferredSize != null;
	}

	@Override
	public int getIconHeight() {
		return hasPreferredSize() ? preferredSize.height : (int) image.getHeight();
	}

	@Override
	public void paintIcon(Component comp, Graphics gg, int x, int y) {
		paintIcon((Graphics2D) gg, x, y);
		gg.dispose();
	}

	private void paintIcon(Graphics2D g, int x, int y) {
		saveRenderingHints(g);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		interpolation.apply(g);
		g.translate(x, y);

		final int width = getIconWidth();
		final int height = getIconHeight();

		if (width == 0 || height == 0) {
			return;
		}
		double diaWidth = image.getWidth();
		double diaHeight = image.getHeight();

		double scaleW = width / diaWidth;
		double scaleH = height / diaHeight;
		g.translate(-x, -y);
		image.paint(g, 0, 0, scaleW, scaleH);
		restoreRenderingHints(g);
	}

	private void saveRenderingHints(Graphics2D g) {
		oldAliasHint = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		oldInterpolationHint = g
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
	}

	private void restoreRenderingHints(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
		if (oldInterpolationHint != null) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					oldInterpolationHint);
		}
	}

	/**
	 *
	 * @param antiAlias to set.
	 */
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	/**
	 *
	 * @param dimension the preferred size of the icon.
	 */
	public void setPreferredSize(Dimension dimension) {
		this.preferredSize = dimension;
	}
}