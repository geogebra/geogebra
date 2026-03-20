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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.euclidian.draw.DrawText;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.AwtFactoryD;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.euclidian.DrawEquationD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.ScaledIcon;
import org.geogebra.desktop.util.ImageResourceD;
import org.geogebra.editor.share.util.Unicode;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;

/**
 * Creates various ImageIcons for use in lists and tables.
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings("javadoc")
public class GeoGebraIconD {

	private static final int HGRID_MARGIN = 4;
	private static final int VGRID_MARGIN = 4;

	/**
	 * @param width width
	 * @param height height
	 * @return empty icon with given size
	 */
	public static ScaledIcon createEmptyIcon(int width, int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		return new ScaledIcon(image, 1.0);
	}

	/**
	 * @param width width
	 * @param height height
	 * @return |X| shaped icon
	 */
	public static ScaledIcon createNullSymbolIcon(int width, int height, double scale) {
		BufferedImage image = newBufferedImage(width, height, scale);

		Graphics2D g2 = createGraphics(image);

		g2.setPaint(Color.GRAY);
		g2.scale(scale, scale);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width - 6, height - 6);
		int k = 7;
		g2.drawLine(k, k, width - k, height - k);
		g2.drawLine(k, height - k, width - k, k);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param res resource
	 * @return icon with given image
	 */
	public static ScaledIcon createFileImageIcon(ImageResourceD res, double scale) {
		URL url = GeoGebraIconD.class.getResource(res.getFilename());

		try {
			JSVGIcon icon = new JSVGIcon(url);
			icon.setAntiAlias(true);
			icon.setPreferredSize(new Dimension(32, 32));

			BufferedImage bufferedImage = newBufferedImage(icon.getIconWidth() + 5,
					icon.getIconHeight() + 5, scale);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.scale(scale, scale);
			icon.paintIcon(null, graphics, 0, 0);
			graphics.dispose();
			return new ScaledIcon(bufferedImage, scale);
		} catch (Exception e) {
			Log.debug("Error loading icon: " + url);
		}
		return null;

	}

	/**
	 * @param iconSize size
	 * @return horizontal grid icon
	 */
	public static ScaledIcon createHGridIcon(Dimension iconSize, double scale) {

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);
		g2.scale(scale, scale);
		// draw 3 horizontal lines
		g2.setPaint(Color.GRAY);
		int gap = h / 4;
		int margin = (h - 2 * gap) / 2;

		for (int i = 0; i < 3; i++) {
			int y = margin + i * gap;

			g2.drawLine(HGRID_MARGIN, y, h - HGRID_MARGIN, y);
		}
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param iconSize size
	 * @return vertical grid icon
	 */
	public static ScaledIcon createVGridIcon(Dimension iconSize, double scale) {

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);
		g2.scale(scale, scale);
		// draw 3 horizontal lines
		g2.setPaint(Color.GRAY);
		int gap = w / 4;
		int margin = (w - 2 * gap) / 2;
		for (int i = 0; i < 3; i++) {

			int x = margin + i * gap;
			g2.drawLine(x, VGRID_MARGIN, x, h - VGRID_MARGIN);
		}
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	private static Graphics2D createGraphics(BufferedImage image) {
		Graphics2D g2 = image.createGraphics();
		GGraphics2DD.setAntialiasing(g2);
		return g2;
	}

	/**
	 * @param font0 font
	 * @return symbol icon with alpha in a rectangle
	 */
	public static ScaledIcon createSymbolTableIcon(Font font0, double scale) {
		int s = 14;
		String alpha = Unicode.alpha + "";

		BufferedImage image = newBufferedImage(s, s, scale);

		Graphics2D g2 = createGraphics(image);

		g2.setPaint(Color.DARK_GRAY);

		Font font = font0.deriveFont(Font.BOLD, s);
		g2.scale(scale, scale);
		g2.setFont(font);
		g2.setColor(Color.DARK_GRAY);
		drawCenteredText(g2, alpha, s / 2 - 1, s / 2);

		g2.setColor(Color.GRAY);
		g2.drawRect(0, 0, s - 2, s - 2);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawLine(s - 1, 1, s - 1, s - 1);
		g2.drawLine(1, s - 1, s - 1, s - 1);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	private static void drawCenteredText(Graphics2D graphics, String text,
			int centerX, int centerY) {

		// get the visual center of the component.
		// int centerX = getWidth()/2;
		// int centerY = getHeight()/2;

		// get the bounds of the string to draw.
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle stringBounds = fontMetrics.getStringBounds(text, graphics)
				.getBounds();

		// get the visual bounds of the text using a GlyphVector.
		Font font = graphics.getFont();
		FontRenderContext renderContext = graphics.getFontRenderContext();
		GlyphVector glyphVector = font.createGlyphVector(renderContext, text);
		Rectangle visualBounds = glyphVector.getVisualBounds().getBounds();

		// calculate the lower left point at which to draw the string. note that
		// this we
		// give the graphics context the y coordinate at which we want the
		// baseline to
		// be placed. use the visual bounds height to center on in conjunction
		// with the
		// position returned in the visual bounds. the vertical position given
		// back in the
		// visualBounds is a negative offset from the baseline of the text.
		int textX = centerX - stringBounds.width / 2;
		int textY = centerY - visualBounds.height / 2 - visualBounds.y;

		graphics.drawString(text, textX, textY);
	}

	/**
	 * Creates an icon for a popup list --- two triangles pointing up and down
	 * @return icon for popup
	 */
	public static ScaledIcon createUpDownTriangleIcon(boolean isRollOver,
			boolean isEnabled, double scale) {

		int h = 18;
		int w = 12;
		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);

		if (!isEnabled) {
			return new ScaledIcon(image, scale);
		}
		if (isRollOver) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(0, 0, w - 1, h - 1);
		}

		g2.setColor(Color.GRAY);
		// g2.drawRect(0, 0, w-1, h-1);

		// g2.setColor(Color.LIGHT_GRAY);
		// g2.drawLine(w-1,1, w-1, h-1);
		// g2.drawLine(1,h-1, w-1, h-1);

		if (isRollOver) {
			g2.setColor(Color.BLACK);
		} else {
			g2.setColor(Color.DARK_GRAY);
		}

		int midx = w / 2;
		int midy = h / 2;

		Polygon p = new Polygon();
		// make a triangle.
		p.addPoint(midx - 3, midy - 1);
		p.addPoint(midx + 3, midy - 1);
		p.addPoint(midx, midy - 6);

		g2.fillPolygon(p);

		// make a triangle.
		p = new Polygon();
		p.addPoint(midx - 3, midy + 1);
		p.addPoint(midx + 3, midy + 1);
		p.addPoint(midx, midy + 6);

		g2.fillPolygon(p);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param alpha opacity
	 * @param iconSize size
	 * @param fgColor0 color
	 * @param bgColor background
	 * @return color swatch icon
	 */
	public static ScaledIcon createColorSwatchIcon(double alpha,
			Dimension iconSize, Color fgColor0, Color bgColor,
			double scale) {

		int h = iconSize.height;
		int w = iconSize.width;

		// if fgColor is null then make it a transparent white
		Color fgColor = fgColor0;
		if (fgColor0 == null) {
			fgColor = new Color(255, 255, 255, 1);
		}

		BufferedImage image = newBufferedImage(w, h, scale);
		Graphics2D g2 = createGraphics(image);

		--h;
		--w;
		g2.scale(scale, scale);
		if (bgColor != null) {
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// interior fill color using alpha level

		float[] rgb = new float[3];
		int offset = 2;
		fgColor.getRGBColorComponents(rgb);
		g2.setPaint(new Color(rgb[0], rgb[1], rgb[2], (float) alpha));
		g2.fillRect(offset, offset, w - 2 * offset, h - 2 * offset);

		// border color with alpha = 1
		float thickness = 3;
		g2.setPaint(fgColor);
		g2.setStroke(new BasicStroke(thickness));
		g2.drawRect(offset, offset, w - 2 * offset, h - 2 * offset);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param dashStyle line style (dashing)
	 * @param thickness thickness
	 * @param iconSize size
	 * @param fgColor line color
	 * @param bgColor background color
	 * @return image of a dashed line
	 */
	public static ScaledIcon createLineStyleIcon(int dashStyle, int thickness,
			Dimension iconSize, Color fgColor, Color bgColor, double scale) {

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);
		g2.scale(scale, scale);
		if (bgColor != null) {
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// draw dashed line
		g2.setPaint(fgColor);
		g2.setStroke(AwtFactoryD
				.getAwtStroke(EuclidianStatic.getStroke(thickness, dashStyle)));
		int mid = h / 2;
		g2.drawLine(4, mid, w - 4, mid);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * TODO unify with createStringIcon?
	 * @param symbol text
	 * @param font font
	 * @param iconSize size
	 * @param fgColor text color
	 * @param bgColor background color
	 * @return symbol icon
	 */
	public static ScaledIcon createTextSymbolIcon(String symbol, Font font,
			Dimension iconSize, Color fgColor, Color bgColor, double scale) {

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);

		if (bgColor != null) {
			g2.setBackground(bgColor);
		}
		g2.scale(scale, scale);
		g2.setColor(fgColor);
		g2.setFont(new Font(font.getFamily(), Font.PLAIN, h - 9));

		FontMetrics fm = g2.getFontMetrics();
		int symbolWidth = fm.stringWidth(symbol);
		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int msg_x = w / 2 - symbolWidth / 2;
		int msg_y = h / 2 - descent / 2 + ascent / 2;

		g2.drawString(symbol, msg_x, msg_y - 2);
		g2.fillRect(1, h - 5, w - 1, 3);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * Creates a 16x16 icon to represent a cell grid background color
	 * @return cell grid icon
	 */
	public static ScaledIcon createCellGridIcon(Color fgColor, Color bgColor, double scale) {
		int h = 16;
		int w = 16;

		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);
		g2.scale(scale, scale);
		// fill rectangle with bgColor (the selected color)
		g2.setPaint(bgColor);
		g2.fillRect(0, 3, 15, 9);

		// draw border around the colored rectangle
		g2.setColor(fgColor);
		g2.drawRect(0, 3, 15, 9);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param str content
	 * @param font0 font
	 * @param isBold bold?
	 * @param isItalic italic?
	 * @param isCentered aligned to center?
	 * @param iconSize size
	 * @param fgColor text color
	 * @param bgColor background color
	 * @return image of a text with a background
	 */
	public static ScaledIcon createStringIcon(String str, Font font0,
			boolean isBold, boolean isItalic, boolean isCentered,
			Dimension iconSize, Color fgColor, Color bgColor, double scale) {
		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = newBufferedImage(w, h, scale);

		Graphics2D g2 = createGraphics(image);
		g2.scale(scale, scale);
		if (bgColor != null) {
			g2.setBackground(bgColor);
		}

		g2.setColor(fgColor);
		// font = font.deriveFont((h-6)*1.0f);
		Font font = font0;
		if (isBold) {
			font = font.deriveFont(Font.BOLD);
		}
		if (isItalic) {
			font = font.deriveFont(Font.ITALIC);
		}
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();
		int symbolWidth = fm.stringWidth(str);
		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int x = isCentered ? w / 2 - symbolWidth / 2 : 1;
		int mid_y = h / 2 - descent / 2 + ascent / 2 - 1;

		g2.drawString(str, x, mid_y);
		g2.dispose();
		return new ScaledIcon(image, scale);
	}

	/**
	 * @param app application
	 * @param latex LaTeX string
	 * @param font font
	 * @param fgColor text color
	 * @return icon
	 */
	public static ScaledIcon createScaledLatexIcon(AppD app, String latex, Font font,
			Color fgColor) {
		Font font1 = font.deriveFont(
				(float) (font.getSize2D() * app.getImageManager().getPixelRatio()));
		app.getDrawEquation().checkFirstCall();
		Image baseIcon = (BufferedImage) TeXFormula.createBufferedImage(
				latex, TeXConstants.STYLE_DISPLAY, font1.getSize() + 3,
				DrawEquationD.toAwtColor(fgColor), DrawEquationD.toAwtColor(null));
		return new ScaledIcon(baseIcon, app.getImageManager().getPixelRatio());
	}

	/**
	 * @param pointStyle point style
	 * @param pointSize point size
	 * @param iconSize dimension
	 * @param fgColor point color
	 * @param bgColor background color
	 * @return styled point icon
	 */
	public static ScaledIcon createPointStyleIcon(int pointStyle, int pointSize,
			Dimension iconSize, Color fgColor, Color bgColor, double scale) {

		// TODO: PointStyleIcon as a stand alone class
		PointStyleImage image = new PointStyleImage(iconSize, pointStyle,
				pointSize, fgColor, bgColor, scale);

		return new ScaledIcon(image, scale);
	}

	public static class PointStyleImage extends BufferedImage {

		private final int pointStyle;

		// for drawing
		private final int pointSize;
		private final Ellipse2D.Double circle = new Ellipse2D.Double();
		private Line2D.Double line1;
		private Line2D.Double line2;
		private Line2D.Double line3;
		private Line2D.Double line4;
		private GeneralPath gp = null;
		private final BasicStroke borderStroke = AwtFactoryD
				.getAwtStroke(EuclidianStatic.getDefaultStroke());
		private final BasicStroke[] crossStrokes = new BasicStroke[10];
		private final int h;
		private final int w;

		protected PointStyleImage(Dimension d, int pointStyle, int pointSize,
				Color fgColor, Color bgColor, double scale) {
			super((int) (d.width * scale), (int) (d.height * scale), BufferedImage.TYPE_INT_ARGB);
			this.h = d.height;
			this.w = d.width;
			this.pointStyle = pointStyle;
			this.pointSize = pointSize;

			drawPointStyle(fgColor, bgColor, scale);
		}

		private void drawPointStyle(Color fgColor, Color bgColor, double scale) {
			Graphics2D g2 = GeoGebraIconD.createGraphics(this);
			g2.scale(scale, scale);

			// set background
			if (bgColor != null) {
				g2.setBackground(bgColor);
			}

			// draw point using routine from euclidian.DrawPoint
			g2.setPaint(fgColor);
			getPath();

			switch (pointStyle) {
			case EuclidianStyleConstants.POINT_STYLE_PLUS:
			case EuclidianStyleConstants.POINT_STYLE_CROSS:
				// draw cross like: X or +
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(line1);
				g2.draw(line2);
				break;

			case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
				// draw diamond
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(line1);
				g2.draw(line2);
				g2.draw(line3);
				g2.draw(line4);
				break;

			case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
				// draw diamond
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(gp);
				g2.fill(gp);
				break;

			case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
				// draw a circle
				g2.setStroke(crossStrokes[pointSize]);
				g2.draw(circle);
				break;

			// case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			default:
				// draw a dot
				g2.fill(circle);
				g2.setStroke(borderStroke);
				g2.draw(circle);
			}
			g2.dispose();
		}

		private void getPath() {
			// clear old path
			if (gp != null) {
				gp.reset();
			}

			// set point size
			// pointSize = 4;
			int diameter = 2 * pointSize;

			// set coords = center of cell
			double[] coords = new double[2];
			coords[0] = w / 2.0;
			coords[1] = h / 2.0;

			// get draw path using routine from euclidian.DrawPoint
			double xUL = coords[0] - pointSize;
			double yUL = coords[1] - pointSize;
			double root3over2 = Math.sqrt(3.0) / 2.0;

			switch (pointStyle) {
			default:
				// do nothing
				break;
			case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:

				double xR = coords[0] + pointSize;
				double yB = coords[1] + pointSize;

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) (xUL + xR) / 2, (float) yUL);
				gp.lineTo((float) xUL, (float) (yB + yUL) / 2);
				gp.lineTo((float) (xUL + xR) / 2, (float) yB);
				gp.lineTo((float) xR, (float) (yB + yUL) / 2);
				gp.closePath();

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

				double direction = 1.0;
				if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH) {
					direction = -1.0;
				}

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) coords[0],
						(float) (coords[1] + direction * pointSize));
				gp.lineTo((float) (coords[0] + pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) (coords[0] - pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) coords[0],
						(float) (coords[1] + direction * pointSize));
				gp.closePath();

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

				direction = 1.0;
				if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) {
					direction = -1.0;
				}

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) (coords[0] + direction * pointSize),
						(float) coords[1]);
				gp.lineTo((float) (coords[0] - direction * pointSize / 2),
						(float) (coords[1] + pointSize * root3over2));
				gp.lineTo((float) (coords[0] - direction * pointSize / 2),
						(float) (coords[1] - pointSize * root3over2));
				gp.lineTo((float) (coords[0] + direction * pointSize),
						(float) coords[1]);
				gp.closePath();

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				if (line3 == null) {
					line3 = new Line2D.Double();
					line4 = new Line2D.Double();
				}
				line1.setLine((xUL + xR) / 2, yUL, xUL, (yB + yUL) / 2);
				line2.setLine(xUL, (yB + yUL) / 2, (xUL + xR) / 2, yB);
				line3.setLine((xUL + xR) / 2, yB, xR, (yB + yUL) / 2);
				line4.setLine(xR, (yB + yUL) / 2, (xUL + xR) / 2, yUL);

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_PLUS:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				line1.setLine((xUL + xR) / 2, yUL, (xUL + xR) / 2, yB);
				line2.setLine(xUL, (yB + yUL) / 2, xR, (yB + yUL) / 2);

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_CROSS:
				xR = coords[0] + pointSize;
				yB = coords[1] + pointSize;

				if (line1 == null) {
					line1 = new Line2D.Double();
					line2 = new Line2D.Double();
				}
				line1.setLine(xUL, yUL, xR, yB);
				line2.setLine(xUL, yB, xR, yUL);

				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;

			case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
				if (crossStrokes[pointSize] == null) {
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				}
				break;
			}
			// for circle points
			circle.setFrame(xUL, yUL, diameter, diameter);
		}
	}

	/**
	 * Draws a LaTeX image in the given ImageIcon. Drawing is done twice. First
	 * draw gives the needed size of the image. Second draw renders the image
	 * with the correct dimensions.
	 * @return image bounds
	 */
	public static Rectangle measureLatexImage(AppD app,
			String latex, Font font, boolean serif, Color fgColor,
			Color bgColor) {
		// Create image with dummy size, then draw into it to get the correct
		// size
		GeoText geo = new GeoText(app.getKernel().getConstruction(), latex);
		geo.setSerifFont(serif);
		DrawText draw = new DrawText(app.getActiveEuclidianView(), geo);
		draw.drawMultilineLaTeX(
				app.getActiveEuclidianView()
						.getTempGraphics2D(new GFontD(font)),
				new GFontD(font), GColorD.newColor(fgColor),
				GColorD.newColor(bgColor));
		return GRectangleD.getAWTRectangle(draw.getBounds());
	}

	/**
	 * Creates a new icon by joining them together (leftIcon to rightIcon).
	 * @return joined icon
	 */
	public static ScaledIcon joinIcons(Icon leftIcon,
			ScaledIcon rightIcon) {
		double scale1 = leftIcon instanceof ScaledIcon scaledIcon ? scaledIcon.getScale() : 1.0;
		int w1 = leftIcon.getIconWidth();
		int w2 = rightIcon.getIconWidth();
		double scale2 = rightIcon.getScale();
		int h1 = leftIcon.getIconHeight();
		int h2 = rightIcon.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h / 2;
		double scale = Math.max(scale1, scale2);
		BufferedImage image = newBufferedImage(w1 + w2, h, scale);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		AffineTransform oldTransform = g2.getTransform();
		g2.translate(0, (mid - h1 / 2.0) * scale);
		g2.scale(scale / scale1, scale / scale1);
		g2.drawImage(extractImage(leftIcon), 0, 0, null);
		g2.setTransform(oldTransform);

		g2.translate(w1 * scale, (mid - h2 / 2.0) * scale);
		g2.scale(scale / scale2, scale / scale2);
		g2.drawImage(rightIcon.getImage(), 0, 0, null);
		g2.dispose();

		return new JoinedScaledIcon(image, scale, leftIcon);
	}

	private static BufferedImage newBufferedImage(int w, int h, double scale) {
		return new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
	}

	private static Image extractImage(Icon leftIcon) {
		return leftIcon instanceof ImageIcon imageIcon ? imageIcon.getImage()
				: leftIcon instanceof ScaledIcon scaledIcon ? scaledIcon.getImage()
				: createEmptyIcon(1, 1).getImage();
	}
}
