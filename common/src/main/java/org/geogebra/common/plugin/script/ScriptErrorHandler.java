package org.geogebra.common.plugin.script;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;

public class ScriptErrorHandler implements ErrorHandler {

	private App app;
	private Event evt;
	private int line;

	public ScriptErrorHandler(App app, Event evt, int i) {
		this.app = app;
		this.evt = evt;
		this.line = i;
	}

	public void showError(String msg) {
		if (evt.type == EventType.UPDATE) {
			app.setBlockUpdateScripts(true);
		}
		app.getDefaultErrorHandler()
				.showError(app.getLocalization().getPlain(
				"ErrorInScriptAtLineAFromObjectB", (line + 1) + "",
				evt.target.getLabel(StringTemplate.defaultTemplate)) + "\n"
				+ msg);

	}

	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	public void showCommandError(String command, String message) {
		if (evt.type == EventType.UPDATE) {
			app.setBlockUpdateScripts(true);
		}
		app.getDefaultErrorHandler().showCommandError(command,
				message + "\n\n" + app.getLocalization().getPlain(
						"ErrorInScriptAtLineAFromObjectB", (line + 1)
								+ "",
						evt.target.getLabel(StringTemplate.defaultTemplate)));


	}

	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

}
