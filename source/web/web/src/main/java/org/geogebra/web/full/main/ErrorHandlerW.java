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

package org.geogebra.web.full.main;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.ManualPage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Default error handler
 */
public class ErrorHandlerW implements ErrorHandler {
	private final AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ErrorHandlerW(AppW app) {
		this.app = app;
	}

	@Override
	public void showError(String msg) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		showErrorDialog("Error", null, "OK", msg, null);
	}

	@Override
	public void resetError() {
		// do nothing
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void showCommandError(final String command, String message) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		showErrorDialog(app.getLocalization().getError("Error"), "Close",
				"ShowOnlineHelp", message, () -> openCommandHelp(command));
	}

	/**
	 * @param command
	 *            command name
	 */
	protected void openCommandHelp(String command) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().openHelp(ManualPage.COMMAND, command);
		}
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	private void showErrorDialog(String title, String negBtn, String posBtn,
			String message, Runnable posBtnAction) {
		DialogData data = new DialogData(title, negBtn, posBtn);
		ComponentDialog dialog = new ComponentDialog(app, data, false, true);
		FlowPanel messagePanel = new FlowPanel();
		String[] lines = message.split("\n");
		for (String item : lines) {
			messagePanel.add(new Label(item));
		}
		dialog.addDialogContent(messagePanel);
		if (posBtnAction != null) {
			dialog.setOnPositiveAction(posBtnAction);
		}
		dialog.show();
	}
}
