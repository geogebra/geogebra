package org.geogebra.common.spreadsheet.kernel;

import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_DOWN;
import static com.himamis.retex.editor.share.util.JavaKeyCodes.VK_UP;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellEditor;
import org.geogebra.common.spreadsheet.core.SpreadsheetController;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.KeyEvent;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.util.JavaKeyCodes;

// TODO move to spreadsheet package, make internal
public final class SpreadsheetEditorListener implements MathFieldListener, UnhandledArrowListener {

	private final MathFieldInternal mathField;
	private final String cellName;
	private final SpreadsheetCellProcessor cellProcessor;
	private final SpreadsheetController spreadsheetController;

	/**
	 * @param mathField math input
	 *
	 */
	public SpreadsheetEditorListener(@Nonnull MathFieldInternal mathField,
			@Nonnull String cellName,
			@Nonnull  SpreadsheetCellProcessor cellProcessor,
			@Nonnull SpreadsheetController spreadsheetController) {
		this.mathField = mathField;
		this.cellName = cellName;
		this.cellProcessor = cellProcessor;
		this.spreadsheetController = spreadsheetController;
	}

	@Override
	public void onEnter() {
		cellProcessor.process(mathField.getText(), cellName);
		spreadsheetController.onEnter();
	}

	@Override
	public void onKeyTyped(String key) {
//		editor.scrollHorizontally();
	}

	@Override
	public boolean onArrowKeyPressed(int keyCode) {
//		editor.scrollHorizontally();
		return false;
	}

	@Override
	public boolean onEscape() {
		mathField.parse("");
		spreadsheetController.hideEditor();
		return true;
	}

	@Override
	public boolean onTab(boolean shiftDown) {
		onEnter();
//		spreadsheetControlelr.tabPressed(); // TODO
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
