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
		Graphics2D awtg2 = geogebra.awt.Graphics2D.getAwtGraphics(g3);
		Object oldHint = awtg2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
		awtg2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g3.fill(shape);
		awtg2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, oldHint);
	}
	
	/**
	 * @param shape
	 * @param g3
	 */
	final public static void doDrawWithValueStrokePure(Shape shape, geogebra.common.awt.Graphics2D g3) {
		drawWithValueStrokePure(shape, geogebra.awt.Graphics2D.getAwtGraphics(g3));
	}

	
	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return geogebra.awt.BasicStroke.getAwtStroke(geogebra.common.euclidian.EuclidianStatic.getDefaultStroke());
	}

	@Override
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
