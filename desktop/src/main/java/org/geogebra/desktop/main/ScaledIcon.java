package org.geogebra.desktop.main;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ScaledIcon implements Icon {
	private final ImageIcon source;
	private final double ratio;

	/**
	 * @param source source icon
	 * @param ratio scaling ratio
	 */
	public ScaledIcon(ImageIcon source, double ratio) {
		this.source = source;
		this.ratio = ratio;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		((Graphics2D) g).scale(1 / ratio, 1 / ratio);
		source.paintIcon(c, g, (int) Math.round(x * ratio), (int) Math.round(y * ratio));
		((Graphics2D) g).scale(ratio, ratio);
	}

	@Override
	public int getIconWidth() {
		return (int) (source.getIconWidth() / ratio);
	}

	@Override
	public int getIconHeight() {
		return (int) (source.getIconHeight() / ratio);
	}

	public Image getImage() {
		return source.getImage();
	}
}
