package org.geogebra.desktop.euclidian;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.desktop.main.AppD;

public class EuclidianStaticD extends org.geogebra.common.euclidian.EuclidianStatic {

	// This has to be made singleton or use prototype,
	// while its static methods be made non-static,
	// or implement by some other solution e.g. AbstractEuclidianStatic,
	// in order to be usable from Common. (like an adapter)

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

		return new org.geogebra.desktop.awt.GRectangleD(xLabel - 3, yLabel - fontSize - 3,
				xoffset + 6, height + 6);
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

	static public java.awt.BasicStroke getDefaultStrokeAwt() {
		return org.geogebra.desktop.awt.GBasicStrokeD.getAwtStroke(EuclidianStatic
				.getDefaultStroke());
	}

}
