/**
 * EpsGraphics.java
 *
 * This file is part of the EPS Graphics Library
 * 
 * The EPS Graphics Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The EPS Graphics Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the EPS Graphics Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2001-2004, Paul Mutton
 * 
 * Copyright (c) 2006-2009, Thomas Abeel
 *  
 * Project: http://sourceforge.net/projects/epsgraphics/
 * 
 * based on original code by Paul Mutton, http://www.jibble.org/
 */
package org.geogebra.desktop.export.epsgraphics;

import java.util.LinkedList;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GQuadCurve2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GFontRenderContextD;

/**
 * EpsGraphics is suitable for creating high quality EPS graphics for use in
 * documents and papers, and can be used just like a standard Graphics2D object.
 * <p>
 * Many Java programs use Graphics2D to draw stuff on the screen, and while it
 * is easy to save the output as a png or jpeg file, it is a little harder to
 * export it as an EPS for including in a document or paper.
 * <p>
 * This class makes the whole process extremely easy, because you can use it as
 * if it's a Graphics2D object. The only difference is that all of the
 * implemented methods create EPS output, which means the diagrams you draw can
 * be resized without leading to any of the jagged edges you may see when
 * resizing pixel-based images, such as jpeg and png files.
 * <p>
 * Example usage:
 * <p>
 * 
 * <pre>
 * Graphics2D g = new EpsGraphics2D();
 * g.setColor(Color.black);
 * 
 * // Line thickness 2.
 * g.setStroke(new BasicStroke(2.0f));
 * 
 * // Draw a line.
 * g.drawLine(10, 10, 50, 10);
 * 
 * // Fill a rectangle in blue
 * g.setColor(Color.blue);
 * g.fillRect(10, 0, 20, 20);
 * 
 * // Get the EPS output.
 * String output = g.toString();
 * </pre>
 * 
 * <p>
 * You do not need to worry about the size of the canvas when drawing on a
 * EpsGraphics object. The bounding box of the EPS document will automatically
 * resize to accommodate new items that you draw.
 * <p>
 * Not all methods are implemented yet. Those that are not are clearly labelled.
 * 
 */
abstract public class EpsGraphics implements GGraphics2D {
	public static final String VERSION = "1.0.0";

	/**
	 * Constructs a new EPS document that is initially empty and can be drawn on
	 * like a Graphics2D object. The EPS document is written to the output
	 * stream as it goes, which reduces memory usage. The bounding box of the
	 * document is fixed and specified at construction time by
	 * minX,minY,maxX,maxY. The output stream is flushed and closed when the
	 * close() method is called.
	 * 
	 * @param bgColor
	 */
	public EpsGraphics(String title, StringBuilder outputStream, int minX,
			int minY, int maxX, int maxY, ColorMode colorMode, GColor bgColor) {
		_document = new EpsDocument(title, outputStream, minX, minY, maxX,
				maxY);
		this.colorMode = colorMode;
		_backgroundColor = bgColor == null ? GColor.WHITE : bgColor;
		_clip = null;
		_transform = AwtFactory.getPrototype().newAffineTransform();
		_clipTransform = AwtFactory.getPrototype().newAffineTransform();
		_accurateTextMode = true;
		setColor(GColor.BLACK);
		setPaint(GColor.BLACK);
		// setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		// setFont(GFont.decode(null));
		setStroke(AwtFactory.getPrototype().newBasicStroke(1));

	}

	/**
	 * Copy constructor. Creates a copy of this EPSGraphics object.
	 */
	public EpsGraphics(EpsGraphics g) {
		// System.err.println("G: " + g._document);
		EpsDocument doc = g._document;
		this._document = doc;
		this.colorMode = g.colorMode;
		_backgroundColor = g._backgroundColor;
		_clip = g.getClip();
		_transform = g.getTransform();
		_clipTransform = g._clipTransform;
		_accurateTextMode = true;
		setColor(g.getColor());
		setPaint(g.getPaint());
		// setComposite(g.getComposite());
		setFont(g.getFont());
		setStroke(g.getStroke());
	}

	

	// ///////////// Specialist methods ///////////////////////

