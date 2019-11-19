package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;

import com.google.j2objc.annotations.Weak;

/**
 * Renders LaTeX as text for the editor.
 */
public class LaTeXTextRenderer implements TextRenderer {

	// This margin is to match how the editor places the equation
	private static final double MARGIN = 3.0;

	// This margin is to match the height of the editor
	private static final double BOTTOM_MARGIN = 10;

	@Weak
	private DrawInputBox drawInputBox;

	LaTeXTextRenderer(DrawInputBox drawInputBox) {
		this.drawInputBox = drawInputBox;
	}

	@Override
	public void drawText(App app, GeoInputBox geo, GGraphics2D graphics, GFont font,
						 String text, double xPos, double yPos, double boxWidth, int lineHeight) {
		drawInputBox.drawLatex(graphics, geo, font, text,
				(int) Math.round(xPos), (int) Math.round(yPos + MARGIN));
	}

	@Override
	public GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font,
									String labelDescription) {
		int inputBoxHeight = drawInputBox.getPreferredHeight();
		double labelHeight = drawInputBox.getLabelRectangle().getHeight();

		double inputBoxTop = drawInputBox.yLabel - (Math.abs(inputBoxHeight - labelHeight) / 2);

		int inputBoxWidth =
				drawInputBox.measureLatex(graphics, geo, font, geo.getText()).getWidth();

		return AwtFactory.getPrototype().newRectangle(
				drawInputBox.boxLeft,
				(int) Math.round(inputBoxTop),
				Math.max(drawInputBox.boxWidth, inputBoxWidth),
				inputBoxHeight);
	}
}
