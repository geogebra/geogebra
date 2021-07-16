package org.geogebra.web.full.gui.inputbar;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.GuiManagerW;

/**
 * Error handler for preview
 * 
 * @author Zbynek
 */
public final class WarningErrorHandler implements ErrorHandler {
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
	public static String getUndefinedValiables(Kernel kernel) {
		return kernel.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV ? null
				: undefinedVariables;
	}

	/**
	 * @param undefinedValiables
	 *            undefined variables
	 */
	public static void setUndefinedValiables(String undefinedValiables) {
		WarningErrorHandler.undefinedVariables = undefinedValiables;
	}
}