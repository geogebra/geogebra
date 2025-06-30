package org.geogebra.common.spreadsheet.core;

import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_DOWN;
import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_UP;

import javax.annotation.Nonnull;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.MathFieldInternalListener;
import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * Adapts between a MathFieldInternal, spreadsheet input processing, and the SpreadsheetController.
 */
final class SpreadsheetMathFieldAdapter implements MathFieldListener, UnhandledArrowListener,
		MathFieldInternalListener {

	private final MathFieldInternal mathField;
	private final int row, column;
	private final SpreadsheetCellProcessor cellProcessor;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param mathField math input
	 *
	 */
	SpreadsheetMathFieldAdapter(@Nonnull MathFieldInternal mathField,
			int row, int column,
			@Nonnull SpreadsheetCellProcessor cellProcessor,
			@Nonnull SpreadsheetController spreadsheetController) {
		this.mathField = mathField;
		this.row = row;
		this.column = column;
		this.cellProcessor = cellProcessor;
		this.spreadsheetController = spreadsheetController;
	}

	void commitInput() {
		cellProcessor.process(mathField.getText(), row, column);
	}

	@Override
	public void onEnter() {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_ENTER)) {
			return;
		}
		spreadsheetController.onEnter();
	}

	@Override
	public boolean onEscape() {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_ESCAPE)) {
			return true;
		}
		spreadsheetController.onEsc();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		if (spreadsheetController.handleKeyPress(JavaKeyCodes.VK_TAB)) {
			return true;
		}
		spreadsheetController.onTab();
		return true;
	}

	@Override
	public void onKeyTyped(String key) {
		// TODO scroll cursor into view?
		// note: this is fired *before* actually handling the input & updating the internal state
		spreadsheetController.onEditorTextChanged();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		// TODO scroll cursor into view?
		// note: this is fired *before* actually handling the input & updating the internal state
		return spreadsheetController.handleKeyPress(keyCode);
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

	@Override
	public void inputChanged(MathFieldInternal mathFieldInternal) {
		// this can be any change: text changed, cursor movement, ...
		spreadsheetController.onEditorTextOrCursorPositionChanged();
	}
}
