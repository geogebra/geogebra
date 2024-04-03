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

public final class SpreadsheetEditorListener implements MathFieldListener, UnhandledArrowListener {

	final MathFieldInternal mathField;
	final Kernel kernel;
	private final SpreadsheetCellEditor editor;
	private final Spreadsheet spreadsheet;
	private final SpreadsheetCellProcessor processor;

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
		this.editor = editor;
		this.spreadsheet = spreadsheet;
		processor = new SpreadsheetCellProcessor(
				GeoElementSpreadsheet.getSpreadsheetCellName(column, row),
				kernel.getAlgebraProcessor(), kernel.getApplication().getDefaultErrorHandler());
	}

	@Override
	public void onEnter() {
		processor.process(mathField.getText());
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
