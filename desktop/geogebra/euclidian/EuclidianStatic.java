package geogebra.euclidian;

import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.AbstractApplication;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;

public class EuclidianStatic extends geogebra.common.euclidian.EuclidianStatic{

	// This has to be made singleton or use prototype,
	// while its static methods be made non-static,
	// or implement by some other solution e.g. AbstractEuclidianStatic,
	// in order to be usable from Common. (like an adapter)

	// Michael Borcherds 2008-06-10
	final static float textWidth(String str, geogebra.common.awt.Font font, 
			geogebra.common.awt.FontRenderContext frc) {
		if (str.equals(""))
			return 0f;
		geogebra.common.awt.font.TextLayout layout = geogebra.common.factories.AwtFactory.prototype.newTextLayout(str, font, frc);
		return layout.getAdvance();

	}

	@Override
	public final geogebra.common.awt.Rectangle doDrawMultilineLaTeX(AbstractApplication app,
			geogebra.common.awt.Graphics2D tempGraphics, GeoElement geo, geogebra.common.awt.Graphics2D g2, geogebra.common.awt.Font font,
			geogebra.common.awt.Color fgColor, geogebra.common.awt.Color bgColor, String labelDesc, int xLabel,
			int yLabel, boolean serif) {
		int fontSize = g2.getFont().getSize();
		int lineSpread = (int) (fontSize * 1.0f);
		int lineSpace = (int) (fontSize * 0.5f);

		// latex delimiters \[ \] \( \) $$ -> $
		labelDesc = labelDesc.replaceAll(
				"(\\$\\$|\\\\\\[|\\\\\\]|\\\\\\(|\\\\\\))", "\\$");

		// split on $ but not \$
		String[] elements = labelDesc.split("(?<![\\\\])(\\$)", -1);

		ArrayList<Integer> lineHeights = new ArrayList<Integer>();
		lineHeights.add(new Integer(lineSpread + lineSpace));
		ArrayList<Integer> elementHeights = new ArrayList<Integer>();

		int depth = 0;

		// use latex by default just if there is just a single element
		boolean isLaTeX = (elements.length == 1);

		// calculate the required space of every element
		for (int i = 0, currentLine = 0, currentElement = 0; i < elements.length; ++i) {
			if (isLaTeX) {
				// save the height of this element by drawing it to a temporary
				// buffer
				geogebra.common.awt.Dimension dim = new geogebra.awt.Dimension();
				dim = app.getDrawEquation().
						drawEquation(app, geo, tempGraphics, 0, 0,
						elements[i], font, ((GeoText) geo).isSerifFont(),
						fgColor, bgColor, false);

				int height = (int) dim.getHeight();

				// depth += dim.depth;

				elementHeights.add(new Integer(height));

				// check if this element is taller than every else in the line
				if (height > (lineHeights.get(currentLine)).intValue())
					lineHeights.set(currentLine, new Integer(height));

				++currentElement;
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
		for (int i = 0, currentLine = 0, currentElement = 0; i < elements.length; ++i) {
			if (isLaTeX) {
				// calculate the y offset of this element by: (lineHeight -
				// elementHeight) / 2
				yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights
						.get(currentElement))).intValue()) / 2;

				DrawEquationInterface de = app.getDrawEquation();
				// draw the equation and save the x offset
				xOffset += de.drawEquation(app, geo, g2, xLabel
						+ xOffset, (yLabel + height) + yOffset, elements[i],
						font, ((GeoText) geo).isSerifFont(), fgColor, bgColor,
						true).getWidth();

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
							serif).x;

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

		return AwtFactory.prototype.newRectangle(xLabel - 3, yLabel - 3 + depth, width + 6,
				height + 6);

	}

