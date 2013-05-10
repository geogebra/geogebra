package geogebra.euclidian;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GDimension;
import geogebra.common.awt.GFont;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.DrawEquation;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;

public class EuclidianStaticD extends geogebra.common.euclidian.EuclidianStatic{

	// This has to be made singleton or use prototype,
	// while its static methods be made non-static,
	// or implement by some other solution e.g. AbstractEuclidianStatic,
	// in order to be usable from Common. (like an adapter)



	@Override
	public final GRectangle doDrawMultilineLaTeX(App app,
			GGraphics2D tempGraphics, GeoElement geo, GGraphics2D g2, GFont font,
			GColor fgColor, GColor bgColor, String labelDesc, int xLabel,
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
		for (int i = 0, currentLine = 0; i < elements.length; ++i) {
			if (isLaTeX) {
				// save the height of this element by drawing it to a temporary
				// buffer
				GDimension dim = new geogebra.awt.GDimensionD();
				dim = app.getDrawEquation().
						drawEquation(app, geo, tempGraphics, 0, 0,
						elements[i], font, ((GeoText) geo).isSerifFont(),
						fgColor, bgColor, false);

				int height = dim.getHeight();

				// depth += dim.depth;

				elementHeights.add(new Integer(height));

				// check if this element is taller than every else in the line
				if (height > (lineHeights.get(currentLine)).intValue())
					lineHeights.set(currentLine, new Integer(height));
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
			if (isLaTeX) {
				// calculate the y offset of this element by: (lineHeight -
				// elementHeight) / 2
				yOffset = (((lineHeights.get(currentLine))).intValue() - ((elementHeights
						.get(currentElement))).intValue()) / 2;

				DrawEquation de = app.getDrawEquation();
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

		return AwtFactory.prototype.newRectangle(xLabel - 3, yLabel - 3 + depth, width + 6,
				height + 6);

	}


	public final static GRectangle drawMultiLineIndexedText(AppD app,
			String labelDesc, int xLabel, int yLabel, GGraphics2D g2,
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
				GPoint p = drawIndexedString(app, g2,
						labelDesc.substring(lineBegin, i), xLabel, yLabel
								+ lines * lineSpread, serif, true);
				if (p.x > xoffset)
					xoffset = p.x;
				if (p.y > yoffset)
					yoffset = p.y;
				lines++;
				lineBegin = i + 1;
			}
		}

		float ypos = yLabel + lines * lineSpread;
		GPoint p = drawIndexedString(app, g2, labelDesc.substring(lineBegin),
				xLabel, ypos, serif, true);
		if (p.x > xoffset)
			xoffset = p.x;
		if (p.y > yoffset)
			yoffset = p.y;
		// labelHasIndex = yoffset > 0;
		int height = (int) ((lines + 1) * lineSpread);

		return new geogebra.awt.GRectangleD(xLabel - 3, yLabel - fontSize - 3, xoffset + 6,
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

	@Override
	protected void doDrawWithValueStrokePure(GShape shape, GGraphics2D g2) {
		drawWithValueStrokePure(geogebra.awt.GGenericShapeD.getAwtShape(shape), geogebra.awt.GGraphics2DD.getAwtGraphics(g2));
	}
	
	@Override
	protected void doFillWithValueStrokePure(GShape shape, GGraphics2D g3) {
		Graphics2D awtg2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
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
	final public static void doDrawWithValueStrokePure(Shape shape, GGraphics2D g3) {
		drawWithValueStrokePure(shape, geogebra.awt.GGraphics2DD.getAwtGraphics(g3));
	}

	
	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return geogebra.awt.GBasicStrokeD.getAwtStroke(EuclidianStatic.getDefaultStroke());
	}

	@Override
	public Object doSetInterpolationHint(GGraphics2D g3,
			boolean needsInterpolationRenderingHint) {
		java.awt.Graphics2D g2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
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
	protected void doResetInterpolationHint(GGraphics2D g3,
			Object hint) {
		java.awt.Graphics2D g2 = geogebra.awt.GGraphics2DD.getAwtGraphics(g3);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				hint);
		
	}
	

}
