package geogebra.gui.util;

import geogebra.common.euclidian.DrawText;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.plugin.EuclidianStyleConstants;
import geogebra.euclidian.EuclidianStaticD;
import geogebra.main.AppD;
import geogebra.util.ImageManager;

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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

/**
 * Creates various ImageIcons for use in lists and tables.
 * 
 * @author G. Sturr
 * 
 */
public class GeoGebraIcon {

	public GeoGebraIcon() {
		super();
	}

	public static ImageIcon createEmptyIcon(int width, int height) {

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	public static ImageIcon createNullSymbolIcon(int width, int height) {

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(Color.GRAY);
		// draw a rectangle with an x inside
		g2.drawRect(3, 3, width - 6, height - 6);
		int k = 7;
		g2.drawLine(k, k, width - k, height - k);
		g2.drawLine(k, height - k, width - k, k);
		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	public static ImageIcon createFileImageIcon(AppD app, String fileName,
			float alpha, Dimension iconSize) {

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		image = ImageManager.toBufferedImage(app.getImageManager()
				.getImageResource(fileName));

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	public static ImageIcon createHGridIcon(Dimension iconSize) {

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// draw 3 horizontal lines
		g2.setPaint(Color.GRAY);

		for (int i = 1; i <= 3; i++)
			g2.drawLine(4, 4 * i, 12, 4 * i);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	public static ImageIcon createVGridIcon(Dimension iconSize) {

		int h = iconSize.height;
		int w = iconSize.width;
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// draw 3 horizontal lines
		g2.setPaint(Color.GRAY);

		for (int i = 1; i <= 3; i++)
			g2.drawLine(4 * i, 4, 4 * i, 12);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	/**
	 * Creates a 16x16 pixel icon representing a tree display of data
	 */
	public static ImageIcon createTreeIcon() {

		BufferedImage image = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setPaint(Color.DARK_GRAY);

		for (int i = 3; i < 12; i = i + 2) {
			g2.drawLine(6, i, 12, i);

			if (i % 3 == 0) {
				g2.fillRect(2, i - 1, 3, 3);
				g2.drawLine(2, i, 12, i);
			}
		}

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	/**
	 * Creates a 16x16 pixel icon to represent rightward opening/closing a list
	 * display of data
	 */
	public static ImageIcon listRightIcon() {

		BufferedImage image = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.DARK_GRAY);

		for (int i = 3; i <= 12; i = i + 2) {
			g2.drawLine(1, i, 6, i);
		}

		g2.setPaint(Color.GRAY);
		g2.drawLine(10, 5, 10, 9);
		g2.drawLine(9, 5, 9, 9);
		g2.drawLine(8, 5, 8, 9);

		for (int i = 0; i <= 4; i++) {
			g2.drawLine(11 + i, 3 + i, 11 + i, 11 - i);
		}

		ImageIcon ic = new ImageIcon(image);

		return ic;
	}

	/**
	 * Creates a 16x16 pixel icon to represent leftward opening/closing a list
	 * display of data
	 */
	public static ImageIcon listLeftIcon() {

		BufferedImage image = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.DARK_GRAY);

		for (int i = 3; i <= 12; i = i + 2) {
			g2.drawLine(10, i, 15, i);
		}

		g2.setPaint(Color.GRAY);
		g2.drawLine(8, 5, 8, 9);
		g2.drawLine(7, 5, 7, 9);
		g2.drawLine(6, 5, 6, 9);

		for (int i = 0; i <= 4; i++) {
			g2.drawLine(5 - i, 11 - i, 5 - i, 3 + i);
		}

		ImageIcon ic = new ImageIcon(image);

		return ic;
	}

	public static ImageIcon createDownTriangleIcon(int height) {
		int width = 8;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.BLACK);
		int x = 1;
		int y = height / 2 - 1;
		g2.drawLine(x, y, x + 6, y);
		g2.drawLine(x + 1, y + 1, x + 5, y + 1);
		g2.drawLine(x + 2, y + 2, x + 4, y + 2);
		g2.drawLine(x + 3, y + 3, x + 3, y + 3);

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	public static ImageIcon createDownTriangleIconRollOver(int height) {
		int width = 8;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillOval(0, 0, width, height);

		g2.setColor(Color.BLACK);
		int x = 1;
		int y = height / 2 - 1;
		g2.drawLine(x, y, x + 6, y);
		g2.drawLine(x + 1, y + 1, x + 5, y + 1);
		g2.drawLine(x + 2, y + 2, x + 4, y + 2);
		g2.drawLine(x + 3, y + 3, x + 3, y + 3);

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	public static ImageIcon createSymbolTableIcon(Font font, boolean isRollOver) {

		int s = 14;
		String alpha = "\u03B1";

		BufferedImage image = new BufferedImage(s, s,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.DARK_GRAY);

		font = font.deriveFont(Font.BOLD, s);
		g2.setFont(font);
		g2.setColor(Color.DARK_GRAY);
		drawCenteredText(g2, alpha, s / 2 - 1, s / 2);

		g2.setColor(Color.GRAY);
		g2.drawRect(0, 0, s - 2, s - 2);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawLine(s - 1, 1, s - 1, s - 1);
		g2.drawLine(1, s - 1, s - 1, s - 1);
		ImageIcon ic = new ImageIcon(image);
		return ic;
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
	 */
	public static ImageIcon createUpDownTriangleIcon(boolean isRollOver,
			boolean isEnabled) {

		int h = 18;
		int w = 12;
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (!isEnabled) {

			ImageIcon ic = new ImageIcon(image);
			return ic;
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

		if (isRollOver)
			g2.setColor(Color.BLACK);
		else
			g2.setColor(Color.DARK_GRAY);

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

		/*
		 * g2.drawLine(x, y, x+6, y); g2.drawLine(x+1, y+1, x+5, y+1);
		 * g2.drawLine(x+2, y+2, x+4, y+2); g2.drawLine(x+3, y+3, x+3, y+3);
		 */

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

	public static ImageIcon createColorSwatchIcon(float alpha,
			Dimension iconSize, Color fgColor, Color bgColor) {

		int h = iconSize.height;
		int w = iconSize.width;
		int offset = 2;
		float thickness = 3;

		// if fgColor is null then make it a transparent white
		if (fgColor == null)
			fgColor = new Color(255, 255, 255, 1);

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		--h;
		--w;

		if (bgColor != null) {
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// interior fill color using alpha level

		float[] rgb = new float[3];
		fgColor.getRGBColorComponents(rgb);
		g2.setPaint(new Color(rgb[0], rgb[1], rgb[2], alpha));
		g2.fillRect(offset, offset, w - 2 * offset, h - 2 * offset);

		// border color with alpha = 1
		g2.setPaint(fgColor);
		g2.setStroke(new BasicStroke(thickness));
		g2.drawRect(offset, offset, w - 2 * offset, h - 2 * offset);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	public static ImageIcon createLineStyleIcon(int dashStyle, int thickness,
			Dimension iconSize, Color fgColor, Color bgColor) {

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (bgColor != null) {
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, w, h);
		}

		// draw dashed line
		g2.setPaint(fgColor);
		g2.setStroke(geogebra.awt.GBasicStrokeD.getAwtStroke(EuclidianStatic
				.getStroke(thickness, dashStyle)));
		int mid = h / 2;
		g2.drawLine(4, mid, w - 4, mid);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	public static ImageIcon createTextSymbolIcon(String symbol, Font font,
			Dimension iconSize, Color fgColor, Color bgColor) {

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (bgColor != null)
			g2.setBackground(bgColor);

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

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	/**
	 * Creates a 16x16 icon to represent a cell grid background color
	 */
	public static ImageIcon createCellGridIcon(Color fgColor, Color bgColor) {

		int h = 16;
		int w = 16;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// fill rectangle with bgColor (the selected color)
		g2.setPaint(bgColor);
		g2.fillRect(0, 3, 15, 9);

		// draw border around the colored rectangle
		g2.setColor(fgColor);
		g2.drawRect(0, 3, 15, 9);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);
		return ic;
	}

	public static ImageIcon createStringIcon(String str, Font font,
			Dimension iconSize) {
		return createStringIcon(str, font, false, false, true, iconSize,
				Color.BLACK, null);
	}

	public static ImageIcon createStringIcon(String str, Font font,
			boolean isBold, boolean isItalic, boolean isCentered,
			Dimension iconSize, Color fgColor, Color bgColor) {

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor(fgColor);
		// font = font.deriveFont((h-6)*1.0f);
		if (isBold)
			font = font.deriveFont(Font.BOLD);
		if (isItalic)
			font = font.deriveFont(Font.ITALIC);
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();
		int symbolWidth = fm.stringWidth(str);
		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int x = (isCentered) ? w / 2 - symbolWidth / 2 : 1;
		int mid_y = h / 2 - descent / 2 + ascent / 2 - 1;

		g2.drawString(str, x, mid_y);

		return new ImageIcon(image);
	}

	public static ImageIcon createBracketIcon(String[] brackets, Font font,
			Dimension iconSize, Color fgColor, Color bgColor) {
		/*
		 * String latex = "\\left" + brackets[0] + "\\equiv" + "\\right" +
		 * brackets[1]; ImageIcon icon = createLatexIcon(app, latex, font,
		 * false, fgColor, null); icon = GeoGebraIcon.ensureIconSize(icon,
		 * iconSize);
		 */

		int h = iconSize.height;
		int w = iconSize.width;

		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (bgColor != null)
			g2.setBackground(bgColor);

		g2.setColor(fgColor);
		font = font.deriveFont((h - 4) * 1.0f);
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics();

		// int symbolWidth = fm.stringWidth (c);
		// int msg_x = w/2 - symbolWidth/2;

		int ascent = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int mid_y = h / 2 - descent / 2 + ascent / 2;

		int x = 2;
		g2.drawString(brackets[0] + "::" + brackets[1], x, mid_y);

		return new ImageIcon(image);
	}

	/**
	 * Draw a LaTeX image in the icon.
	 */
	public static ImageIcon createLatexIcon(AppD app, String latex, Font font,
			boolean serif, Color fgColor, Color bgColor) {
		return new ImageIcon(TeXFormula.createBufferedImage(latex,
				TeXConstants.STYLE_DISPLAY, font.getSize() + 3, fgColor,
				bgColor));
	}

	public static ImageIcon createLatexIcon(AppD app, String latex,
			boolean serif, Color fgColor, Color bgColor, int height) {
		ImageIcon ic = new ImageIcon(TeXFormula.createBufferedImage(latex,
				TeXConstants.STYLE_DISPLAY, height - 6, fgColor, bgColor));
		ensureIconSize(ic, new Dimension(ic.getIconWidth(), height));
		return ic;
	}

	public static ImageIcon createPointStyleIcon(int pointStyle, int pointSize,
			Dimension iconSize, Color fgColor, Color bgColor) {

		// TODO: PointStyleIcon as a stand alone class
		GeoGebraIcon g = new GeoGebraIcon();
		PointStyleImage image = g.new PointStyleImage(iconSize, pointStyle,
				pointSize, fgColor, bgColor);

		ImageIcon ic = new ImageIcon(image);
		// ensureIconSize(ic, iconSize);

		return ic;
	}

	public class PointStyleImage extends BufferedImage {

		private int pointStyle = -1;

		// for drawing
		private int pointSize = 4;
		private Ellipse2D.Double circle = new Ellipse2D.Double();
		private Line2D.Double line1, line2, line3, line4;
		private GeneralPath gp = null;
		private BasicStroke borderStroke = EuclidianStaticD
				.getDefaultStrokeAwt();
		private BasicStroke[] crossStrokes = new BasicStroke[10];
		private int h, w;

		public PointStyleImage(Dimension d, int pointStyle, int pointSize,
				Color fgColor, Color bgColor) {
			super(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			this.h = d.height;
			this.w = d.width;
			this.pointStyle = pointStyle;
			this.pointSize = pointSize;

			drawPointStyle(fgColor, bgColor);
		}

		public void drawPointStyle(Color fgColor, Color bgColor) {

			Graphics2D g2 = createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			// set background
			if (bgColor != null)
				g2.setBackground(bgColor);

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
				// drawWithValueStrokePure(gp, g2);
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
		}

		public void getPath() {
			// clear old path
			if (gp != null)
				gp.reset();

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

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:

				double direction = 1.0;
				if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH)
					direction = -1.0;

				if (gp == null) {
					gp = new GeneralPath();
				}
				gp.moveTo((float) coords[0], (float) (coords[1] + direction
						* pointSize));
				gp.lineTo((float) (coords[0] + pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) (coords[0] - pointSize * root3over2),
						(float) (coords[1] - direction * pointSize / 2));
				gp.lineTo((float) coords[0], (float) (coords[1] + direction
						* pointSize));
				gp.closePath();

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:

				direction = 1.0;
				if (pointStyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST)
					direction = -1.0;

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

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
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

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
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

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
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

				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;

			case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
				if (crossStrokes[pointSize] == null)
					crossStrokes[pointSize] = new BasicStroke(pointSize / 2f);
				break;
			}
			// for circle points
			circle.setFrame(xUL, yUL, diameter, diameter);
		}
	}

	public static ImageIcon ensureIconSize(ImageIcon icon, Dimension iconSize) {

		int h = iconSize.height;
		int w = iconSize.width;
		int h2 = icon.getIconHeight();
		int w2 = icon.getIconWidth();
		if (h2 == h && w2 == w)
			return icon;

		int wInset = (w - w2) > 0 ? (w - w2) / 2 : 0;
		int hInset = (h - h2) > 0 ? (h - h2) / 2 : 0;

		BufferedImage newImage = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		try {
			Image currentImage = icon.getImage();
			if (currentImage != null) {
				g2.drawImage(currentImage, wInset, hInset, null);
				icon.setImage(newImage);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return icon;
	}

	/**
	 * Draws a LaTeX image in the given ImageIcon. Drawing is done twice. First
	 * draw gives the needed size of the image. Second draw renders the image
	 * with the correct dimensions.
	 */
	public static final void drawLatexImageIcon(AppD app, ImageIcon latexIcon,
			String latex, Font font, boolean serif, Color fgColor, Color bgColor) {
		// Create image with dummy size, then draw into it to get the correct
		// size
		GeoText geo = new GeoText(app.getKernel().getConstruction(), latex);
		geo.setSerifFont(serif);
		DrawText draw = new DrawText(app.getActiveEuclidianView(), geo);
		draw.drawMultilineLaTeX(
				app.getActiveEuclidianView().getTempGraphics2D(
						new geogebra.awt.GFontD(font)),
				new geogebra.awt.GFontD(font),
				new geogebra.awt.GColorD(fgColor), new geogebra.awt.GColorD(
						bgColor));
		Rectangle d = geogebra.awt.GRectangleD
				.getAWTRectangle(draw.getBounds());

		// Now use this size and draw again to get the final image
		if (d.width == -1 || d.height == -1)
			return;
		BufferedImage image = new BufferedImage(d.width, d.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		draw.drawMultilineLaTeX(new geogebra.awt.GGraphics2DD(g2image),
				new geogebra.awt.GFontD(font),
				new geogebra.awt.GColorD(fgColor), new geogebra.awt.GColorD(
						bgColor));

		latexIcon.setImage(image);
	}

	/**
	 * Creates a new ImageIcon by joining them together (leftIcon to rightIcon).
	 */
	public static final ImageIcon joinIcons(ImageIcon leftIcon,
			ImageIcon rightIcon) {

		int w1 = leftIcon.getIconWidth();
		int w2 = rightIcon.getIconWidth();
		int h1 = leftIcon.getIconHeight();
		int h2 = rightIcon.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h / 2;
		BufferedImage image = new BufferedImage(w1 + w2, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(leftIcon.getImage(), 0, mid - h1 / 2, null);
		g2.drawImage(rightIcon.getImage(), w1, mid - h2 / 2, null);
		g2.dispose();

		ImageIcon ic = new ImageIcon(image);
		return ic;
	}

}