	/**
	 * Adds \\- to positions where the line can be broken. Now it only breaks at
	 * +, -, * and spaces.
	 * 
	 * @param latex
	 *            String
	 * @return The LaTeX string with breaks
	 */
	private static String addPossibleBreaks(String latex) {
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

	public final geogebra.common.awt.Rectangle doDrawMultiLineText(AbstractApplication app,
			String labelDesc, int xLabel, int yLabel, geogebra.common.awt.Graphics2D g2,
			boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		geogebra.common.awt.Font font = g2.getFont();
		font = ((Application) app).getFontCanDisplay(labelDesc, serif, font.getStyle(),
				font.getSize());

		geogebra.common.awt.FontRenderContext frc = g2.getFontRenderContext();
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
		// labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height
		// );

	}

	public final static geogebra.common.awt.Rectangle drawMultiLineIndexedText(Application app,
			String labelDesc, int xLabel, int yLabel, geogebra.common.awt.Graphics2D g2,
			boolean serif) {
		int lines = 0;
		int fontSize = g2.getFont().getSize();
		float lineSpread = fontSize * 1.5f;

		int xoffset = 0, yoffset = 0;

		// draw text line by line
		int lineBegin = 0;
		int length = labelDesc.length();
		xoffset = 0;
		yoffset = 0;
		for (int i = 0; i < length - 1; i++) {
			if (labelDesc.charAt(i) == '\n') {
				// end of line reached: draw this line
				geogebra.common.awt.Point p = drawIndexedString(app, g2,
						labelDesc.substring(lineBegin, i), xLabel, yLabel
								+ lines * lineSpread, serif);
				if (p.x > xoffset)
					xoffset = p.x;
				if (p.y > yoffset)
					yoffset = p.y;
				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;
		geogebra.common.awt.Point p = drawIndexedString(app, g2, labelDesc.substring(lineBegin),
				xLabel, ypos, serif);
		if (p.x > xoffset)
			xoffset = p.x;
		if (p.y > yoffset)
			yoffset = p.y;
		// labelHasIndex = yoffset > 0;
		int height = (int) ((lines + 1) * lineSpread);

		return new geogebra.awt.Rectangle(xLabel - 3, yLabel - fontSize - 3, xoffset + 6,
				height + 6);
		// labelRectangle.setBounds(xLabel, yLabel - fontSize, xoffset, height
		// );

	}

	/**
	 * Draws a string str with possible indices to g2 at position x, y. The
	 * indices are drawn using the given indexFont. Examples for strings with
	 * indices: "a_1" or "s_{ab}"
	 * 
	 * @param g2
	 * @param str
	 * @return additional pixel needed to draw str (x-offset, y-offset)
	 */
	@Override
	public geogebra.common.awt.Point doDrawIndexedString(AbstractApplication app, geogebra.common.awt.Graphics2D g3,
			String str, float xPos, float yPos, boolean serif) {
		
		geogebra.common.awt.Font g2font = g3.getFont();
		g2font = ((Application) app).getFontCanDisplay(str, serif, g2font.getStyle(),
				g2font.getSize());
		geogebra.common.awt.Font indexFont = getIndexFont(g2font);
		geogebra.common.awt.Font font = g2font;
		geogebra.common.awt.font.TextLayout layout;
		geogebra.common.awt.FontRenderContext frc = g3.getFontRenderContext();

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
		return new geogebra.common.awt.Point(Math.round(x - xPos), Math.round(maxY - yPos));
	}

	private static geogebra.common.awt.Font getIndexFont(geogebra.common.awt.Font f) {
		// index font size should be at least 8pt
		int newSize = Math.max((int) (f.getSize() * 0.9), 8);
		return f.deriveFont(f.getStyle(), newSize);
	}

	/**
	 * @param shape
	 * @param g2
	 */
	final public static void drawWithValueStrokePure(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2.draw(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);
	}

	protected void doDrawWithValueStrokePure(geogebra.common.awt.Shape shape, geogebra.common.awt.Graphics2D g2) {
		drawWithValueStrokePure(geogebra.awt.GenericShape.getAwtShape(shape), geogebra.awt.Graphics2D.getAwtGraphics(g2));
	}
	
	protected void doFillWithValueStrokePure(geogebra.common.awt.Shape shape, geogebra.common.awt.Graphics2D g3) {
		fillWithValueStrokePure(geogebra.awt.GenericShape.getAwtShape(shape), geogebra.awt.Graphics2D.getAwtGraphics(g3));
	}
	
	/**
	 * @param shape
	 * @param g3
	 */
	final public static void doDrawWithValueStrokePure(Shape shape, geogebra.common.awt.Graphics2D g3) {
		drawWithValueStrokePure(shape, geogebra.awt.Graphics2D.getAwtGraphics(g3));
	}
	
	/**
	 * @deprecated
	 * @param shape
	 * @param g2
	 */
	final public static void fillWithValueStrokePure(Shape shape, Graphics2D g2) {
		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2.fill(shape);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);
	}


	
	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return geogebra.awt.BasicStroke.getAwtStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());
	}

	public Object doSetInterpolationHint(geogebra.common.awt.Graphics2D g3,
			boolean needsInterpolationRenderingHint) {
		java.awt.Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g3);
		Object oldInterpolationHint = g2
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

		if (oldInterpolationHint == null)
			oldInterpolationHint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		if (needsInterpolationRenderingHint) {
			// improve rendering quality for transformed images
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		} else {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);					
		}
		return oldInterpolationHint;
	}

	@Override
	protected void doResetInterpolationHint(geogebra.common.awt.Graphics2D g3,
			Object hint) {
		java.awt.Graphics2D g2 = geogebra.awt.Graphics2D.getAwtGraphics(g3);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				hint);
		
	}
	

}
