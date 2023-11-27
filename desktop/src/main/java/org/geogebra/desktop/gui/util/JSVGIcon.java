package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeSupport;
import java.net.URL;

import javax.swing.Icon;

/**
 * Class to create and render icons from SVGs.
 */
public final class JSVGIcon implements Icon {

	public static final String PROP_AUTOSIZE = "PROP_AUTOSIZE";
	private final SVGImage image;
	private Object oldAliasHint;
	private Object oldInterpolationHint;
	private Dimension preferredSize = null;
	private boolean antiAlias;
	private JSVGAutoSize autoSize = JSVGAutoSize.NONE;
	private final JSCGInterpolation interpolation = JSCGInterpolation.NEAREST_NEIGHBOR;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(
			this);


	/**
	 * Method to fetch the SVG icon from an url
	 * @param url the url from which to fetch the SVG icon
	 */
	public JSVGIcon(URL url) {
		image = JSVGImageBuilder.fromUrl(url);
	}

	@Override
	public int getIconWidth() {
		if (preferredSize != null
				&& (autoSize == JSVGAutoSize.HORIZONTAL || autoSize == JSVGAutoSize.STRETCH
				|| autoSize == JSVGAutoSize.BEST_FIT)) {
			return preferredSize.width;
		}

		return (int) image.getWidth();
	}

	@Override
	public int getIconHeight() {
		if (preferredSize != null
				&& (autoSize == JSVGAutoSize.VERTICAL || autoSize == JSVGAutoSize.STRETCH
				|| autoSize == JSVGAutoSize.BEST_FIT)) {
			return preferredSize.height;
		}

		return (int) image.getHeight();
	}

	@Override
	public void paintIcon(Component comp, Graphics gg, int x, int y) {
		paintIcon((Graphics2D) gg, x, y);
		gg.dispose();
	}

	private void paintIcon(Graphics2D g, int x, int y) {
		saveRenderingHints(g);
		interpolation.apply(g);
		g.translate(x, y);

		if (autoSize == JSVGAutoSize.NONE) {
			try {
				g.translate(-x, -y);
				image.paint(g);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						oldAliasHint);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return;
		}

		final int width = getIconWidth();
		final int height = getIconHeight();

		if (width == 0 || height == 0) {
			return;
		}
		double diaWidth = image.getWidth();
		double diaHeight = image.getHeight();

		double scaleW = 1;
		double scaleH = 1;
		switch (autoSize) {

		case NONE:
			break;
		case HORIZONTAL:
			scaleW = scaleH = width / diaWidth;
			break;
		case VERTICAL:
			scaleW = scaleH = height / diaHeight;
			break;
		case BEST_FIT:
			scaleW = Math.min(height / diaHeight, width / diaWidth);
			scaleH = scaleW;

			break;
		case STRETCH:
			scaleW = width / diaWidth;
			scaleH = height / diaHeight;
			break;
		}
		g.translate(-x, -y);
		image.paint(g, 0, 0, scaleW, scaleH);
		restoreRenderingHints(g);
	}

	private void restoreRenderingHints(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
		if (oldInterpolationHint != null) {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					oldInterpolationHint);
		}
	}

	private void saveRenderingHints(Graphics2D g) {
		oldAliasHint = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		oldInterpolationHint = g
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
	}

	/**
	 *
	 * @param antiAlias to set.
	 */
	public void setAntiAlias(boolean antiAlias) {
		boolean old = this.antiAlias;
		this.antiAlias = antiAlias;
		changes.firePropertyChange("antiAlias", old, antiAlias);
	}

	/**
	 * Sets the auto-sizing mode of the icon.
	 *
	 * @param autoSize {@link JSVGAutoSize} to set.
	 */
	public void setAutoSize(JSVGAutoSize autoSize) {
		JSVGAutoSize old = this.autoSize;
		this.autoSize = autoSize;
		changes.firePropertyChange(PROP_AUTOSIZE, old, autoSize);
	}

	/**
	 *
	 * @param dimension the preferred size of the icon.
	 */
	public void setPreferredSize(Dimension dimension) {
		Dimension old = this.preferredSize;
		this.preferredSize = dimension;
		changes.firePropertyChange("preferredSize", old, preferredSize);

	}
}