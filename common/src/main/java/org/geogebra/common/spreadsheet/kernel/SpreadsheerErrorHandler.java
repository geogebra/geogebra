package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;

public class SpreadsheerErrorHandler implements ErrorHandler {
	final SpreadsheetCellProcessor processor;
	final MathFieldInternal mathField;

	/**
	 * Constructor
	 * @param mathField - text field
	 */
	public SpreadsheerErrorHandler(SpreadsheetCellProcessor processor, MathFieldInternal mathField) {
		this.processor = processor;
		this.mathField = mathField;
	}

	@Override
	public void showError(String msg) {
		mathField.setPlainText("ERROR");
		processor.showError();
	}

	@Override
	public void showCommandError(String command, String message) {

	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void resetError() {

	}
}
