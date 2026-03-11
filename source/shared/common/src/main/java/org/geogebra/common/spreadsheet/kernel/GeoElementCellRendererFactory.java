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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.DrawBoolean;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.rendering.AwtReTeXGraphicsBridge;
import org.geogebra.common.spreadsheet.rendering.LaTeXRenderer;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXFormula;

public final class GeoElementCellRendererFactory implements CellRenderableFactory {

	private final LaTeXRenderer laTeXRenderer;
	private final StringRenderer stringRenderer = new StringRenderer();
	private final CheckboxCellRenderer checkboxCellRenderer = new CheckboxCellRenderer();

	public GeoElementCellRendererFactory(AwtReTeXGraphicsBridge bridge) {
		this.laTeXRenderer = new LaTeXRenderer(bridge);
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
		Integer align = styling.getAlignment(row, column);
		if (align == null) {
			align = SpreadsheetStyling.cellFormatFromTextAlignment(
					SpreadsheetStyling.getDefaultTextAlignment(data));
		}
		if (geoElement.isLaTeXDrawableGeo()) {
			TeXFormula tf = new TeXFormula(geoElement
					.toValueString(StringTemplate.latexTemplate));
			GColor fgColor = styling.getTextColor(row, column, getTextColor(geoElement));
			return new SelfRenderable(laTeXRenderer,
					fontStyle, align,
					tf.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							StringRenderer.FONT_SIZE, TeXFont.SANSSERIF,
							fgColor),
					background, fgColor);
		}
		if (data instanceof GeoBoolean && ((GeoBoolean) data).isIndependent()) {
			return new SelfRenderable(checkboxCellRenderer, fontStyle, align, data, background,
					SpreadsheetStyling.getDefaultTextColor());
		}

		return new SelfRenderable(stringRenderer, fontStyle, align,
				getValueString(geoElement),
				background, getTextColor(geoElement));
	}

	private GColor getTextColor(GeoElement geoElement) {
		GColor textColor = geoElement.getObjectColor();
		return textColor == GColor.BLACK
				? SpreadsheetStyling.getDefaultTextColor() : textColor;
	}

	private String getValueString(GeoElement geoElement) {
		return geoElement.isEmptySpreadsheetCell() ? ""
				: geoElement.toValueString(StringTemplate.defaultTemplate);
	}

	private static final class CheckboxCellRenderer implements CellRenderer {
		private static final double CHECKBOX_SCALE = 0.5;

		@Override
		public void draw(Object data, int fontStyle, double offsetX,
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
		public double measure(Object renderable, int fontStyle) {
			return DEFAULT_CHECKBOX_SIZE * CHECKBOX_SCALE;
		}
	}
}
