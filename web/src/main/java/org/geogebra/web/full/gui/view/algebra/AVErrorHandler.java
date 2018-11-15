package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Error handler for AV input
 *
 */
final class AVErrorHandler implements ErrorHandler {
	/**
	 * 
	 */
	private final RadioTreeItem radioTreeItem;
	private final boolean valid;
	private final boolean allowSliders;
	private final boolean withSliders;

	/**
	 * @param radioTreeItem
	 *            parent item
	 * @param valid
	 *            previous input valid
	 * @param allowSliders
	 *            whether to allow sliders at all
	 * @param withSliders
	 *            whether to allow slider creation without asking
	 */
	AVErrorHandler(RadioTreeItem radioTreeItem, boolean valid, boolean allowSliders,
			boolean withSliders) {
		this.radioTreeItem = radioTreeItem;
		this.valid = valid;
		this.allowSliders = allowSliders;
		this.withSliders = withSliders;
	}

	@Override
	public void showError(String msg) {
		this.radioTreeItem.errorMessage = valid ? msg
				: this.radioTreeItem.loc.getError("InvalidInput");

		this.radioTreeItem.commandError = null;
		this.radioTreeItem.showCurrentError();
		this.radioTreeItem.saveError();
	}

	@Override
	public void resetError() {
		showError(null);
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		if (withSliders) {
			return true;
		}

		if (!(allowSliders && valid) && this.radioTreeItem.app.getLocalization()
				.getReverseCommand(getCurrentCommand()) != null) {
			showCommandError(this.radioTreeItem.app.getLocalization()
					.getReverseCommand(getCurrentCommand()), null);

			return false;
		}
		callback.callback(new String[] { "7" });
		return false;
	}

	@Override
	public void showCommandError(final String command,
			final String message) {
		this.radioTreeItem.commandError = command;
		this.radioTreeItem.errorMessage = message;
		this.radioTreeItem.showCurrentError();
		this.radioTreeItem.saveError();
	}

	@Override
	public String getCurrentCommand() {
		return this.radioTreeItem.getCommand();
	}
}