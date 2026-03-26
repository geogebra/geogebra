/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.kernel;

import static org.geogebra.common.euclidian.EuclidianConstants.DEFAULT_CHECKBOX_SIZE;

import java.util.function.Supplier;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.DrawBoolean;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.rendering.AwtReTeXGraphicsBridge;
import org.geogebra.common.spreadsheet.rendering.LaTeXRenderer;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;

public final class GeoElementCellRendererFactory implements CellRenderableFactory {

	private final LaTeXRenderer laTeXRenderer;
	private final static StringRenderer stringRenderer = new StringRenderer();
	private final CheckboxCellRenderer checkboxCellRenderer = new CheckboxCellRenderer();
	private final ImageCellRenderer imageCellRenderer = new ImageCellRenderer();
	private final ButtonCellRenderer buttonCellRenderer = new ButtonCellRenderer();
	private final Supplier<Double> fontSize;

	/**
	 * @param bridge graphics converter
	 * @param fontSizeProvider font size provider
	 */
	public GeoElementCellRendererFactory(AwtReTeXGraphicsBridge bridge,
			Supplier<Double> fontSizeProvider) {
		this.laTeXRenderer = new LaTeXRenderer(bridge);
		this.fontSize = fontSizeProvider;
	}

	@Override
	public SelfRenderable getRenderable(Object data, SpreadsheetStyling styling, int row,
			int column) {
		if (data == null) {
			return null;
		}
		Integer fontStyle = styling.getFontStyle(row, column);
		GeoElement geoElement = (GeoElement) data;
		GColor background = styling.getBackgroundColor(row, column,
				geoElement.getBackgroundColor());
		GColor textColor = styling.getTextColor(row, column, getTextColor(geoElement));
		Integer align = styling.getAlignment(row, column);
		if (align == null) {
			align = SpreadsheetStyling.cellFormatFromTextAlignment(
					SpreadsheetStyling.getDefaultTextAlignment(data));
		}
		if (geoElement.isLaTeXDrawableGeo()) {
			TeXFormula tf = new TeXFormula(geoElement
					.toValueString(StringTemplate.latexTemplate));
			GColor fgColor = styling.getTextColor(row, column, textColor);
			TeXIcon icon = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					fontSize.get(), TeXFont.SANSSERIF, fgColor);
			return new SelfRenderable(laTeXRenderer, fontSize.get(),
					fontStyle, align, icon, background, fgColor);
		}
		if (data instanceof GeoBoolean bool && bool.isIndependent()) {
			return new SelfRenderable(checkboxCellRenderer, fontSize.get(),
					fontStyle, align, data, background, textColor);
		}

		if (data instanceof GeoImage) {
			return new SelfRenderable(imageCellRenderer, fontSize.get(),
					fontStyle, align, data, background, textColor);
		}

		if (data instanceof GeoButton) {
			return new SelfRenderable(buttonCellRenderer, fontSize.get(),
					fontStyle, CellFormat.ALIGN_CENTER, data, null, textColor);
		}

		return new SelfRenderable(stringRenderer, fontSize.get(), fontStyle, align,
				getValueString(geoElement),
				background, textColor);
	}

	private GColor getTextColor(GeoElement geoElement) {
		GColor color = geoElement.getObjectColor();
		return color == GColor.BLACK ? GeoGebraColorConstants.NEUTRAL_900 : color;
	}

	private String getValueString(GeoElement geoElement) {
		return geoElement.isEmptySpreadsheetCell() ? ""
				: geoElement.toValueString(StringTemplate.defaultTemplate);
	}

	private static final class CheckboxCellRenderer implements CellRenderer {
		private static final double CHECKBOX_SCALE = 0.5;

		@Override
		public void draw(Object data, double fontSize, int fontStyle, double offsetX,
				GGraphics2D g2d, Rectangle cellBorder) {
			double positionX = cellBorder.getMinX() + offsetX;
			double positionY = cellBorder.getMinY() + (cellBorder.getHeight()
					- DEFAULT_CHECKBOX_SIZE * CHECKBOX_SCALE) / 2;
			g2d.saveTransform();
			g2d.translate(positionX, positionY);
			g2d.scale(CHECKBOX_SCALE, CHECKBOX_SCALE);
			DrawBoolean.CheckBoxIcon.paintIcon(((GeoBoolean) data).getBoolean(),
					!((GeoBoolean) data).isSelectionAllowed(null), g2d, 0, 0);
			g2d.restoreTransform();
		}

		@Override
		public boolean match(Object renderable) {
			return renderable instanceof GeoBoolean;
		}

		@Override
		public double measureWidth(Object renderable, int fontStyle, double fontSize) {
			return DEFAULT_CHECKBOX_SIZE * CHECKBOX_SCALE;
		}
	}

	private static final class ImageCellRenderer implements CellRenderer {
		@Override
		public void draw(Object data, double fontSize, int fontStyle, double offsetX,
				GGraphics2D g2d, Rectangle cellBorder) {
			g2d.saveTransform();
			g2d.translate(cellBorder.getMinX(), cellBorder.getMinY());
			g2d.drawImage(((GeoImage) data).getFillImage(), 0, 0);
			g2d.restoreTransform();
		}

		@Override
		public boolean match(Object renderable) {
			return renderable instanceof GeoImage;
		}

		@Override
		public double measureWidth(Object renderable, int fontStyle, double fontSize) {
			return 32;
		}
	}

	private static final class ButtonCellRenderer implements CellRenderer {
		@Override
		public void draw(Object data, double fontSize, int fontStyle, double offsetX,
				GGraphics2D g2d, Rectangle cellBorder) {
			g2d.saveTransform();
			g2d.translate(cellBorder.getMinX(), cellBorder.getMinY());
			GeoButton geoButton = (GeoButton) data;
			g2d.setColor(geoButton.getBackgroundColor());
			g2d.fillRoundRect(0, 0, cellBorder.getWidth(), cellBorder.getHeight(), 8, 8);
			g2d.setColor(geoButton.getObjectColor());
			g2d.restoreTransform();
			stringRenderer.draw(geoButton.getCaption(StringTemplate.defaultTemplate),
					fontSize, fontStyle, offsetX, g2d, cellBorder);
		}

		@Override
		public boolean match(Object renderable) {
			return renderable instanceof GeoButton;
		}

		@Override
		public double measureWidth(Object renderable, int fontStyle, double fontSize) {
			return stringRenderer.measureWidth(((GeoButton) renderable)
							.getCaption(StringTemplate.defaultTemplate), fontStyle, fontSize);
		}
	}

	@Override
	public double getFontSize() {
		return fontSize.get();
	}
}
