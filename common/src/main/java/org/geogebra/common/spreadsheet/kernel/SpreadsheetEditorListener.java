package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetControlsDelegate;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.Unicode;

public final class SpreadsheetEditorListener implements MathFieldListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final int row;
	private final int column;
	private final SpreadsheetControlsDelegate controls;

	/**
	 * @param mathField math input
	 * @param kernel kernel
	 * @param row spreadsheet row
	 * @param column spreadsheet column
	 */
	public SpreadsheetEditorListener(MathFieldInternal mathField, Kernel kernel,
			int row, int column, SpreadsheetControlsDelegate controls) {
		this.mathField = mathField;
		this.kernel = kernel;
		this.row = row;
		this.column = column;
		this.controls = controls;
	}

	@Override
	public void onEnter() {
		String cmd = GeoElementSpreadsheet.getSpreadsheetCellName(column, row)
				+ Unicode.ASSIGN_STRING + mathField.getText();
		kernel.getAlgebraProcessor().processAlgebraCommand(
				cmd, true);
		controls.hideCellEditor();
	}

	@Override
	public void onKeyTyped(String key) {
		controls.getCellEditor().scrollHorizontally();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		controls.getCellEditor().scrollHorizontally();
		return false;
	}

	@Override
	public void onInsertString() {
		// not needed
	}

	@Override
	public boolean onEscape() {
		controls.hideCellEditor();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		return false;
	}
}
