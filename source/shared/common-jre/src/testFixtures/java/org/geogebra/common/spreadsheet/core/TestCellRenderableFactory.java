package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.awt.GFont;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;

public class TestCellRenderableFactory implements CellRenderableFactory {
	@Override
	public SelfRenderable getRenderable(Object data, SpreadsheetStyling style,
			int row, int column) {
		return data == null ? null : new SelfRenderable(new StringRenderer(),
				GFont.PLAIN, CellFormat.ALIGN_LEFT, data);
	}
}