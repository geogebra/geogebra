package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class TestSpreadsheetCellEditor implements SpreadsheetCellEditor {


	@Override
	public void show(Rectangle editorBounds, Rectangle viewport, int textAlignment) {

	}

	@Override
	public void hide() {

	}

	@Override
	public void scrollCursorVisible() {

	}

	@Nonnull
	@Override
	public MathFieldInternal getMathField() {
		return null;
	}

	@Nonnull
	@Override
	public DefaultSpreadsheetCellProcessor getCellProcessor() {
		return null;
	}
}
