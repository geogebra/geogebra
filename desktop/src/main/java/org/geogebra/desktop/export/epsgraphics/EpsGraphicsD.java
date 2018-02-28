package org.geogebra.desktop.export.epsgraphics;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.awt.GFontRenderContextD;
import org.geogebra.desktop.awt.GGenericShapeD;
import org.geogebra.desktop.awt.GTextLayoutD;
import org.geogebra.desktop.gui.MyImageD;

public class EpsGraphicsD extends EpsGraphics {

	@Override
	final protected GFontRenderContextD getNewFontRenderContext() {
		return new GFontRenderContextD(
				new FontRenderContext(null, false, true));
	}

	public EpsGraphicsD(String string, StringBuilder epsOutput, int i, int j,
			int pixelWidth, int pixelHeight, ColorMode colorRgb,
			GColor bgColor) {
		super(string, epsOutput, i, j, pixelWidth, pixelHeight, colorRgb,
				bgColor);
	}

	public EpsGraphicsD(EpsGraphicsD eps) {
		super(eps);
	}

	@Override
	public GGraphics2D create() {
		return new EpsGraphicsD(this);
	}

	@Override
	public void drawString(String s, double x, double y, GFont font) {
		if (getAccurateTextMode()) {
			GTextLayout layout = AwtFactory.getPrototype().newTextLayout(s,
					font, getFontRenderContext());

			// methodNotSupported();
			// GShape shape = layout
			// .getOutline(AwtFactory.getTranslateInstance(x, y));

			GTextLayoutD layoutD = (GTextLayoutD) layout;
			TextLayout layoutNative = layoutD.getImpl();
			Shape shapeNative = layoutNative
					.getOutline(AffineTransform.getTranslateInstance(x, y));
			GShape shape = new GGenericShapeD(shapeNative);

			draw(shape, "fill", false);
		} else {
			append("newpath");
			GPoint2D location = transform(x, y);
			append(location.getX() + " " + location.getY() + " moveto");
			StringBuilder buffer = new StringBuilder();
			// for (char ch = iterator.first(); ch != CharacterIterator.DONE; ch
			// = iterator
			// .next()) {
			for (int i = 0; i < s.length(); i++) {

				char ch = s.charAt(i);
				if (ch == '(' || ch == ')') {
					buffer.append('\\');
				}
				buffer.append(ch);
			}
			append("(" + buffer.toString() + ") show");
		}
	}

	@Override
	public void drawImage(GBufferedImage img, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2, GColor bgcolor) {
		if (dx1 >= dx2) {
			throw new IllegalArgumentException("dx1 >= dx2");
		}
		if (sx1 >= sx2) {
			throw new IllegalArgumentException("sx1 >= sx2");
		}
		if (dy1 >= dy2) {
			throw new IllegalArgumentException("dy1 >= dy2");
		}
		if (sy1 >= sy2) {
			throw new IllegalArgumentException("sy1 >= sy2");
		}
		append("gsave");
		int width = sx2 - sx1;
		int height = sy2 - sy1;
		int destWidth = dx2 - dx1;
		int destHeight = dy2 - dy1;
		int[] pixels = new int[width * height];
		PixelGrabber pg = new PixelGrabber(
				GBufferedImageD.getAwtBufferedImage(img), sx1, sy1, sx2 - sx1,
				sy2 - sy1, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			return;
		}
		GAffineTransform matrix = AwtFactory.getPrototype()
				.newAffineTransform();
		matrix.setTransform(_transform);
		matrix.translate(dx1, dy1);
		matrix.scale(destWidth / (double) width, destHeight / (double) height);
		double[] m = new double[6];
		try {
			matrix = matrix.createInverse();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to get inverse of matrix: " + matrix);
		}
		matrix.scale(1, -1);
		matrix.getMatrix(m);
		String bitsPerSample = "8";
		// TODO Not using proper imagemask function yet
		// if (getColorDepth() == BLACK_AND_WHITE) {
		// bitsPerSample = "true";
		// }
		append(width + " " + height + " " + bitsPerSample + " [" + m[0] + " "
				+ m[1] + " " + m[2] + " " + m[3] + " " + m[4] + " " + m[5]
				+ "]");
		// Fill the background to update the bounding box.
		GColor oldColor = getColor();
		setColor(getBackground());
		fillRect(dx1, dy1, destWidth, destHeight);
		setColor(oldColor);
		if (this.colorMode.equals(ColorMode.BLACK_AND_WHITE)
				|| this.colorMode.equals(ColorMode.GRAYSCALE)) {
			// TODO Should really use imagemask.
			append("{currentfile " + width + " string readhexstring pop} bind");
			append("image");
		} else {// TODO: no difference between RGB and CMYK
			append("{currentfile 3 " + width
					+ " mul string readhexstring pop} bind");
			append("false 3 colorimage");
		}
		StringBuffer line = new StringBuffer();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				GColor color = GColor.newColorRGB(pixels[x + width * y]);
				if (this.colorMode.equals(ColorMode.BLACK_AND_WHITE)) {
					if (color.getRed() + color.getGreen()
							+ color.getBlue() > 255 * 1.5 - 1) {
						line.append("ff");
					} else {
						line.append("00");
					}
				} else if (this.colorMode.equals(ColorMode.GRAYSCALE)) {
					line.append(toHexString((color.getRed() + color.getGreen()
							+ color.getBlue()) / 3));
				} else {// TODO: no difference between RGB and CMYK
					line.append(toHexString(color.getRed())
							+ toHexString(color.getGreen())
							+ toHexString(color.getBlue()));
				}
				if (line.length() > 64) {
					append(line.toString());
					line = new StringBuffer();
				}
			}
		}
		if (line.length() > 0) {
			append(line.toString());
		}
		append("grestore");
	}

	@Override
	public void drawImage(MyImage img, int x, int y) {

		MyImageD imgd = (MyImageD) img;
		BufferedImage bi = (BufferedImage) imgd.getImage();

		drawImage(new GBufferedImageD(bi), x, y);

	}

	@Override
	public void setRenderingHint(int hintKey, int hintValue) {
		// nothing to do

	}

	@Override
	public void resetClip() {
		this.setClip(null);

	}

	@Override
	public void drawImage(MyImage img, int sx, int sy, int sw, int sh, int dx,
			int dy) {
		drawImage(img, dx, dy);
	}

}