	/**
	 * Sets whether to use accurate text mode when rendering text in EPS. This
	 * is enabled (true) by default. When accurate text mode is used, all text
	 * will be rendered in EPS to appear exactly the same as it would do when
	 * drawn with a Graphics2D context. With accurate text mode enabled, it is
	 * not necessary for the EPS viewer to have the required font installed.
	 * <p>
	 * Turning off accurate text mode will require the EPS viewer to have the
	 * necessary fonts installed. If you are using a lot of text, you will find
	 * that this significantly reduces the file size of your EPS documents.
	 * AffineTransforms can only affect the starting point of text using this
	 * simpler text mode - all text will be horizontal.
	 */
	public void setAccurateTextMode(boolean b) {
		_accurateTextMode = b;
		if (!getAccurateTextMode()) {
			setFont(getFont());
		}
	}

	/**
	 * Returns whether accurate text mode is being used.
	 */
	public boolean getAccurateTextMode() {
		return _accurateTextMode;
	}

	/**
	 * Appends a line to the EpsDocument.
	 */
	protected void append(String line) {
		_document.append(this, line);

	}

	/**
	 * Returns the point after it has been transformed by the transformation.
	 */
	protected GPoint2D transform(double x, double y) {
		GPoint2D result = new GPoint2D(x, y);
		result = _transform.transform(result, result);
		result.setLocation(result.getX(), -result.getY());
		return result;
	}

	/**
	 * Appends the commands required to draw a shape on the EPS document.
	 */
	protected void draw(GShape s0, String action, boolean subPath) {
		if (s0 != null) {
			GShape s = s0;
			// 20120115 bugfix: stroke needs to be appended each time
			if (!subPath) {
				appendStroke();
			}

			if (!_transform.isIdentity()) {
				s = _transform.createTransformedShape(s);
			}
			if (!subPath) {
				append("newpath");
			}
			int type = 0;
			double[] coords = new double[6];
			GPathIterator it = s.getPathIterator(null);
			double x0 = 0;
			double y0 = 0;
			while (!it.isDone()) {
				type = it.currentSegment(coords);
				double x1 = coords[0];
				double y1 = -coords[1];
				double x2 = coords[2];
				double y2 = -coords[3];
				double x3 = coords[4];
				double y3 = -coords[5];
				if (type == GPathIterator.SEG_CLOSE) {
					append("closepath");
				} else if (type == GPathIterator.SEG_CUBICTO) {
					append(x1 + " " + y1 + " " + x2 + " " + y2 + " " + x3 + " "
							+ y3 + " curveto");
					x0 = x3;
					y0 = y3;
				} else if (type == GPathIterator.SEG_LINETO) {
					append(x1 + " " + y1 + " lineto");
					x0 = x1;
					y0 = y1;
				} else if (type == GPathIterator.SEG_MOVETO) {
					append(x1 + " " + y1 + " moveto");
					x0 = x1;
					y0 = y1;
				} else if (type == GPathIterator.SEG_QUADTO) {
					// Convert the quad curve into a cubic.
					double _x1 = x0 + 2 / 3f * (x1 - x0);
					double _y1 = y0 + 2 / 3f * (y1 - y0);
					double _x2 = x1 + 1 / 3f * (x2 - x1);
					double _y2 = y1 + 1 / 3f * (y2 - y1);
					double _x3 = x2;
					double _y3 = y2;
					append(_x1 + " " + _y1 + " " + _x2 + " " + _y2 + " " + _x3
							+ " " + _y3 + " curveto");
					x0 = _x3;
					y0 = _y3;
				} else {
					Log.warn("unknown type " + type);
				}
				it.next();
			}
			if (!subPath) {
				append(action);
				append("newpath");
			}
		}
	}

	/**
	 * Returns a hex string that always contains two characters.
	 */
	protected static String toHexString(int n) {
		String result = Integer.toString(n, 16);
		while (result.length() < 2) {
			result = "0" + result;
		}
		return result;
	}

	// ///////////// Graphics2D methods ///////////////////////

	/**
	 * Draws a 3D rectangle outline. If it is raised, light appears to come from
	 * the top left.
	 */
	// @Override
	// public void draw3DRect(int x, int y, int width, int height, boolean
	// raised) {
	// Color originalColor = getColor();
	// Stroke originalStroke = getStroke();
	// setStroke(new BasicStroke(1.0f));
	// if (raised) {
	// setColor(originalColor.brighter());
	// } else {
	// setColor(originalColor.darker());
	// }
	// drawLine(x, y, x + width, y);
	// drawLine(x, y, x, y + height);
	// if (raised) {
	// setColor(originalColor.darker());
	// } else {
	// setColor(originalColor.brighter());
	// }
	// drawLine(x + width, y + height, x, y + height);
	// drawLine(x + width, y + height, x + width, y);
	// setColor(originalColor);
	// setStroke(originalStroke);
	// }

