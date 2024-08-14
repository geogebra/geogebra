package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class SpreadsheetErrorHandler implements ErrorLogger {
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

	@Override
	public void log(Throwable e) {
		Log.warn(e.getMessage());
	}
}
