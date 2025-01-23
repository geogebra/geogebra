// Copyright 2001-2007, FreeHEP.
package org.freehep.util.images;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import org.geogebra.common.util.debug.Log;

/**
 * @author Mark Donszelmann
 * @version $Id: ImageUtilities.java,v 1.5 2008-10-23 19:04:05 hohenwarter Exp $
 */
public class ImageUtilities {

	private ImageUtilities() {
	}

	public static RenderedImage createRenderedImage(Image image,
			ImageObserver observer, Color bkg) {
		if ((bkg == null) && (image instanceof RenderedImage)) {
			return (RenderedImage) image;
		}

		BufferedImage bufferedImage = new BufferedImage(
				image.getWidth(observer), image.getHeight(observer),
				(bkg == null) ? BufferedImage.TYPE_INT_ARGB
						: BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		if (bkg == null) {
			g.drawImage(image, 0, 0, observer);
		} else {
			g.drawImage(image, 0, 0, bkg, observer);
		}
		return bufferedImage;
	}

	public static BufferedImage createBufferedImage(RenderedImage image,
			ImageObserver observer, Color bkg) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		throw new IllegalArgumentException("not supperted " + image.getClass());
	}

	public static BufferedImage createBufferedImage(Image image,
			ImageObserver observer, Color bkg) {
		if ((bkg == null) && (image instanceof BufferedImage)) {
			return (BufferedImage) image;
		}
		return (BufferedImage) createRenderedImage(image, observer, bkg);
	}

	public static RenderedImage createRenderedImage(RenderedImage image,
			Color bkg) {
		if (bkg == null) {
			return image;
		}

		BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		g.setBackground(bkg);
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		g.drawRenderedImage(image, new AffineTransform());
		return bufferedImage;
	}

	public static byte[] getBytes(Image image, Color bkg, String code, int pad,
			ImageObserver observer) {
		return getBytes(createRenderedImage(image, observer, bkg), bkg, code,
				pad);
	}

	/**
	 * Returns the bytes of an image.
	 *
	 * @param image
	 *            to be converted to bytes
	 * @param bkg
	 *            the color to be used for alpha-multiplication
	 * @param code
	 *            ARGB, A, or BGR, ... you may also use *ARGB to pre-multiply
	 *            with alpha
	 * @param pad
	 *            number of bytes to pad the scanline with (1=byte, 2=short,
	 *            4=int, ...)
	 */
	public static byte[] getBytes(RenderedImage image, Color bkg, String code,
			int pad) {
		if (pad < 1) {
			pad = 1;
		}

		Raster raster = image.getData();

		int width = image.getWidth();
		int height = image.getHeight();

		boolean preMultiply = (code.charAt(0) == '*');
		if (preMultiply) {
			code = code.substring(1);
		}

		int pixelSize = code.length();

		int size = width * height * pixelSize;
		size += (width % pad) * height;
		int index = 0;
		byte[] bytes = new byte[size];

		ColorModel colorModel = image.getColorModel();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int argb = colorModel
						.getRGB(raster.getDataElements(x, y, (Object) null));
				int a = ((argb >> 24) & 0xFF);
				int r = ((argb >> 16) & 0xFF);
				int g = ((argb >> 8) & 0xFF);
				int b = ((argb >> 0) & 0xFF);

				// Check the transparancy. If transparent substitute
				// the background color.
				if (preMultiply && (a < 0xFF)) {
					if (bkg == null) {
						bkg = Color.BLACK;
					}
					double alpha = a / 255.0;
					r = (int) (alpha * r + (1 - alpha) * bkg.getRed());
					g = (int) (alpha * g + (1 - alpha) * bkg.getGreen());
					b = (int) (alpha * b + (1 - alpha) * bkg.getBlue());
				}

				for (int i = 0; i < code.length(); i++) {
					switch (code.charAt(i)) {
					case 'a':
					case 'A':
						bytes[index] = (byte) a;
						break;

					case 'r':
					case 'R':
						bytes[index] = (byte) r;
						break;

					case 'g':
					case 'G':
						bytes[index] = (byte) g;
						break;

					case 'b':
					case 'B':
						bytes[index] = (byte) b;
						break;

					default:
						Log.debug(ImageUtilities.class.getClass()
								+ ": Invalid code in '" + code + "'");
						break;
					}
					index++;
				}
			}
			for (int i = 0; i < (width % pad); i++) {
				bytes[index] = 0;
				index++;
			}
		}

		return bytes;
	}
}