	/**
	 * Fills a 3D rectangle. If raised, it has bright fill and light appears to
	 * come from the top left.
	 */
	// @Override
	// public void fill3DRect(int x, int y, int width, int height, boolean
	// raised) {
	// Color originalColor = getColor();
	// if (raised) {
	// setColor(originalColor.brighter());
	// } else {
	// setColor(originalColor.darker());
	// }
	// draw(new Rectangle(x, y, width, height), "fill");
	// setColor(originalColor);
	// draw3DRect(x, y, width, height, raised);
	// }

	/**
	 * Draws a Shape on the EPS document.
	 */
	@Override
	public void draw(GShape s) {
		draw(s, "stroke", false);
	}

	/**
	 * Draws an Image on the EPS document.
	 */
	public void drawImage(GBufferedImage img, GAffineTransform xform) {
		GAffineTransform at = getTransform();
		transform(xform);
		drawImage(img, 0, 0, img.getWidth(), img.getHeight());
		setTransform(at);
	}

	/**
	 * Draws a BufferedImage on the EPS document.
	 */
	@Override
	public void drawImage(GBufferedImage img, int x, int y) {
		GAffineTransform transform = AwtFactory.getPrototype()
				.newAffineTransform();
		transform.setTransform(1f, 0f, 0f, 1f, x, y);
		// drawImage(op.filter(img, null), transform, null);
		drawImage(img, transform);
	}

	/**
	 * Draws a RenderedImage on the EPS document.
	 */
	// @Override
	// public void drawRenderedImage(GRenderedImage img, GAffineTransform xform)
	// {
	// Hashtable<String, Object> properties = new Hashtable<String, Object>();
	// String[] names = img.getPropertyNames();
	// for (int i = 0; i < names.length; i++) {
	// properties.put(names[i], img.getProperty(names[i]));
	// }
	// GColorModel cm = img.getColorModel();
	// GWritableRaster wr = img.copyData(null);
	// GBufferedImage img1 = new BufferedImage(cm, wr,
	// cm.isAlphaPremultiplied(), properties);
	// GAffineTransform at = GAffineTransform.getTranslateInstance(
	// img.getMinX(), img.getMinY());
	// at.preConcatenate(xform);
	// drawImage(img1, at, null);
	// }

	/**
	 * Draws a RenderableImage by invoking its createDefaultRendering method.
	 */
	// @Override
	// public void drawRenderableImage(GRenderableImage img, GAffineTransform
	// xform) {
	// drawRenderedImage(img.createDefaultRendering(), xform);
	// }

