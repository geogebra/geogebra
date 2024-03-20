package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoInputBox;

import com.google.j2objc.annotations.Weak;

/**
 * Renders LaTeX as text for the editor.
 */
public class LaTeXTextRenderer implements TextRenderer {
	private static final int CLIP_PADDING = 8;
	public static final int BORDER_THICKNESS = 6;
	@Weak
	private final DrawInputBox drawInputBox;
	private final TextRendererSettings settings;

	LaTeXTextRenderer(DrawInputBox drawInputBox, TextRendererSettings settings) {
		this.drawInputBox = drawInputBox;
		this.settings = settings;
	}

	@Override
	public TextRendererSettings getSettings() {
		return settings;
	}

	@Override
	public void drawText(GeoInputBox geo, GGraphics2D graphics,
			GFont font, String text, double xPos, double yPos) {
		int textLeft = (int) Math.round(xPos) + settings.getFixMargin();

		GFont font1 = getFont(font, settings.getRendererFontSize());
		GDimension textDimension = drawInputBox.measureLatex(geo,
				font1, text,  true);
		double inputBoxHeight = drawInputBox.getInputFieldBounds().getHeight()
				+ 2 * settings.getFixMargin();
		double diffToCenter = (inputBoxHeight - textDimension.getHeight()) / 2.0;
		int textTop = (int) Math.round(yPos + diffToCenter) - settings.getFixMargin();

		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		int clipWidth = drawInputBox.boxWidth - CLIP_PADDING;
		if (textDimension.getWidth() > clipWidth) {
			// if the text does not fit, reduce the margin a little
			clipWidth = drawInputBox.boxWidth - DrawInputBox.TF_PADDING_HORIZONTAL;
			textLeft -= DrawInputBox.TF_PADDING_HORIZONTAL;
		}
		rect.setRect(textLeft, 0, clipWidth - BORDER_THICKNESS, drawInputBox.getView().getHeight());
		graphics.setClip(rect);
		drawInputBox.drawLatex(graphics, geo, font1, text, textLeft - settings.getFixMargin(),
				textTop, true);

		graphics.resetClip();
	}

	private int calculateInputBoxHeight(GDimension textDimension) {
		int textHeightWithMargin = textDimension.getHeight() + settings.getFixMargin()
				+ BORDER_THICKNESS;
		return Math.max(textHeightWithMargin, settings.getMinHeight()
				+ BORDER_THICKNESS);
	}

	@Override
	public GRectangle measureBounds(GGraphics2D graphics, GeoInputBox geo, GFont font,
									String labelDescription) {
		GFont gFont = getFont(font, settings.getRendererFontSize());
		GDimension textDimension =
				CanvasDrawable.measureLatex(geo.getKernel().getApplication(), gFont,
						geo.getDisplayText(), geo.isSerifContent());

		int inputBoxHeight = calculateInputBoxHeight(textDimension);
		double labelHeight = drawInputBox.getHeightForLabel(labelDescription);
		double inputBoxTop = drawInputBox.getLabelTop() + (labelHeight
				- inputBoxHeight) / 2;

		return AwtFactory.getPrototype().newRectangle(
				drawInputBox.boxLeft,
				(int) inputBoxTop,
				drawInputBox.boxWidth,
				inputBoxHeight);
	}

	private GFont getFont(GFont font, int fontSize) {
		return font.deriveFont(font.getStyle(), fontSize);
	}
}
