package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;

/**
 * @author gabor@gegeobra.org
 *
 *
 *         Abstract class for EuclidianStatic
 */
public abstract class EuclidianStatic {
	/**
	 * need to clip just outside the viewing area when drawing eg vectors as a
	 * near-horizontal thick vector isn't drawn correctly otherwise
	 */
	public static final int CLIP_DISTANCE = 5;
	/**
	 * Prototype decides what implementation will be used for static methods
	 */
	public static EuclidianStatic prototype;
	/** standardstroke */
	protected static GBasicStroke standardStroke = org.geogebra.common.factories.AwtFactory.prototype
			.newMyBasicStroke(1.0f);
	/** stroke for selected geos */
	protected static GBasicStroke selStroke = org.geogebra.common.factories.AwtFactory.prototype
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
	public final static float textWidth(String str,
			org.geogebra.common.awt.GFont font,
			org.geogebra.common.awt.GFontRenderContext frc) {
		if (str.equals(""))
			return 0f;
		org.geogebra.common.awt.font.GTextLayout layout = org.geogebra.common.factories.AwtFactory.prototype
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
	public static org.geogebra.common.awt.GBasicStroke getStroke(float width,
			int type) {
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

		return org.geogebra.common.factories.AwtFactory.prototype.newBasicStroke(
				width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	/**
	 * Adds \\- to positions where the line can be broken. Now it only breaks at
	 * +, -, * and spaces.
	 * 
	 * @param latex
	 *            String
	 * @return The LaTeX string with breaks
	 */
	protected static String addPossibleBreaks(String latex) {
		StringBuilder latexTmp = new StringBuilder(latex);
		int depth = 0;
		boolean no_addition = true;
		for (int i = 0; i < latexTmp.length() - 2; i++) {
			char character = latexTmp.charAt(i);
			switch (character) {
			case '(':
			case '[':
			case '{':
				depth++;
				break;
			case ')':
			case ']':
			case '}':
				depth--;
				break;
			case '\\':
				if (latexTmp.charAt(i + 1) != ';')
					break;
				i++;
				latexTmp.insert(i + 1, "\\?");
				i = i + 2;
				break;
			case ' ':
				if (latexTmp.charAt(i + 1) != ' ')
					break;
				i++;
			case '*':
				if (depth != 0)
					break;
				latexTmp.insert(i + 1, "\\?");
				i = i + 2;
				break;
			case '+':
			case '-':
				if (depth != 0)
					break;
				latexTmp.insert(i + 1, "\\-");
				i = i + 2;
				no_addition = false;
			}
		}
		// no addition happened at depth zero so it can be broken
		// on * and space too.
		if (no_addition) {
			return latexTmp.toString().replaceAll("\\?", "\\-");
		}
		return latexTmp.toString().replaceAll("\\?", "");
	}

	/*
	 * public abstract float textWidth(String str, Font font, FontRenderContext
	 * frc);
	 */

	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * TODO: Improve performance (caching, etc.) Florian Sonner
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
	public static final org.geogebra.common.awt.GRectangle drawMultilineLaTeX(
			App app, org.geogebra.common.awt.GGraphics2D tempGraphics,
			GeoElement geo, org.geogebra.common.awt.GGraphics2D g2,
			org.geogebra.common.awt.GFont font, org.geogebra.common.awt.GColor fgColor,
			org.geogebra.common.awt.GColor bgColor, String labelDesc,
			int xLabel, int yLabel, boolean serif, Runnable callback) {
		return prototype.doDrawMultilineLaTeX(app, tempGraphics, geo, g2, font,
				fgColor, bgColor, labelDesc, xLabel, yLabel, serif, callback);
	}

	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * TODO: Improve performance (caching, etc.) Florian Sonner
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
	protected abstract org.geogebra.common.awt.GRectangle doDrawMultilineLaTeX(
			App app, org.geogebra.common.awt.GGraphics2D tempGraphics,
			GeoElement geo, org.geogebra.common.awt.GGraphics2D g2,
			org.geogebra.common.awt.GFont font, org.geogebra.common.awt.GColor fgColor,
			org.geogebra.common.awt.GColor bgColor, String labelDesc, int xLabel,
 int yLabel, boolean serif, Runnable callback);

	private static org.geogebra.common.awt.GFont getIndexFont(
			org.geogebra.common.awt.GFont f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	/**
	 * Draws a string str with possible indices to g2 at position x, y. The
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
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static org.geogebra.common.awt.GPoint drawIndexedString(App app,
			org.geogebra.common.awt.GGraphics2D g3, String str, float xPos,
			float yPos, boolean serif, boolean precise) {

		org.geogebra.common.awt.GFont g2font = g3.getFont();
		g2font = app.getFontCanDisplay(str, serif, g2font.getStyle(),
				g2font.getSize());
		org.geogebra.common.awt.GFont indexFont = getIndexFont(g2font);
		org.geogebra.common.awt.GFont font = g2font;
		// geogebra.common.awt.font.GTextLayout layout;
		org.geogebra.common.awt.GFontRenderContext frc = g3.getFontRenderContext();

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
					g3.setFont(font);
					g3.drawString(tempStr, x, y);
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
					g3.setFont(font);
					g3.drawString(tempStr, x, y);
					x += measureString(tempStr, font, frc);
					depth--;
				}
				i++;
				startPos++;
				break;

			case '}': // end of index with several characters
				if (depth > 0) {
					if (i > startPos) {
						font = (depth == 0) ? g2font : indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY)
							maxY = y;
						String tempStr = str.substring(startPos, i);
						g3.setFont(font);
						g3.drawString(tempStr, x, y);
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
			g3.setFont(font);
			g3.drawString(tempStr, x, y);
			x += measureString(tempStr, font, frc);
		}
		g3.setFont(g2font);
		return new org.geogebra.common.awt.GPoint(Math.round(x - xPos),
				Math.round(maxY - yPos));

	}

	private static double measureString(String tempStr, GFont font,
			GFontRenderContext frc) {
		if (frc != null)
			return AwtFactory.prototype.newTextLayout(tempStr, font, frc)
					.getAdvance();
		return StringUtil.prototype.estimateLength(tempStr, font);
	}

	/**
	 * This hack was needed for ticket #3265
	 */
	protected void doFillAfterImageLoaded(org.geogebra.common.awt.GShape shape,
			org.geogebra.common.awt.GGraphics2D g3,
			org.geogebra.common.awt.GBufferedImage gi, App app) {
	}

	/**
	 * This hack was needed for ticket #3265
	 */
	public static void fillAfterImageLoaded(org.geogebra.common.awt.GShape shape,
			org.geogebra.common.awt.GGraphics2D g3,
			org.geogebra.common.awt.GBufferedImage gi, App app) {
		prototype.doFillAfterImageLoaded(shape, g3, gi, app);
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
	public final static org.geogebra.common.awt.GRectangle drawMultiLineText(
			App app, String labelDesc, int xLabel, int yLabel,
			org.geogebra.common.awt.GGraphics2D g2, boolean serif, GFont textFont) {

		int lines = 0;
		int fontSize = textFont.getSize();
		float lineSpread = fontSize * 1.5f;

		org.geogebra.common.awt.GFont font = app.getFontCanDisplay(labelDesc,
				serif, textFont.getStyle(), fontSize);

		org.geogebra.common.awt.GFontRenderContext frc = g2.getFontRenderContext();
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

		return AwtFactory.prototype.newRectangle(xLabel - 3, yLabel - fontSize
				- 3, xoffset + 6, height + 6);
	}

}