	/**
	 * Draws a string at (x,y)
	 */
	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float) x, (float) y);
	}

	/**
	 * Draws a string at (x,y)
	 */
	@Override
	public void drawString(String s, double x, double y) {
		if (s != null && s.length() > 0) {
			// AttributedString as = new AttributedString(s);
			// as.addAttribute(TextAttribute.FONT, getFont());
			// drawString(as.getIterator(), x, y);
			drawString(s, x, y, getFont());
		}
	}

	/**
	 * Draws the characters of an AttributedCharacterIterator, starting from
	 * (x,y).
	 */
	public abstract void drawString(String s, double x, double y, GFont font);

	/**
	 * Draws a GlyphVector at (x,y)
	 */
	// @Override
	// public void drawGlyphVector(GGlyphVector g, float x, float y) {
	// GShape shape = g.getOutline(x, y);
	// draw(shape, "fill");
	// }

	/**
	 * Fills a Shape on the EPS document.
	 */
	@Override
	public void fill(GShape s) {
		draw(s, "fill", false);
	}

	/**
	 * Checks whether or not the specified Shape intersects the specified
	 * Rectangle, which is in device space.
	 */
	// @Override
	// public boolean hit(GRectangle rect, GShape s, boolean onStroke) {
	// return s.intersects(rect);
	// }

	/**
	 * Returns the device configuration associated with this EpsGraphics2D
	 * object.
	 */
	// @Override
	// public GraphicsConfiguration getDeviceConfiguration() {
	// GraphicsConfiguration gc = null;
	// GraphicsEnvironment ge = GraphicsEnvironment
	// .getLocalGraphicsEnvironment();
	// GraphicsDevice[] gds = ge.getScreenDevices();
	// for (int i = 0; i < gds.length; i++) {
	// GraphicsDevice gd = gds[i];
	// GraphicsConfiguration[] gcs = gd.getConfigurations();
	// if (gcs.length > 0) {
	// return gcs[0];
	// }
	// }
	// return gc;
	// }

	/**
	 * Sets the Composite to be used by this EpsGraphics2D. EpsGraphics2D does
	 * not make use of these.
	 */
	@Override
	public void setComposite(GComposite comp) {
		_composite = comp;
	}

	/**
	 * Sets the Paint attribute for the EpsGraphics2D object. Only Paint objects
	 * of type Color are respected by EpsGraphics2D.
	 */
	@Override
	public void setPaint(GPaint paint) {
		_paint = paint;
		if (paint instanceof GColor) {
			setColor((GColor) paint);
		}
	}

	/*
	 * 20120115 bugfix: stroke needs to be appended each time (non-Javadoc)
	 * 
	 * @see java.awt.Graphics2D#setStroke(java.awt.Stroke)
	 */
	@Override
	public void setStroke(final GBasicStroke currentStroke) {
		this.currentStroke = currentStroke;
	}

	GBasicStroke currentStroke = null;

	/**
	 * Sets the stroke. Only accepts BasicStroke objects (or subclasses of
	 * BasicStroke).
	 */
	public void appendStroke() {
		if (currentStroke != null) {
			_stroke = currentStroke;
			append(_stroke.getLineWidth() + " setlinewidth");
			double miterLimit = _stroke.getMiterLimit();
			if (miterLimit < 1.0f) {
				miterLimit = 1;
			}
			append(miterLimit + " setmiterlimit");
			append(_stroke.getLineJoin() + " setlinejoin");
			append(_stroke.getEndCap() + " setlinecap");
			StringBuffer dashes = new StringBuffer();
			dashes.append("[ ");
			double[] dashArray = _stroke.getDashArray();
			if (dashArray != null) {
				for (int i = 0; i < dashArray.length; i++) {
					dashes.append((dashArray[i]) + " ");
				}
			}
			dashes.append("]");
			append(dashes.toString() + " 0 setdash");
		}
	}

	/**
	 * Translates the origin of the EpsGraphics2D context to the point (x,y) in
	 * the current coordinate system.
	 */
	// @Override
	// public void translate(int x, int y) {
	// translate((double) x, (double) y);
	// }

	/**
	 * Concatenates the current EpsGraphics2D Transformation with a translation
	 * transform.
	 */
	@Override
	public void translate(double tx, double ty) {
		transform(AwtFactory.getTranslateInstance(tx, ty));
	}

	/**
	 * Concatenates the current EpsGraphics2D Transform with a rotation
	 * transform.
	 * 
	 * @param theta
	 *            anglein radians
	 */
	// @Override
	public void rotate(double theta) {
		rotate(theta, 0, 0);
	}

	/**
	 * Concatenates the current EpsGraphics2D Transform with a translated
	 * rotation transform.
	 */
	// @Override
	public void rotate(double theta, double x, double y) {
		transform(AwtFactory.getRotateInstance(theta, x, y));
	}

	/**
	 * Concatenates the current EpsGraphics2D Transform with a scaling
	 * transformation.
	 */
	@Override
	public void scale(double sx, double sy) {
		transform(AwtFactory.getScaleInstance(sx, sy));
	}

	/**
	 * Concatenates the current EpsGraphics2D Transform with a shearing
	 * transform.
	 */
	// @Override
	// public void shear(double shx, double shy) {
	// transform(AffineTransform.getShearInstance(shx, shy));
	// }

	/**
	 * Composes an AffineTransform object with the Transform in this
	 * EpsGraphics2D according to the rule last-specified-first-applied.
	 */
	@Override
	public void transform(GAffineTransform Tx) {
		_transform.concatenate(Tx);
		setTransform(getTransform());
	}

	/**
	 * Sets the AffineTransform to be used by this EpsGraphics2D.
	 */
	public void setTransform(GAffineTransform Tx) {
		if (Tx == null) {
			_transform = AwtFactory.getPrototype().newAffineTransform();
		} else {
			_transform = AwtFactory.getPrototype().newAffineTransform();
			_transform.setTransform(Tx);
		}
		// Need to update the stroke and font so they know the scale changed
		setStroke(getStroke());
		setFont(getFont());
	}

	/**
	 * Gets the AffineTransform used by this EpsGraphics2D.
	 */
	public GAffineTransform getTransform() {
		GAffineTransform ret = AwtFactory.getPrototype().newAffineTransform();
		ret.setTransform(_transform);
		return ret;
	}

	/**
	 * Returns the current Paint of the EpsGraphics2D object.
	 */
	public GPaint getPaint() {
		if (_paint == null) {
			return GColor.BLACK;
		}
		return _paint;
	}

	/**
	 * returns the current Composite of the EpsGraphics2D object.
	 */
	@Override
	public GComposite getComposite() {
		return _composite;
	}

	/**
	 * Sets the background color to be used by the clearRect method.
	 * 
	 * @param color
	 *            background
	 */
	public void setBackground(GColor color) {
		if (color == null) {
			_backgroundColor = GColor.BLACK;
		} else {
			_backgroundColor = color;
		}
	}

	/**
	 * Gets the background color that is used by the clearRect method.
	 */
	@Override
	public GColor getBackground() {
		return _backgroundColor;
	}

	/**
	 * Returns the Stroke currently used. Guaranteed to be an instance of
	 * BasicStroke.
	 */
	@Override
	public GBasicStroke getStroke() {
		return _stroke;
	}

	/**
	 * Returns the FontRenderContext.
	 */
	@Override
	public GFontRenderContext getFontRenderContext() {

		if (_fontRenderContext == null) {
			_fontRenderContext = getNewFontRenderContext();
		}

		return _fontRenderContext;
	}

	// ///////////// Graphics methods ///////////////////////

	/**
	 * Returns an EpsGraphics2D object based on this Graphics object, but with a
	 * new translation and clip area.
	 */
	// @Override
	// public Graphics create(int x, int y, int width, int height) {
	// Graphics g = create();
	// g.translate(x, y);
	// g.clipRect(0, 0, width, height);
	// return g;
	// }

	/**
	 * Returns the current Color. This will be a default value (black) until it
	 * is changed using the setColor method.
	 */
	@Override
	public GColor getColor() {
		if (color == null) {
			return GColor.BLACK;
		}
		return color;
	}

	/**
	 * Sets the Color to be used when drawing all future shapes, text, etc.
	 */
	@Override
	public void setColor(GColor color) {
		GColor color1 = color;
		if (color1 == null) {
			color1 = GColor.BLACK;
		}

		float red = color1.getRed() / 255f;
		float green = color1.getGreen() / 255f;
		float blue = color1.getBlue() / 255f;

		float alpha = color1.getAlpha() / 255f;

		if (alpha != 1) {

			float bgRed = _backgroundColor.getRed() / 255f;
			float bgGreen = _backgroundColor.getGreen() / 255f;
			float bgBlue = _backgroundColor.getBlue() / 255f;

			// GGB-1146
			// fake transparency, work out correct color assuming shape
			// is being drawn on the background and not over something else
			// https://en.wikipedia.org/wiki/Alpha_compositing
			red = bgRed * (1 - alpha) + red * alpha;
			green = bgGreen * (1 - alpha) + green * alpha;
			blue = bgBlue * (1 - alpha) + blue * alpha;

		}

		this.color = color1;
		switch (colorMode) {
		case BLACK_AND_WHITE:
			double value = 0;
			if (red + green + blue > 0.5) {
				value = 1;
			}
			append(value + " setgray");
			break;
		case GRAYSCALE:
			double grayvalue = (red + green + blue) / 3;
			append(grayvalue + " setgray");
			break;
		case COLOR_RGB:
			append(red + " " + green + " " + blue + " setrgbcolor");
			break;
		case COLOR_CMYK:
			if (color1.equals(GColor.BLACK)) {
				append("0.0 0.0 0.0 1.0 setcmykcolor");
			} else {
				double c = 1 - red;
				double m = 1 - green;
				double y = 1 - blue;
				double k = Math.min(Math.min(c, y), m);

				append((c - k) / (1 - k) + " " + (m - k) / (1 - k) + " "
						+ (y - k) / (1 - k) + " " + k + " setcmykcolor");
			}
			break;

		}

	}

	/**
	 * Sets the paint mode of this EpsGraphics2D object to overwrite the
	 * destination EpsDocument with the current color.
	 */
	// @Override
	// public void setPaintMode() {
	// // Do nothing - paint mode is the only method supported anyway.
	// }

	/**
	 * <b><i><font color="red">Not implemented</font></i></b> - performs no
	 * action.
	 */
	// @Override
	// public void setXORMode(GColor c1) {
	// methodNotSupported();
	// }

	/**
	 * Returns the Font currently being used.
	 */
	@Override
	public GFont getFont() {
		return _font;
	}

	/**
	 * Sets the Font to be used in future text.
	 */
	@Override
	public void setFont(GFont font) {
		// if (font == null) {
		// font = GFont.decode(null);
		// }
		_font = font;
		// if (!getAccurateTextMode()) {
		// append("/" + _font.getPSName() + " findfont " + _font.getSize()
		// + " scalefont setfont");
		// }
	}

	/**
	 * Gets the font metrics of the current font.
	 */
	// @Override
	// public FontMetrics getFontMetrics() {
	// return getFontMetrics(getFont());
	// }

	/**
	 * Gets the font metrics for the specified font.
	 */
	// @Override
	// public FontMetrics getFontMetrics(GFont f) {
	// BufferedImage image = new BufferedImage(1, 1,
	// BufferedImage.TYPE_INT_RGB);
	// Graphics g = image.getGraphics();
	// return g.getFontMetrics(f);
	// }

	/**
	 * Returns the bounding rectangle of the current clipping area.
	 */
	// @Override
	// public GRectangle getClipBounds() {
	// if (_clip == null) {
	// return null;
	// }
	// Rectangle rect = getClip().getBounds();
	// return rect;
	// }

	/**
	 * Intersects the current clip with the specified rectangle.
	 */
	// @Override
	// public void clipRect(int x, int y, int width, int height) {
	// clip(AwtFactory.getPrototype().newRectangle(x, y, width, height));
	// }

	/**
	 * Sets the current clip to the rectangle specified by the given
	 * coordinates.
	 */
	@Override
	public void setClip(int x, int y, int width, int height) {
		setClip(AwtFactory.getPrototype().newRectangle(x, y, width, height));
	}

	@Override
	public void setClip(int x, int y, int width, int height,
			boolean saveContext) {
		setClip(x, y, width, height);
	}

	/**
	 * Gets the current clipping area.
	 */
	protected GShape getClip() {
		if (_clip == null) {
			return null;
		}
		try {
			GAffineTransform t = _transform.createInverse();
			t.concatenate(_clipTransform);
			return t.createTransformedShape(_clip);
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to get inverse of matrix: " + _transform);
		}
	}

	/**
	 * Sets the current clipping area to an arbitrary clip shape.
	 */
	@Override
	public void setClip(GShape clip) {
		if (clip != null) {
			if (_document.isClipSet()) {
				append("grestore");
				append("gsave");
			} else {
				_document.setClipSet(true);
				append("gsave");
			}
			draw(clip, "clip", false);
			_clip = clip;
			// _clipTransform = (GAffineTransform) _transform.clone();
			_clipTransform = AwtFactory.getPrototype().newAffineTransform();
			_clipTransform.setTransform(_transform);
		} else {
			if (_document.isClipSet()) {
				append("grestore");
				_document.setClipSet(false);
			}
			_clip = null;
		}
	}

	@Override
	public void setClip(GShape clip, boolean saveContext) {
		setClip(clip);
	}

	/**
	 * <b><i><font color="red">Not implemented</font></i></b> - performs no
	 * action.
	 */
	// @Override
	// public void copyArea(int x, int y, int width, int height, int dx, int dy)
	// {
	// methodNotSupported();
	// }

	/**
	 * Draws a straight line from (x1,y1) to (x2,y2).
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		GLine2D line1 = AwtFactory.getPrototype().newLine2D();
		line1.setLine(x1, y1, x2, y2);
		draw(line1);
	}

	/**
	 * Fills a rectangle with top-left corner placed at (x,y).
	 */
	@Override
	public void fillRect(int x, int y, int width, int height) {
		GShape shape = AwtFactory.getPrototype().newRectangle(x, y, width,
				height);
		draw(shape, "fill", false);
	}

	/**
	 * Draws a rectangle with top-left corner placed at (x,y).
	 */
	@Override
	public void drawRect(int x, int y, int width, int height) {
		GShape shape = AwtFactory.getPrototype().newRectangle(x, y, width,
				height);
		draw(shape);
	}

	/**
	 * Clears a rectangle with top-left corner placed at (x,y) using the current
	 * background color.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	@Override
	public void clearRect(int x, int y, int width, int height) {
		GColor originalColor = getColor();
		setColor(getBackground());
		GShape shape = AwtFactory.getPrototype().newRectangle(x, y, width,
				height);
		draw(shape, "fill", false);
		setColor(originalColor);
	}

	/**
	 * Draws a rounded rectangle.
	 * 
	 * adapted from GGraphics2DW.drawRoundRectangle()
	 */
	@Override
	public void drawRoundRect(int x, int y, int width, int height, int radius,
			int arcHeight) {

		drawLine(x + radius, y, x + width - radius, y);

		GQuadCurve2D arc = AwtFactory.getPrototype().newQuadCurve2D();
		arc.setCurve(x + width - radius, y, x + width, y, x + width,
				y + radius);
		draw(arc);

		drawLine(x + width, y + radius, x + width, y + height - radius);

		arc.setCurve(x + width, y + height - radius, x + width, y + height,
				x + width - radius, y + height);
		draw(arc);

		drawLine(x + width - radius, y + height, x + radius, y + height);

		arc.setCurve(x + radius, y + height, x, y + height, x,
				y + height - radius);
		draw(arc);

		drawLine(x, y + height - radius, x, y + radius);

		arc.setCurve(x, y + radius, x, y, x + radius, y);
		draw(arc);

	}

	/**
	 * Fills a rounded rectangle.
	 */
	@Override
	public void fillRoundRect(int x, int y, int width, int height, int radius,
			int arcHeight) {

		appendStroke();
		append("newpath");

		GLine2D line1 = AwtFactory.getPrototype().newLine2D();
		line1.setLine(x + radius, y, x + width - radius, y);
		draw(line1, null, true);

		GQuadCurve2D arc = AwtFactory.getPrototype().newQuadCurve2D();
		arc.setCurve(x + width - radius, y, x + width, y, x + width,
				y + radius);
		draw(arc, null, true);

		line1.setLine(x + width, y + radius, x + width, y + height - radius);
		draw(line1, null, true);

		arc.setCurve(x + width, y + height - radius, x + width, y + height,
				x + width - radius, y + height);
		draw(arc, null, true);

		line1.setLine(x + width - radius, y + height, x + radius, y + height);
		draw(line1, null, true);

		arc.setCurve(x + radius, y + height, x, y + height, x,
				y + height - radius);
		draw(arc, null, true);

		line1.setLine(x, y + height - radius, x, y + radius);
		draw(line1, null, true);

		arc.setCurve(x, y + radius, x, y, x + radius, y);
		draw(arc, null, true);

		append("fill");
		append("newpath");

	}

	/**
	 * Draws an oval.
	 */
	// @Override
	// public void drawOval(int x, int y, int width, int height) {
	// Shape shape = new Ellipse2D.Double(x, y, width, height);
	// draw(shape);
	// }

	/**
	 * Fills an oval.
	 */
	// @Override
	// public void fillOval(int x, int y, int width, int height) {
	// Shape shape = new Ellipse2D.Double(x, y, width, height);
	// draw(shape, "fill");
	// }

	/**
	 * Draws an arc.
	 */
	// @Override
	// public void drawArc(int x, int y, int width, int height, int startAngle,
	// int arcAngle) {
	// Shape shape = new Arc2D.Double(x, y, width, height, startAngle,
	// arcAngle, Arc2D.OPEN);
	// draw(shape);
	// }

	/**
	 * Fills an arc.
	 */
	// @Override
	// public void fillArc(int x, int y, int width, int height, int startAngle,
	// int arcAngle) {
	// Shape shape = new Arc2D.Double(x, y, width, height, startAngle,
	// arcAngle, Arc2D.PIE);
	// draw(shape, "fill");
	// }

	/**
	 * Draws a polyline.
	 */
	// @Override
	// public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
	// if (nPoints > 0) {
	// GeneralPath path = new GeneralPath();
	// path.moveTo(xPoints[0], yPoints[0]);
	// for (int i = 1; i < nPoints; i++) {
	// path.lineTo(xPoints[i], yPoints[i]);
	// }
	// draw(path);
	// }
	// }

	/**
	 * Draws a polygon made with the specified points.
	 */
	// @Override
	// public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	// Shape shape = new Polygon(xPoints, yPoints, nPoints);
	// draw(shape);
	// }

	/**
	 * Draws a polygon.
	 */
	// public void drawPolygon(GPolygon p) {
	// draw(p);
	// }

	/**
	 * Fills a polygon made with the specified points.
	 */
	// @Override
	// public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	// Shape shape = new Polygon(xPoints, yPoints, nPoints);
	// draw(shape, "fill");
	// }

	/**
	 * Fills a polygon.
	 */
	// public void fillPolygon(GPolygon p) {
	// draw(p, "fill", false);
	// }

	/**
	 * Draws the specified characters, starting from (x,y)
	 */
	// @Override
	// public void drawChars(char[] data, int offset, int length, int x, int y)
	// {
	// String string = new String(data, offset, length);
	// drawString(string, x, y);
	// }

	/**
	 * Draws the specified bytes, starting from (x,y)
	 */
	// @Override
	// public void drawBytes(byte[] data, int offset, int length, int x, int y)
	// {
	// String string = new String(data, offset, length);
	// drawString(string, x, y);
	// }

	/**
	 * Draws an image.
	 */
	public void drawImage(GBufferedImage img, int x, int y, int width,
			int height) {
		drawImage(img, x, y, width, height, GColor.WHITE);
	}

	/**
	 * Draws an image.
	 */
	public void drawImage(GBufferedImage img, int x, int y, GColor bgcolor) {
		int width = img.getWidth();
		int height = img.getHeight();
		drawImage(img, x, y, width, height, bgcolor);
	}

	/**
	 * Draws an image.
	 */
	public void drawImage(GBufferedImage img, int x, int y, int width,
			int height, GColor bgcolor) {
		drawImage(img, x, y, x + width, y + height, 0, 0, width, height,
				bgcolor);
	}

	/**
	 * Draws an image.
	 */
	public void drawImage(GBufferedImage img, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, GColor.WHITE);
	}

	/**
	 * Draws an image.
	 */
	abstract public void drawImage(GBufferedImage img, int dx1, int dy1,
			int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			GColor bgcolor);

	/**
	 * Returns the entire contents of the EPS document, complete with headers
	 * and bounding box. The returned String is suitable for being written
	 * directly to disk as an EPS file.
	 */
	@Override
	public String toString() {
		if (_document == null) {
			return "";
		}

		return _document.getStream().toString();
	}

	/**
	 * Returns true if the specified rectangular area might intersect the
	 * current clipping area.
	 */
	// @Override
	// public boolean hitClip(int x, int y, int width, int height) {
	// if (_clip == null) {
	// return true;
	// }
	// Rectangle rect = new Rectangle(x, y, width, height);
	// return hit(rect, _clip, true);
	// }

	/**
	 * Returns the bounding rectangle of the current clipping area.
	 */
	// @Override
	// public GRectangle getClipBounds(GRectangle r) {
	// if (_clip == null) {
	// return r;
	// }
	// GRectangle rect = getClipBounds();
	// r.setLocation((int) rect.getX(), (int) rect.getY());
	// r.setSize((int) rect.getWidth(), (int) rect.getHeight());
	// return r;
	// }

	private GColor color;

	private GAffineTransform _clipTransform;

	private GColor _backgroundColor;

	private GPaint _paint;

	private GComposite _composite;

	private GBasicStroke _stroke;

	private GFont _font;

	private GShape _clip;

	protected GAffineTransform _transform = AwtFactory.getPrototype()
			.newAffineTransform();

	private boolean _accurateTextMode;

	private EpsDocument _document;

	private GFontRenderContext _fontRenderContext;

	protected ColorMode colorMode = ColorMode.COLOR_RGB;

	abstract public GGraphics2D create();

	@Override
	abstract public void drawImage(MyImage img, int x, int y);

	@Override
	public void setAntialiasing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTransparent() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object setInterpolationHint(
			boolean needsInterpolationRenderingHint) {
		return null;
	}

	@Override
	public void resetInterpolationHint(Object oldInterpolationHint) {
		//
	}

	@Override
	public void updateCanvasColor() {
		// TODO Auto-generated method stub

	}

	private GLine2D line = AwtFactory.getPrototype().newLine2D();

	@Override
	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		line.setLine(x1, y1, x2, y2);
		this.draw(line);
	}

	@Override
	public void startGeneralPath() {
		// do nothing, used in web
	}

	@Override
	public void addStraightLineToGeneralPath(double x1, double y1, double x2, double y2) {
		drawStraightLine(x1, y1, x2, y2);
	}

	@Override
	public void endAndDrawGeneralPath() {
		// do nothing, used in web
	}

	private LinkedList<GAffineTransform> transformationStack = new LinkedList<>();

	@Override
	public void saveTransform() {
		transformationStack.add(getTransform());
	}

	@Override
	public void restoreTransform() {
		_transform = transformationStack.removeLast();
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		drawString(new String(data).substring(offset, offset + length), x, y);
	}

	protected abstract GFontRenderContextD getNewFontRenderContext();

}
