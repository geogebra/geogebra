package geogebra.common.euclidian;

import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.EuclidianStyleConstants;


/**
 * @author gabor@gegeobra.org
 *
 *
 *Abstract class for EuclidianStatic
 */
public abstract class EuclidianStatic {
	/** need to clip just outside the viewing area when drawing eg vectors
 	as a near-horizontal thick vector isn't drawn correctly otherwise*/
		public static final int CLIP_DISTANCE = 5;
		/**
		 * Prototype decides what implementation will be used for static methods
		 */
	public static EuclidianStatic prototype;
	/** standardstroke*/
	protected static GBasicStroke standardStroke = 
			geogebra.common.factories.AwtFactory.prototype.newMyBasicStroke(1.0f);
	/** stroke for selected geos*/
	protected static GBasicStroke selStroke = 
			geogebra.common.factories.AwtFactory.prototype.newMyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

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
	 * @param str string
	 * @param font font
	 * @param frc rendering context
	 * @return text width
	 */
	public final static float textWidth(String str, geogebra.common.awt.GFont font, 
			geogebra.common.awt.GFontRenderContext frc) {
		if (str.equals(""))
			return 0f;
		geogebra.common.awt.font.GTextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(str, font, frc);
		return layout.getAdvance();

	}
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * 
	 * @param width stroke width
	 * @param type stroke type (EuclidianStyleConstants.LINE_TYPE_*)
	 * @return stroke
	 */
	public static geogebra.common.awt.GBasicStroke getStroke(float width, int type) {
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

		return geogebra.common.factories.AwtFactory.prototype.newBasicStroke(width, endCap, standardStroke.getLineJoin(),
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
	public abstract float textWidth(String str, Font font, FontRenderContext frc);
	*/
	
	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * TODO: Improve performance (caching, etc.) Florian Sonner
	 * @param app application
	 * @param tempGraphics temporary graphics
	 * @param geo geo
	 * 
	 * @param g2 graphics
	 * @param font font
	 * @param fgColor color
	 * @param bgColor background color
	 * @param labelDesc LaTeX text
	 * @param xLabel x-coord
	 * @param yLabel y-coord
	 * @param serif true touseserif font
	 * @return bounds of resulting LaTeX formula
	 */
	public static final geogebra.common.awt.GRectangle drawMultilineLaTeX(App app,
			geogebra.common.awt.GGraphics2D tempGraphics, GeoElement geo, geogebra.common.awt.GGraphics2D g2, geogebra.common.awt.GFont font,
			geogebra.common.awt.GColor fgColor, geogebra.common.awt.GColor bgColor, String labelDesc, int xLabel,
			int yLabel, boolean serif) {
		return prototype.doDrawMultilineLaTeX(app, tempGraphics, geo, g2, font, fgColor, bgColor, labelDesc, xLabel, yLabel, serif);
	}
	/**
	 * Draw a multiline LaTeX label.
	 * 
	 * TODO: Improve performance (caching, etc.) Florian Sonner
	 * @param app application
	 * @param tempGraphics temporary graphics
	 * @param geo geo
	 * 
	 * @param g2 graphics
	 * @param font font
	 * @param fgColor color
	 * @param bgColor background color
	 * @param labelDesc LaTeX text
	 * @param xLabel x-coord
	 * @param yLabel y-coord
	 * @param serif true touseserif font
	 * @return bounds of resulting LaTeX formula
	 */
	protected abstract geogebra.common.awt.GRectangle doDrawMultilineLaTeX(App app,
			geogebra.common.awt.GGraphics2D tempGraphics, GeoElement geo, geogebra.common.awt.GGraphics2D g2, geogebra.common.awt.GFont font,
			geogebra.common.awt.GColor fgColor, geogebra.common.awt.GColor bgColor, String labelDesc, int xLabel,
			int yLabel, boolean serif);
	
	private static geogebra.common.awt.GFont getIndexFont(geogebra.common.awt.GFont f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}
	
	/**
	 * Draws a string str with possible indices to g2 at position x, y. The
	 * indices are drawn using the given indexFont. Examples for strings with
	 * indices: "a_1" or "s_{ab}"
	 * @param app application
	 * @param g3 graphics
	 * 
	 * @param str input string
	 * @param xPos x-coord
	 * @param yPos y-coord
	 * @param serif true to use serif font
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static geogebra.common.awt.GPoint drawIndexedString(App app, geogebra.common.awt.GGraphics2D g3,
			String str, float xPos, float yPos, boolean serif) {

		geogebra.common.awt.GFont g2font = g3.getFont();
		g2font = app.getFontCanDisplay(str, serif, g2font.getStyle(),
				g2font.getSize());
		geogebra.common.awt.GFont indexFont = getIndexFont(g2font);
		geogebra.common.awt.GFont font = g2font;
		geogebra.common.awt.font.GTextLayout layout;
		geogebra.common.awt.GFontRenderContext frc = g3.getFontRenderContext();

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
					layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(tempStr, font, frc);
					g3.setFont(font);
					g3.drawString(tempStr, x, y);
					x += layout.getAdvance();
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
					layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(tempStr, font, frc);
					g3.setFont(font);
					g3.drawString(tempStr, x, y);
					x += layout.getAdvance();
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
						layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(tempStr, font, frc);
						g3.setFont(font);
						g3.drawString(tempStr, x, y);
						x += layout.getAdvance();
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
			layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(tempStr, font, frc);
			g3.setFont(font);
			g3.drawString(tempStr, x, y);
			x += layout.getAdvance();
		}
		g3.setFont(g2font);
		return new geogebra.common.awt.GPoint(Math.round(x - xPos), Math.round(maxY - yPos));

	}
	
	
	/**
	 * @param shape shape tobe filled
	 * @param g3 graphics
	 */
	protected abstract  void doFillWithValueStrokePure(geogebra.common.awt.GShape shape, geogebra.common.awt.GGraphics2D g3);
	/**
	 * @param shape shape to be filled
	 * @param g3 graphics
	 */
	public static void fillWithValueStrokePure(geogebra.common.awt.GShape shape, geogebra.common.awt.GGraphics2D g3){
		prototype.doFillWithValueStrokePure(shape, g3);
		
	}

	/**
	 * This hack was needed for ticket #3265
	 */
	protected void doFillAfterImageLoaded(geogebra.common.awt.GShape shape, geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBufferedImage gi, App app)
	{ }
	/**
	 * This hack was needed for ticket #3265
	 */
	public static void fillAfterImageLoaded(geogebra.common.awt.GShape shape, geogebra.common.awt.GGraphics2D g3, geogebra.common.awt.GBufferedImage gi, App app) {
		prototype.doFillAfterImageLoaded(shape, g3, gi, app);
	}
	/**
	 * @param app application
	 * @param labelDesc text
	 * @param xLabel x-coord
	 * @param yLabel y-coord
	 * @param g2 graphics
	 * @param serif true for serif font
	 * @return border of resulting text drawing
	 */
	public final static geogebra.common.awt.GRectangle drawMultiLineText(App app,
			String labelDesc, int xLabel, int yLabel, geogebra.common.awt.GGraphics2D g2,
			boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		geogebra.common.awt.GFont font = g2.getFont();
		font = app.getFontCanDisplay(labelDesc, serif, font.getStyle(),
				font.getSize());

		geogebra.common.awt.GFontRenderContext frc = g2.getFontRenderContext();
		int xoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		for (int i = 0; i < length - 1; i++) {
			if (labelDesc.charAt(i) == '\n') {
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
		g2.drawString(labelDesc.substring(lineBegin), xLabel, ypos);

		int width = (int) textWidth(labelDesc.substring(lineBegin), font, frc);
		if (width > xoffset)
			xoffset = width;

		// Michael Borcherds 2008-06-10
		// changed setLocation to setBounds (bugfix)
		// and added final float textWidth()
		// labelRectangle.setLocation(xLabel, yLabel - fontSize);
		int height = (int) ((lines + 1) * lineSpread);

		return AwtFactory.prototype.newRectangle(xLabel - 3, yLabel - fontSize - 3, xoffset + 6,
				height + 6);
	}

	/**
	 * @param shape shape to be drawn
	 * @param g2 graphics
	 */
	public static void drawWithValueStrokePure(geogebra.common.awt.GShape shape, GGraphics2D g2) {
		prototype.doDrawWithValueStrokePure(shape, g2);
		
	}
	/**
	 * @param shape shapeto be drawn
	 * @param g2 graphics
	 */
	protected abstract void doDrawWithValueStrokePure(geogebra.common.awt.GShape shape, GGraphics2D g2);
	/**
	 * @param g3 graphics
	 * @param needsInterpolation true to turn interpolation on
	 * @return hint
	 */
	public static Object setInterpolationHint(
			geogebra.common.awt.GGraphics2D g3,
			boolean needsInterpolation) {
		
		return prototype.doSetInterpolationHint(g3,needsInterpolation);
	}
	/**
	 * @param g3 graphics
	 * @param hint old hint value
	 */
	public static void resetInterpolationHint(
			geogebra.common.awt.GGraphics2D g3,
			Object hint) {
		
		prototype.doResetInterpolationHint(g3,hint);
	}
	/**
	 * @param g3 graphics
	 * @param needsInterpolation true to turn interpolation on
	 * @return hint
	 */
	protected abstract Object doSetInterpolationHint(GGraphics2D g3,
			boolean needsInterpolation);
	/**
	 * @param g3 graphics
	 * @param hint old hint value
	 */
	protected abstract void doResetInterpolationHint(
			geogebra.common.awt.GGraphics2D g3,
			Object hint);
		
}
