package org.geogebra.desktop.main;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ScaledIcon implements Icon {
	private final ImageIcon source;
	private final Component component;
	private double ratio = -1;

	/**
	 * @param source source icon
	 * @param ratio scaling ratio
	 */
	public ScaledIcon(ImageIcon source, double ratio) {
		this.source = source;
		this.ratio = ratio;
		component = null;
	}

	/**
	 * @param c parent component
	 */
	public ScaledIcon(Component c) {
		this.source = new ImageIcon();
		this.component = c;
	}

	/**
	 * Creates a new ImageIcon by joining them together (leftIcon to rightIcon).
	 *
	 * @param leftIcon left icon
	 * @param rightIcon right icon
	 * @return merged icon
	 */
	public static Icon joinIcons(ScaledIcon leftIcon,
			ScaledIcon rightIcon, Component comp) {

		if (leftIcon == null) {
			return rightIcon;
		}

		if (rightIcon == null) {
			return leftIcon;
		}
		ScaledIcon ret = new ScaledIcon(comp);
		int w1 = leftIcon.source.getIconWidth();
		int w2 = rightIcon.source.getIconWidth();
		int h1 = leftIcon.source.getIconHeight();
		int h2 = rightIcon.source.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h / 2;
		BufferedImage image = new BufferedImage(w1 + w2, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(leftIcon.getImage(), 0, mid - h1 / 2, null);
		g2.drawImage(rightIcon.getImage(), w1, mid - h2 / 2, null);
		g2.dispose();
		ret.setImage(image);
		return ret;
	}

	private double getRatio() {
		if (ratio < 0) {
			if (component != null) {
				try {
					ratio = component.getGraphicsConfiguration().getDefaultTransform().getScaleX();
				} catch (Exception ex) {
					// graphics not yet loaded, fall back to 1
					return 1;
				}
			} else {
				ratio = 1;
			}
		}
		return ratio;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		double ratio = getRatio();
		((Graphics2D) g).scale(1 / ratio, 1 / ratio);
		source.paintIcon(c, g, (int) Math.round(x * ratio), (int) Math.round(y * ratio));
		((Graphics2D) g).scale(ratio, ratio);
	}

	@Override
	public int getIconWidth() {
		return (int) (source.getIconWidth() / getRatio());
	}

	@Override
	public int getIconHeight() {
		return (int) (source.getIconHeight() / getRatio());
	}

	public Image getImage() {
		return source.getImage();
	}

	/**
	 * Set the image.
	 * @param image image to scale
	 */
	public void setImage(Image image) {
		source.setImage(image);
	}

	public void setRatio(double pixelRatio) {
		this.ratio = pixelRatio;
	}
}
