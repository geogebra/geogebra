package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.Unicode;

public final class SpreadsheetEditorListener implements MathFieldListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final int row;
	private final int column;
	private final SpreadsheetCellEditor editor;

	/**
	 * @param mathField math input
	 * @param kernel kernel
	 * @param row spreadsheet row
	 * @param column spreadsheet column
	 * @param editor equation editor for spreadsheet
	 */
	public SpreadsheetEditorListener(MathFieldInternal mathField, Kernel kernel,
			int row, int column, SpreadsheetCellEditor editor) {
		this.mathField = mathField;
		this.kernel = kernel;
		this.row = row;
		this.column = column;
		this.editor = editor;
	}

	@Override
	public void onEnter() {
		if (!mathField.getText().isEmpty()) {
			String cmd = GeoElementSpreadsheet.getSpreadsheetCellName(column, row)
					+ Unicode.ASSIGN_STRING + mathField.getText();
			kernel.getAlgebraProcessor().processAlgebraCommand(
					cmd, true);
		}
		editor.hide();
	}

	@Override
	public void onKeyTyped(String key) {
		editor.scrollHorizontally();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		editor.scrollHorizontally();
		return false;
	}

	@Override
	public boolean onEscape() {
		editor.hide();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		onEnter();
		editor.runOnTabCallback();
		return true;
	}
}
