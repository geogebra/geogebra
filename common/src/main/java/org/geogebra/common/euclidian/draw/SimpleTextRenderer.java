package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;

import com.google.j2objc.annotations.Weak;

/**
 * Renders the text as a "flat" string.
 */
public class SimpleTextRenderer implements TextRenderer {

	@Weak
	private CanvasDrawable drawable;

	SimpleTextRenderer(CanvasDrawable drawable) {
		this.drawable = drawable;
	}

	@Override
	public void drawText(App app, GeoInputBox geo, GGraphics2D graphics, GFont font,
						 String text, double xPos, double yPos, double boxWidth, int lineHeight) {
		double textBottom = yPos + lineHeight;
		String truncated = text.substring(0, getTruncIndex(text, graphics, boxWidth));
		double newXPos = xPos + getTextAlignment(truncated, geo, app, (int) boxWidth, graphics);
		EuclidianStatic.drawIndexedString(app, graphics, truncated, newXPos, textBottom, false);
	}

	private int getTextAlignment(String text, GeoInputBox geoInputBox, App app,
								 int boxWidth, GGraphics2D graphics2D) {
		switch (geoInputBox.getAlignment()) {
			case CENTER:
				return (getTextWidth(app, graphics2D, text, boxWidth) - 5) / 2;
			case RIGHT:
				return getTextWidth(app, graphics2D, text, boxWidth) - 10;
			default:
				return 0;
		}
	}

	private int getTextWidth(App app, GGraphics2D graphics2D, String text, int boxWidth) {
		return boxWidth  - EuclidianStatic.drawIndexedString(app, graphics2D, text,
				0, 0, false, false ,null, null).x;
	}

	@Override
	public GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font,
									String labelDescription) {
		drawable.measureLabel(graphics, geo, labelDescription);
		return AwtFactory.getPrototype().newRectangle(
				drawable.boxLeft, drawable.boxTop, drawable.boxWidth, drawable.boxHeight);
	}

	private int getTruncIndex(String text, GGraphics2D g2, double boxWidth) {
		int idx = text.length();
		int mt = drawable.measureTextWidth(text, g2.getFont(), g2);
		while (mt > boxWidth && idx > 0) {
			idx--;
			mt = drawable.measureTextWidth(text.substring(0, idx), g2.getFont(), g2);

		}
		return idx;
	}
}
