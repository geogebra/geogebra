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

package org.geogebra.web.full.gui.inputbar;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;

/**
 * Error handler for preview
 * 
 * @author Zbynek
 */
public final class WarningErrorHandler implements ErrorLogger {
	private static String undefinedVariables;
	private final App app2;
	private final HasHelpButton input;

	/**
	 * @param app2
	 *            application
	 * @param input
	 *            input element
	 */
	WarningErrorHandler(App app2, HasHelpButton input) {
		this.app2 = app2;
		this.input = input;
	}

	@Override
	public void showError(String msg) {
		input.setError(msg);
	}

	@Override
	public void resetError() {
		showError(null);
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		input.setUndefinedVariables(string);
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {
		input.setCommandError(command);
		if (((GuiManagerW) app2.getGuiManager())
				.hasInputHelpPanel()) {
			InputBarHelpPanelW helpPanel = ((GuiManagerW) app2
					.getGuiManager()).getInputHelpPanel();
			helpPanel.focusCommand(
					app2.getLocalization().getCommand(command));
			input.getHelpToggle().asWidget().getElement()
					.setTitle(app2.getLocalization().getInvalidInputError());
		}
	}

	@Override
	public String getCurrentCommand() {
		return input.getCommand();
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return undefined vars or null in symbolic mode
	 */
	public static String getUndefinedVariables(Kernel kernel) {
		return kernel.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV ? null
				: undefinedVariables;
	}

	/**
	 * @param undefinedVariables
	 *            undefined variables
	 */
	public static void setUndefinedVariables(String undefinedVariables) {
		WarningErrorHandler.undefinedVariables = undefinedVariables;
	}

	@Override
	public void log(Throwable e) {
		Log.trace(e);
	}
}