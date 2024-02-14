package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_DOWN;
import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_UP;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.JavaKeyCodes;
import com.himamis.retex.editor.share.util.Unicode;

public final class SpreadsheetEditorListener implements MathFieldListener, UnhandledArrowListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final int row;
	private final int column;
	private final SpreadsheetCellEditor editor;
	private final Spreadsheet spreadsheet;

	/**
	 * @param mathField math input
	 * @param kernel kernel
	 * @param row spreadsheet row
	 * @param column spreadsheet column
	 * @param editor equation editor for spreadsheet
	 */
	public SpreadsheetEditorListener(MathFieldInternal mathField, Kernel kernel,
			int row, int column, SpreadsheetCellEditor editor, Spreadsheet spreadsheet) {
		this.mathField = mathField;
		this.kernel = kernel;
		this.row = row;
		this.column = column;
		this.editor = editor;
		this.spreadsheet = spreadsheet;
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
		mathField.setPlainText("");
		editor.hide();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		onEnter();
		spreadsheet.getController().moveRight(false);
		return true;
	}

	@Override
	public void onArrow(int keyCode) {
		if (keyCode == VK_UP) {
			mathField.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_HOME));
		}
		if (keyCode == VK_DOWN) {
			mathField.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_END));
		}
	}
}
