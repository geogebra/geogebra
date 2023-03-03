package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.settings.FontSettings;

import com.google.j2objc.annotations.Weak;

/**
 * Renders LaTeX as text for the editor.
 */
public class LaTeXTextRenderer implements TextRenderer {

	// This margin is to match the height of the editor
	public static final int MARGIN =  2;
	private static final int CLIP_PADDING = 8;
	private static final int PADDING = 2;

	@Weak
	private DrawInputBox drawInputBox;
	private FontSettings fontSettings;

	LaTeXTextRenderer(DrawInputBox drawInputBox) {
		this.drawInputBox = drawInputBox;
		fontSettings = drawInputBox.getGeoInputBox().getApp().getSettings()
				.getFontSettings();
	}

	@Override
	public void drawText(GeoInputBox geo, GGraphics2D graphics,
						 GFont font, String text,
						 double xPos, double yPos) {
		int textLeft = (int) Math.round(xPos);

		GFont font1 = getFont(geo, font, fontSettings.getAppFontSize() + 1);
		GDimension textDimension = drawInputBox.measureLatex(graphics, geo,
				font1, text);
		double inputBoxHeight = drawInputBox.getInputFieldBounds().getHeight();
		double diffToCenter = (inputBoxHeight - textDimension.getHeight()) / 2.0;
		int textTop = (int) Math.round(yPos + diffToCenter) ;

		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		int clipWidth = drawInputBox.boxWidth - CLIP_PADDING;
		if (textDimension.getWidth() > clipWidth) {
			// if the text does not fit, reduce the margin a little
			clipWidth = drawInputBox.boxWidth - DrawInputBox.TF_PADDING_HORIZONTAL;
			textLeft -= DrawInputBox.TF_PADDING_HORIZONTAL;
		}
		rect.setRect(textLeft, 0, clipWidth, drawInputBox.getView().getHeight());
		graphics.setClip(rect);

		drawInputBox.drawLatex(graphics, geo, font1, text, textLeft + PADDING,
				textTop, true);
		graphics.resetClip();
	}

	private int calculateInputBoxHeight(GDimension textDimension) {
		int textHeightWithMargin = textDimension.getHeight();
		return Math.max(textHeightWithMargin, DrawInputBox.SYMBOLIC_MIN_HEIGHT);
	}

	@Override
	public GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font,
									String labelDescription) {
		GFont gFont = getFont(geo, font);
		GDimension textDimension =
				drawInputBox.measureLatex(graphics, geo, gFont, geo.getDisplayText());

		int inputBoxHeight = calculateInputBoxHeight(textDimension);
		double labelHeight = drawInputBox.getHeightForLabel(labelDescription);
		double inputBoxTop = drawInputBox.getLabelTop() + (labelHeight
				- inputBoxHeight) / 2;

		return AwtFactory.getPrototype().newRectangle(
				drawInputBox.boxLeft,
				(int) Math.round(inputBoxTop) + PADDING,
				drawInputBox.boxWidth,
				inputBoxHeight);
	}

	private GFont getFont(GeoInputBox geo, GFont font) {
		return getFont(geo, font,
				fontSettings.getAppFontSize() + 3);
	}

	private GFont getFont(GeoInputBox geo, GFont font, int fontSize) {
		int style = font.getLaTeXStyle(geo.isSerifContent());
		return font.deriveFont(style, fontSize * geo.getFontSizeMultiplier());
	}
}
