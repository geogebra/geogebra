package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
import org.geogebra.common.util.AsyncOperation;

public class SpreadsheetErrorHandler implements ErrorHandler {
	final SpreadsheetCellProcessor processor;

	/**
	 * Constructor
	 */
	public SpreadsheetErrorHandler(SpreadsheetCellProcessor processor) {
		this.processor = processor;
	}

	@Override
	public void showError(String msg) {
		processor.markError();
	}

	@Override
	public void showCommandError(String command, String message) {
		processor.markError();
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
		// nothing to do here
	}
}
