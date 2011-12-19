package geogebra.euclidian;

import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;

public class EuclidianStatic {

	// This has to be made singleton or use prototype,
	// while its static methods be made non-static,
	// or implement by some other solution e.g. AbstractEuclidianStatic,
	// in order to be usable from Common. (like an adapter)

	// Michael Borcherds 2008-06-10
	final static float textWidth(String str, Font font, FontRenderContext frc)
	{
		if (str.equals("")) return 0f;
		TextLayout layout = new TextLayout(str , font, frc);
		return layout.getAdvance();

	}

	/**
	 * Draw a multiline LaTeX label.
	 *
	 * TODO: Improve performance (caching, etc.)
	 * Florian Sonner
	 * @param g2
	 * @param font
	 * @param fgColor
	 * @param bgColor
	 */
	public static final Rectangle drawMultilineLaTeX(Application app, Graphics2D tempGraphics, GeoElement geo, Graphics2D g2, Font font, Color fgColor, Color bgColor, String labelDesc, int xLabel, int yLabel, boolean serif) {
		int fontSize = g2.getFont().getSize();
		int lineSpread = (int)(fontSize * 1.0f);
		int lineSpace = (int)(fontSize * 0.5f);

		// latex delimiters \[ \] \( \) $$ -> $
		labelDesc = labelDesc.replaceAll("(\\$\\$|\\\\\\[|\\\\\\]|\\\\\\(|\\\\\\))", "\\$");

		// split on $ but not \$
		String[] elements = labelDesc.split("(?<![\\\\])(\\$)", -1);

		ArrayList<Integer> lineHeights = new ArrayList<Integer>();
		lineHeights.add(new Integer(lineSpread + lineSpace));
		ArrayList<Integer> elementHeights = new ArrayList<Integer>();

		int depth = 0;

		// use latex by default just if there is just a single element
		boolean isLaTeX = (elements.length == 1);

		// calculate the required space of every element
		for(int i = 0, currentLine = 0, currentElement = 0; i < elements.length; ++i) {
			if(isLaTeX) {
				// save the height of this element by drawing it to a temporary buffer
				Dimension dim = new Dimension();
				dim = app.getDrawEquation().drawEquation(app, geo, tempGraphics, 0, 0, elements[i], font, ((GeoText)geo).isSerifFont(), fgColor, bgColor, false);
				
				int height = dim.height;
				
				//depth += dim.depth;
				
				elementHeights.add(new Integer(height));

				// check if this element is taller than every else in the line
				if(height > (lineHeights.get(currentLine)).intValue())
					lineHeights.set(currentLine, new Integer(height));

				++currentElement;
			} else {
				elements[i] = elements[i].replaceAll("\\\\\\$", "\\$");
				String[] lines = elements[i].split("\\n", -1);

				for(int j = 0; j < lines.length; ++j) {
					elementHeights.add(new Integer(lineSpread));

					// create a new line
					if(j + 1 < lines.length) {
						++currentLine;

						lineHeights.add(new Integer(lineSpread + lineSpace));
					}

					++currentElement;
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
		for(int i = 0, currentLine = 0, currentElement = 0; i < elements.length; ++i) {
			if(isLaTeX) {
				// calculate the y offset of this element by: (lineHeight - elementHeight) / 2
				yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights.get(currentElement))).intValue()) / 2;
				
				// draw the equation and save the x offset
				xOffset += app.getDrawEquation().drawEquation(app, geo, g2, xLabel + xOffset, (yLabel + height) + yOffset, elements[i], font, ((GeoText)geo).isSerifFont(), fgColor, bgColor, true).width;

				++currentElement;
			} else {
				String[] lines = elements[i].split("\\n", -1);

				for(int j = 0; j < lines.length; ++j) {
					// calculate the y offset like done with the element
					yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights.get(currentElement))).intValue()) / 2;

					// draw the string
					g2.setFont(font); // JLaTeXMath changes g2's fontsize
					xOffset += drawIndexedString(app, g2, lines[j], xLabel + xOffset, yLabel + height + yOffset + lineSpread, serif).x;

					// add the height of this line if more lines follow
					if(j + 1 < lines.length) {
						height += ((lineHeights.get(currentLine))).intValue();

						if(xOffset > width)
							width = xOffset;
					}

					// create a new line if more will follow
					if(j + 1 < lines.length) {
						++currentLine;
						xOffset = 0;
					}

					++currentElement;
				}
			}

			// last element, increase total height and check if this is the most wide element
			if(i + 1 == elements.length) {
				height += ((lineHeights.get(currentLine))).intValue();

				if(xOffset > width)
					width = xOffset;
			}

			isLaTeX = !isLaTeX;
		}
		
		return new Rectangle(xLabel - 3, yLabel - 3 + depth, width + 6, height + 6);
		
		
	}

	/**
	 * Adds \\- to positions where the line can
	 * be broken. Now it only breaks at +, -, *
	 * and spaces.
	 *
	 * @param latex String
	 * @return The LaTeX string with breaks
	 */
	private static String addPossibleBreaks(String latex){
		StringBuilder latexTmp=new StringBuilder(latex);
		int depth=0;
		boolean no_addition=true;
		for (int i=0; i<latexTmp.length()-2;i++){
			char character=latexTmp.charAt(i);
			switch (character){
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
				if (latexTmp.charAt(i+1) != ';')
					break;
				i++;
				latexTmp.insert(i+1, "\\?");
				i=i+2;
				break;
			case ' ':
				if (latexTmp.charAt(i+1)!=' ')
					break;
				i++;
			case '*':
				if (depth != 0)
					break;
				latexTmp.insert(i+1, "\\?");
				i=i+2;
				break;
			case '+':
			case '-':
				if (depth != 0)
					break;
				latexTmp.insert(i+1, "\\-");
				i=i+2;
				no_addition=false;
			}
		}
		//no addition happened at depth zero so it can be broken
		//on * and space too.
		if (no_addition){
			return latexTmp.toString().replaceAll("\\?", "\\-");
		} else {
			return latexTmp.toString().replaceAll("\\?", "");
		}
	}


	public final static Rectangle drawMultiLineText(Application app, String labelDesc, int xLabel, int yLabel, Graphics2D g2, boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		Font font = g2.getFont();
		font = app.getFontCanDisplay(labelDesc, serif, font.getStyle(), font.getSize());

		FontRenderContext frc = g2.getFontRenderContext();
		int xoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		for (int i=0; i < length-1; i++) {
			if (labelDesc.charAt(i) == '\n') {
				//end of line reached: draw this line
				g2.drawString(labelDesc.substring(lineBegin, i), xLabel, yLabel + lines * lineSpread);

				int width=(int)textWidth(labelDesc.substring(lineBegin, i), font, frc);
				if (width > xoffset) xoffset = width;

				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;
		g2.drawString(labelDesc.substring(lineBegin), xLabel, ypos);

		int width=(int)textWidth(labelDesc.substring(lineBegin), font, frc);
		if (width > xoffset) xoffset = width;

		// Michael Borcherds 2008-06-10
		// changed setLocation to setBounds (bugfix)
		// and added final float textWidth()
		//labelRectangle.setLocation(xLabel, yLabel - fontSize);
		int height = (int) ( (lines +1)*lineSpread);

		return new Rectangle(xLabel-3, yLabel - fontSize -3, xoffset+6, height+6);
		//labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height );

	}
	
	
	public final static Rectangle drawMultiLineIndexedText(Application app, String labelDesc, int xLabel, int yLabel, Graphics2D g2, boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		int xoffset = 0, yoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		xoffset = 0;
		yoffset = 0;
		for (int i=0; i < length-1; i++) {
			if (labelDesc.charAt(i) == '\n') {
				//end of line reached: draw this line
				Point p = drawIndexedString(app, g2, labelDesc.substring(lineBegin, i), xLabel, yLabel + lines * lineSpread, serif);
				if (p.x > xoffset) xoffset = p.x;
				if (p.y > yoffset) yoffset = p.y;
				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;
		Point p = drawIndexedString(app, g2, labelDesc.substring(lineBegin), xLabel, ypos, serif);
		if (p.x > xoffset) xoffset = p.x;
		if (p.y > yoffset) yoffset = p.y;
		//labelHasIndex = yoffset > 0;
		int height = (int) ( (lines +1)*lineSpread);

		return new Rectangle(xLabel-3, yLabel - fontSize - 3, xoffset + 6, height + 6 );
		//labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height );

	}

	/**
	 * Draws a string str with possible indices to g2 at position x, y.
	 * The indices are drawn using the given indexFont.
	 * Examples for strings with indices: "a_1" or "s_{ab}"
	 * @param g2
	 * @param str
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	public static Point drawIndexedString(Application app, Graphics2D g2, String str, float xPos, float yPos, boolean serif) {
		Font g2font = g2.getFont();
		g2font = app.getFontCanDisplay(str, serif, g2font.getStyle(), g2font.getSize());
		Font indexFont = getIndexFont(g2font);
		Font font = g2font;
		TextLayout layout;
		FontRenderContext frc = g2.getFontRenderContext();

		int indexOffset = indexFont.getSize() / 2;
		float maxY = 0;
		int depth = 0;
		float x = xPos;
		float y = yPos;
		int startPos = 0;
		if (str == null) return null;
		int length = str.length();

		for (int i=0; i < length; i++) {
			switch (str.charAt(i)) {
				case '_':
					//	draw everything before _
					if (i > startPos) {
						font = (depth == 0) ? g2font : indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY) maxY = y;
						String tempStr = str.substring(startPos, i);
						layout = new TextLayout(tempStr, font, frc);
						g2.setFont(font);
						g2.drawString(tempStr, x, y);
						x += layout.getAdvance();
					}
					startPos = i + 1;
					depth++;

					// check if next character is a '{' (beginning of index with several chars)
					if (startPos < length && str.charAt(startPos) != '{') {
						font = (depth == 0) ? g2font : indexFont;
						y = yPos + depth * indexOffset;
						if (y > maxY) maxY = y;
						String tempStr = str.substring(startPos, startPos+1);
						layout = new TextLayout(tempStr, font, frc);
						g2.setFont(font);
						g2.drawString(tempStr, x, y);
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
							if (y > maxY) maxY = y;
							String tempStr = str.substring(startPos, i);
							layout = new TextLayout(tempStr, font, frc);
							g2.setFont(font);
							g2.drawString(tempStr, x, y);
							x += layout.getAdvance();
						}
						startPos = i+1;
						depth--;
					}
					break;
			}
		}

		if (startPos < length) {
			font = (depth == 0) ? g2font : indexFont;
			y = yPos + depth * indexOffset;
			if (y > maxY) maxY = y;
			String tempStr = str.substring(startPos);
			layout = new TextLayout(tempStr, font, frc);
			g2.setFont(font);
			g2.drawString(tempStr, x, y);
			x += layout.getAdvance();
		}
		g2.setFont(g2font);
		return new Point(Math.round(x - xPos), Math.round(maxY - yPos));
	}

	private static Font getIndexFont(Font f) {
		//	index font size should be at least 8pt
		int newSize = Math.max( (int) (f.getSize() * 0.9) , 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	final public static void drawWithValueStrokePure(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.draw(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);
	}

	final public static void fillWithValueStrokePure(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2.fill(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);
	}

	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}

	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * 
	 * @param width
	 * @param type
	 * @return stroke
	 */
	public static BasicStroke getStroke(float width, int type) {
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

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke
				.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

}
