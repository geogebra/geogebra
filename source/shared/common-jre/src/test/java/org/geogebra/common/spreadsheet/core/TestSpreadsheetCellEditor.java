package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

import org.geogebra.common.io.MathFieldCommon;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellDataSerializer;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.MathFieldInternal;

final class TestSpreadsheetCellEditor implements SpreadsheetCellEditor {

	private final MathFieldCommon mathField = new MathFieldCommon(new TemplateCatalog(), null);

	private final SpreadsheetCellDataSerializer cellDataSerializer =
			new DefaultSpreadsheetCellDataSerializer();

	private final TabularData<?> tabularData;
	private boolean showing;

	TestSpreadsheetCellEditor(TabularData<?> tabularData) {
		this.tabularData = tabularData;
	}

	@Override
	public void show(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport, int textAlignment) {
		showing = true;
	}

	@Override
	public void updatePosition(@Nonnull Rectangle editorBounds, @Nonnull Rectangle viewport) {
		// not needed in tests
	}

	@Override
	public void hide() {
		showing = false;
	}

	@Override
	public @Nonnull MathFieldInternal getMathField() {
		return mathField.getInternal();
	}

	@Override
	public @Nonnull SpreadsheetCellProcessor getCellProcessor() {
		return tabularData.getCellProcessor();
	}

	@Override
	public @Nonnull SpreadsheetCellDataSerializer getCellDataSerializer() {
		return cellDataSerializer;
	}

	/**
	 * @return whether this is visible; keeps track of {@link #show} and {@link #hide} calls.
	 */
	public boolean isShowing() {
		return showing;
	}
}
