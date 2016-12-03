package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;

/**
 * @author gabor@gegeobra.org
 *
 *
 *         Abstract class for EuclidianStatic
 */
public class EuclidianStatic {
	/**
	 * need to clip just outside the viewing area when drawing eg vectors as a
	 * near-horizontal thick vector isn't drawn correctly otherwise
	 */
	public static final int CLIP_DISTANCE = 5;

	/** standardstroke */
	protected static final GBasicStroke standardStroke = AwtFactory.getPrototype()
			.newMyBasicStroke(1.0f);
	/** stroke for selected geos */
	protected static final GBasicStroke selStroke = AwtFactory.getPrototype()
			.newMyBasicStroke(1.0f + EuclidianStyleConstants.SELECTION_ADD);

	/**
	 * @return default stroke
	 */
	static public GBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	/**
	 * @return stroke for selected geos
	 */
	static public GBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}

	// Michael Borcherds 2008-06-10
	/**
	 * @param str
	 *            string
	 * @param font
	 *            font
	 * @param frc
	 *            rendering context
	 * @return text width
	 */
	public final static float textWidth(String str, GFont font,
			GFontRenderContext frc) {
		if (str.equals(""))
			return 0f;
		GTextLayout layout = AwtFactory.getPrototype()
				.newTextLayout(str, font, frc);
		return layout.getAdvance();

	}

	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * 
	 * @param width
	 *            stroke width
	 * @param type
	 *            stroke type (EuclidianStyleConstants.LINE_TYPE_*)
	 * @return stroke
	 */
	public static GBasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianStyleConstants.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? GBasicStroke.CAP_BUTT : standardStroke
				.getEndCap();

		return AwtFactory.getPrototype().newBasicStroke(
				width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	/*
	 * public abstract float textWidth(String str, Font font, FontRenderContext
	 * frc);
	 */


	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * @param app
	 *            application
	 * @param tempGraphics
	 *            temporary graphics
	 * @param geo
	 *            geo
	 * 
	 * @param g2
	 *            graphics
	 * @param font
	 *            font
	 * @param fgColor
	 *            color
	 * @param bgColor
	 *            background color
	 * @param labelDesc
	 *            LaTeX text
	 * @param xLabel
	 *            x-coord
	 * @param yLabel
	 *            y-coord
	 * @param serif
	 *            true touseserif font
	 * @return bounds of resulting LaTeX formula
	 */
	public static final GRectangle drawMultilineLaTeX(App app,
			GGraphics2D tempGraphics, GeoElementND geo, GGraphics2D g2,
			GFont font, GColor fgColor, GColor bgColor, String labelDesc,
			int xLabel, int yLabel, boolean serif, Runnable callback) {
		int fontSize = g2.getFont().getSize();
		int lineSpread = (int) (fontSize * 1.0f);
		int lineSpace = (int) (fontSize * 0.5f);

		// latex delimiters \[ \] \( \) $$ -> $
		// labelDesc = labelDesc.replaceAll(
		// "(\\$\\$|\\\\\\[|\\\\\\]|\\\\\\(|\\\\\\))", "\\$");

		// split on $ but not \$
		String[] elements = blockSplit(labelDesc);

		ArrayList<Integer> lineHeights = new ArrayList<Integer>();
		lineHeights.add(lineSpread + lineSpace);
		ArrayList<Integer> elementHeights = new ArrayList<Integer>();

		int depth = 0;

		// use latex by default just if there is just a single element
		boolean isLaTeX = (elements.length == 1);


		// calculate the required space of every element
		for (int i = 0, currentLine = 0; i < elements.length; ++i) {

			if (isLaTeX) {
				// save the height of this element by drawing it to a temporary
				// buffer
				GDimension dim = AwtFactory.getPrototype().newDimension(0,0);
				dim = app.getDrawEquation().drawEquation(app, geo,
						tempGraphics, 0, 0, elements[i], font,
						((GeoText) geo).isSerifFont(), fgColor, bgColor, false,
						false, callback);

				int height = dim.getHeight();

				// depth += dim.depth;

				elementHeights.add(Integer.valueOf(height));

				// check if this element is taller than every else in the line
				if (height > (lineHeights.get(currentLine)).intValue()) {
					lineHeights.set(currentLine, Integer.valueOf(height));
				}
			} else {
				elements[i] = elements[i].replaceAll("\\\\\\$", "\\$");
				String[] lines = elements[i].split("\\n", -1);

				for (int j = 0; j < lines.length; ++j) {
					elementHeights.add(new Integer(lineSpread));

					// create a new line
					if (j + 1 < lines.length) {
						++currentLine;

						lineHeights.add(new Integer(lineSpread + lineSpace));
					}
				}
			}

			isLaTeX = !isLaTeX;
		}

		int width = 0;
		int height = 0;

		// use latex by default just if there is just a single element
		isLaTeX = (elements.length == 1);

		int xOffset = 0;
		int yOffset = 0;

		// now draw all elements
		for (int i = 0, currentLine = 0, currentElement = 0; i < elements.length; ++i) {
			if (elements[i] == null) {
				continue;
			}
			if (isLaTeX) {
				// calculate the y offset of this element by: (lineHeight -
				// elementHeight) / 2
				yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights
						.get(currentElement))).intValue()) / 2;

				DrawEquation de = app.getDrawEquation();
				// draw the equation and save the x offset
				xOffset += de.drawEquation(app, geo, g2, xLabel + xOffset,
						(yLabel + height) + yOffset, elements[i], font,
						((GeoText) geo).isSerifFont(), fgColor, bgColor, true,
						false, callback).getWidth();

				++currentElement;
			} else {
				String[] lines = elements[i].split("\\n", -1);

				for (int j = 0; j < lines.length; ++j) {
					// calculate the y offset like done with the element
					yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights
							.get(currentElement))).intValue()) / 2;

					// draw the string
					g2.setFont(font); // JLaTeXMath changes g2's fontsize
					xOffset += drawIndexedString(app, g2, lines[j], xLabel
							+ xOffset, yLabel + height + yOffset + lineSpread,
							serif, true).x;

					// add the height of this line if more lines follow
					if (j + 1 < lines.length) {
						height += ((lineHeights.get(currentLine))).intValue();

						if (xOffset > width)
							width = xOffset;
					}

					// create a new line if more will follow
					if (j + 1 < lines.length) {
						++currentLine;
						xOffset = 0;
					}

					++currentElement;
				}
			}

			// last element, increase total height and check if this is the most
			// wide element
			if (i + 1 == elements.length) {
				height += ((lineHeights.get(currentLine))).intValue();

				if (xOffset > width)
					width = xOffset;
			}

			isLaTeX = !isLaTeX;
		}

		return AwtFactory.getPrototype().newRectangle(xLabel - 3,
				yLabel - 3 + depth, width + 6, height + 6);

	}

	/**
	 * eg FormulaText["\text{Price (\$)}"]
	 * 
	 * @param str
	 *            String to split
	 * @return str split on $ but not \$
	 */
	private static String[] blockSplit(String str) {
		
		// http://stackoverflow.com/questions/2709839/how-do-i-express-but-not-preceded-by-in-a-java-regular-expression
		// negative lookbehind
		// return str.split("(?<!\\\\)$");

		// JavaScript GWT compatible version
		// reverse string and use a lookahead
		// http://stackoverflow.com/questions/641407/javascript-negative-lookbehind-equivalent
		String reverse = new StringBuilder(str).reverse().toString();
		String[] split = reverse.split("\\$(?!([\\\\]))");

		// special case: need an extra "" at the start
		if (str.startsWith("$")) {
			String[] normal = new String[split.length + 1];

			normal[0] = "";

			for (int i = 0; i < split.length; i++) {
				normal[split.length - i] = new StringBuilder(split[i]).reverse()
						.toString();
			}

			return normal;

		}

		String[] normal = new String[split.length];

		for (int i = 0; i < split.length; i++) {
			normal[split.length - i - 1] = new StringBuilder(split[i]).reverse()
					.toString();
		}

		return normal;
		
	}

	private static GFont getIndexFont(GFont f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	/**
	 * Always draws a string str with possible indices to g2 at position x, y.
	 * The indices are drawn using the given indexFont. Examples for strings
	 * with indices: "a_1" or "s_{ab}"
	 * 
	 * @param app
	 *            application
	 * @param g3
	 *            graphics
	 * 
	 * @param str
	 *            input string
	 * @param xPos
	 *            x-coord
	 * @param yPos
	 *            y-coord
	 * @param serif
	 *            true to use serif font
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static GPoint drawIndexedString(App app, GGraphics2D g3, String str,
			float xPos,
			float yPos, boolean serif, boolean precise) {

		return drawIndexedString(app, g3, str, xPos, yPos, serif, precise, true);
	}

	/**
	 * Draws or just measures the string str with possible indices to g2. The
	 * indices are drawn using the given indexFont. Examples for strings with
	 * indices: "a_1" or "s_{ab}"
	 * 
	 * @param app
	 *            application
	 * @param g3
	 *            graphics
	 * 
	 * @param str
	 *            input string
	 * @param xPos
	 *            x-coord
	 * @param yPos
	 *            y-coord
	 * @param serif
	 *            true to use serif font
	 * @param doDraw
	 *            true to draw, false to measure only.
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static GPoint drawIndexedString(App app, GGraphics2D g3, String str,
			float xPos, float yPos, boolean serif, boolean precise,
			boolean doDraw) {

		GFont g2font = g3.getFont();
		g2font = app.getFontCanDisplay(str, serif, g2font.getStyle(),
				g2font.getSize());
		GFont indexFont = getIndexFont(g2font);
		GFont font = g2font;
		// GTextLayout layout;
		GFontRenderContext frc = g3
				.getFontRenderContext();

		int indexOffset = indexFont.getSize() / 2;
		float maxY = 0;
		int depth = 0;
		float x = xPos;
		float y = yPos;
		int startPos = 0;
		if (str == null)
			return null;
		int length = str.length();

		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
			case '_':
				// draw everything before _
				if (i > startPos) {
					font = (depth == 0) ? g2font : indexFont;
					y = yPos + depth * indexOffset;
					if (y > maxY)
						maxY = y;
					String tempStr = str.substring(startPos, i);

					if (doDraw) {
						g3.setFont(font);
						g3.drawString(tempStr, x, y);
					}

					x += measureString(tempStr, font, frc);
				}
				startPos = i + 1;
				depth++;

				// check if next character is a '{' (beginning of index with
				// several chars)
				if (startPos < length && str.charAt(startPos) != '{') {
					font = (depth == 0) ? g2font : indexFont;
					y = yPos + depth * indexOffset;
					if (y > maxY)
						maxY = y;
					String tempStr = str.substring(startPos, startPos + 1);
					if (doDraw) {
						g3.setFont(font);
						g3.drawString(tempStr, x, y);
					}
					x += measureString(tempStr, font, frc);
					depth--;
				}
				i++;
				startPos++;
				break;

			case '}': // end of index with several characters
				if (depth > 0) {
					if (i > startPos) {
						font = indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY)
							maxY = y;
						String tempStr = str.substring(startPos, i);
						if (doDraw) {
							g3.setFont(font);
							g3.drawString(tempStr, x, y);
						}
						x += measureString(tempStr, font, frc);
					}
					startPos = i + 1;
					depth--;
				}
				break;
			}
		}

		if (startPos < length) {
			font = (depth == 0) ? g2font : indexFont;
			y = yPos + depth * indexOffset;
			if (y > maxY)
				maxY = y;
			String tempStr = str.substring(startPos);
			if (doDraw) {
				g3.setFont(font);
				g3.drawString(tempStr, x, y);
			}
			x += measureString(tempStr, font, frc);
		}

		if (doDraw) {
			g3.setFont(g2font);
		}
		return new GPoint(Math.round(x - xPos),
				Math.round(maxY - yPos));

	}
	private static double measureString(String tempStr, GFont font,
			GFontRenderContext frc) {
		if (frc != null)
			return AwtFactory.getPrototype().newTextLayout(tempStr, font, frc)
					.getAdvance();
		return StringUtil.getPrototype().estimateLength(tempStr, font);
	}

	/**
	 * @param app
	 *            application
	 * @param labelDesc
	 *            text
	 * @param xLabel
	 *            x-coord
	 * @param yLabel
	 *            y-coord
	 * @param g2
	 *            graphics
	 * @param serif
	 *            true for serif font
	 * @param textFont
	 * @return border of resulting text drawing
	 */
	public final static GRectangle drawMultiLineText(App app, String labelDesc,
			int xLabel, int yLabel, GGraphics2D g2, boolean serif,
			GFont textFont) {

		int lines = 0;
		int fontSize = textFont.getSize();
		float lineSpread = fontSize * 1.5f;

		GFont font = app.getFontCanDisplay(labelDesc, serif,
				textFont.getStyle(), fontSize);

		GFontRenderContext frc = g2.getFontRenderContext();
		int xoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		for (int i = 0; i < length - 1; i++) {
			if (labelDesc.charAt(i) == '\n') {

				// iOS (bug?) - bold text needs font setting for each line
				g2.setFont(font);

				// end of line reached: draw this line
				g2.drawString(labelDesc.substring(lineBegin, i), xLabel, yLabel
						+ lines * lineSpread);

				int width = (int) textWidth(labelDesc.substring(lineBegin, i),
						font, frc);
				if (width > xoffset)
					xoffset = width;

				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;

		// iOS (bug?) - bold text needs font setting for each line
		g2.setFont(font);

		g2.drawString(labelDesc.substring(lineBegin), xLabel, ypos);

		int width = (int) textWidth(labelDesc.substring(lineBegin), font, frc);
		if (width > xoffset)
			xoffset = width;

		// Michael Borcherds 2008-06-10
		// changed setLocation to setBounds (bugfix)
		// and added final float textWidth()
		// labelRectangle.setLocation(xLabel, yLabel - fontSize);
		int height = (int) ((lines + 1) * lineSpread);

		return AwtFactory.getPrototype().newRectangle(xLabel - 3, yLabel - fontSize
				- 3, xoffset + 6, height + 6);
	}

}
