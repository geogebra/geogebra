package org.geogebra.common.spreadsheet.core;

import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_DOWN;
import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_UP;

import javax.annotation.Nonnull;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

/**
 * Adapts between a MathFieldInternal, spreadsheet input processing, and the SpreadsheetController.
 */
final class SpreadsheetMathFieldAdapter implements MathFieldListener, UnhandledArrowListener {

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

	void discardInput() {
		mathField.parse("");
	}

	@Override
	public void onEnter() {
		commitInput();
		spreadsheetController.onEnter();
	}

	@Override
	public boolean onEscape() {
		discardInput();
		spreadsheetController.onEsc();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		commitInput();
		spreadsheetController.onTab();
		return true;
	}

	@Override
	public void onKeyTyped(String key) {
		// TODO scroll cursor into view?
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
		// TODO scroll cursor into view?
		return false;
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
