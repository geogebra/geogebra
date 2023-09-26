package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.Unicode;

public final class SpreadsheetEditorListener implements MathFieldListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final int row;
	private final int column;

	/**
	 * @param mathField math input
	 * @param kernel kernel
	 * @param row spreadsheet row
	 * @param column spreadsheet column
	 */
	public SpreadsheetEditorListener(MathFieldInternal mathField, Kernel kernel,
			int row, int column) {
		this.mathField = mathField;
		this.kernel = kernel;
		this.row = row;
		this.column = column;
	}

	@Override
	public void onEnter() {
		String cmd = GeoElementSpreadsheet.getSpreadsheetCellName(column, row)
				+ Unicode.ASSIGN_STRING + mathField.getText();
		kernel.getAlgebraProcessor().processAlgebraCommand(
				cmd, true);
	}

	@Override
	public void onKeyTyped(String key) {
		// not needed
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		return false;
	}

	@Override
	public void onInsertString() {
		// not needed
	}

	@Override
	public boolean onEscape() {
		return false;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		return false;
	}
}
