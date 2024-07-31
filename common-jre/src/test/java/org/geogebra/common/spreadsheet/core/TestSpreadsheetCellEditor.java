package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.MetaModel;

final class TestSpreadsheetCellEditor implements SpreadsheetCellEditor {

	private final MathFieldCommon mathField = new MathFieldCommon(new MetaModel(), null);

	private final SpreadsheetCellProcessor cellProcessor = new SpreadsheetCellProcessor() {
		@Override
		public void process(String input, int row, int column) {
			tabularData.setContent(row, column, input);
		}

		@Override
		public void markError() {
			// nothing to do here
		}
	};
	private final SpreadsheetCellDataSerializer cellDataSerializer =
			new DefaultSpreadsheetCellDataSerializer();

	private final TabularData tabularData;

	TestSpreadsheetCellEditor(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	@Override
	public void show(Rectangle editorBounds, Rectangle viewport, int textAlignment) {
	}

	@Override
	public void updatePosition(Rectangle editorBounds, Rectangle viewport) {
		// not needed in tests
	}

	@Override
	public void hide() {
	}

	@Nonnull
	@Override
	public MathFieldInternal getMathField() {
		return mathField.getInternal();
	}

	@Nonnull
	@Override
	public SpreadsheetCellProcessor getCellProcessor() {
		return cellProcessor;
	}

	@Nonnull
	@Override
	public SpreadsheetCellDataSerializer getCellDataSerializer() {
		return cellDataSerializer;
	}
}
