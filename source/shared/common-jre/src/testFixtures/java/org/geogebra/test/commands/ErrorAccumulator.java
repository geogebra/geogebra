/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.test.commands;

import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;

public class ErrorAccumulator implements ErrorLogger {
	private String errors = "";
	private String errorsSinceReset = "";
	private String currentCommand;

	@Override
	public void showError(String msg) {
		errors += msg;
		errorsSinceReset += msg;
	}

	@Override
	public void showCommandError(String command, String message) {
		showError(message);
	}

	@Override
	public String getCurrentCommand() {
		return currentCommand;
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetError() {
		errorsSinceReset = "";
	}

	public String getErrors() {
		return errors;
	}

	public String getErrorsSinceReset() {
		return errorsSinceReset;
	}

	@Override
	public void log(Throwable e) {
		// errors expected, don't print
	}

	public void setCurrentCommand(String currentCommand) {
		this.currentCommand = currentCommand;
	}
}
