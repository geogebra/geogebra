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

package org.geogebra.common.plugin.script;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;

/**
 * Handler for errors from GGBScript
 */
public class ScriptErrorHandler implements ErrorHandler {

	private App app;
	private Event evt;
	private int line;

	/**
	 * @param app
	 *            application
	 * @param evt
	 *            scripting event
	 * @param line
	 *            line
	 */
	public ScriptErrorHandler(App app, Event evt, int line) {
		this.app = app;
		this.evt = evt;
		this.line = line;
	}

	@Override
	public void showError(String msg) {
		if (evt.type == EventType.UPDATE) {
			app.setBlockUpdateScripts(true);
		}

		app.getDefaultErrorHandler()
				.showError(app.getLocalization().getPlainDefault(
						"ErrorInScriptAtLineAFromObjectB",
						"Error in script at line %0 from object %1",
						String.valueOf(line + 1),
						evt.target.getLabel(StringTemplate.defaultTemplate))
						+ "\n" + msg);
	}

	@Override
	public void resetError() {
		// nothing to do
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {
		if (evt.type == EventType.UPDATE) {
			app.setBlockUpdateScripts(true);
		}
		String errorMessage = message + "\n\n" + app.getLocalization().getPlainDefault(
				"ErrorInScriptAtLineAFromObjectB",
				"Error in script at line %0 from object %1",
				String.valueOf(line + 1),
				evt.target.getLabel(StringTemplate.defaultTemplate));
		app.getDefaultErrorHandler().showCommandError(command, errorMessage);

	}

	@Override
	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

}
