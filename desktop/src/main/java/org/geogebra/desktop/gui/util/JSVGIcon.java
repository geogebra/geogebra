package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeSupport;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.geogebra.desktop.util.GuiResourcesD;

public final class JSVGIcon implements Icon {


	public static final String PROP_AUTOSIZE = "PROP_AUTOSIZE";
	private final JSVGImage image;
	private Object oldAliasHint;
	private Object oldInterpolationHint;

	enum AutoSize {
		NONE,
		HORIZONTAL,
		VERTICAL,
		BEST_FIT,
		STRETCH
	}

	enum Interpolation {
		NEAREST_NEIGHBOR,
		BILINEAR,
		BICUBIC;

		void apply(Graphics2D g) {
			switch (this) {
			case NEAREST_NEIGHBOR:
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				break;
			case BILINEAR:
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				break;
			case BICUBIC:
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				break;
			}
		}
	}

	private Dimension preferredSize = null;
	private boolean clipToViewbox;
	private final AffineTransform scaleXform = new AffineTransform();

	private boolean antiAlias;
	private AutoSize autoSize = AutoSize.NONE;
	private Interpolation interpolation = Interpolation.NEAREST_NEIGHBOR;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(
			this);


	public JSVGIcon(URL url) throws Exception {
		this(url.toString());
	}

		/**
		 * Method to fetch the SVG icon from an url
		 * @param url the url from which to fetch the SVG icon
		 */
	public JSVGIcon(String url) {
		image = JSVGImage.fromUrl(url);
	}

	@Override
	public int getIconWidth() {
		if (preferredSize != null
				&& (autoSize == AutoSize.HORIZONTAL || autoSize == AutoSize.STRETCH
				|| autoSize == AutoSize.BEST_FIT)) {
			return preferredSize.width;
		}

		return (int) image.getWidth();
	}

	@Override
	public int getIconHeight() {
		if (preferredSize != null
				&& (autoSize == AutoSize.VERTICAL || autoSize == AutoSize.STRETCH
				|| autoSize == AutoSize.BEST_FIT)) {
			return preferredSize.height;
		}

		return (int) image.getHeight();
	}

	@Override
	public void paintIcon(Component comp, Graphics gg, int x, int y) {
//		Graphics2D g = (Graphics2D) gg.create();
		paintIcon((Graphics2D) gg, x, y);
		gg.dispose();
	}

	private void paintIcon(Graphics2D g, int x, int y) {
		image.paint(g, x, y, 1,1);
	}
	private void paintIcon_(Graphics2D g, int x, int y) {
		saveRenderingHints(g);

		interpolation.apply(g);

		g.translate(x, y);

		if (clipToViewbox) {
			g.setClip(new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
		}

		if (autoSize == AutoSize.NONE) {
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
		image.paint(g, 0,0, scaleW, scaleH);
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


	public void setAntiAlias(boolean antiAlias) {
		boolean old = this.antiAlias;
		this.antiAlias = antiAlias;
		changes.firePropertyChange("antiAlias", old, antiAlias);
	}

	public void setAutoSize(AutoSize autoSize) {
		AutoSize old = this.autoSize;
		this.autoSize = autoSize;
		changes.firePropertyChange(PROP_AUTOSIZE, old, autoSize);
	}

	public void setPreferredSize(Dimension dimension) {
		Dimension old = this.preferredSize;
		this.preferredSize = dimension;
		changes.firePropertyChange("preferredSize", old, preferredSize);

	}

	public  static void main(String args[]) throws Exception {
		GuiResourcesD res = GuiResourcesD.FILLING_SETTINGS;
		ImageIcon image = GeoGebraIconD.createFileImageIcon(res);
		JLabel label = new JLabel(image);
		javax.swing.JOptionPane.showMessageDialog(null,
				label);
	}
}