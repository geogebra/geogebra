package org.geogebra.common.plugin;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.script.Script;

/**
 * Runs GGB Scripts
 */
public class GeoScriptRunner implements EventListener {

	private App app;
	private boolean eventsDuringClick;

	/**
	 * @param app
	 *            application
	 */
	public GeoScriptRunner(App app) {
		this.app = app;
	}

	@Override
	public void sendEvent(Event evt) {
		if (app.isScriptingDisabled() || evt.target == null) {
			return;
		}

		eventsDuringClick = eventsDuringClick || needsStoringUndo(evt.type);
		Script script = evt.target.getScript(evt.type);
		if (script == null) {
			return;
		}

		if (app.getScriptManager().isDisabled(script.getType())) {
			return;
		}

		if (evt.type != EventType.CLICK) {
			if (app.isBlockUpdateScripts() && !evt.isAlwaysDispatched()) {
				return;
			}
		}
		try {
			if (evt.type == EventType.CLICK) {
				if (evt.isAlwaysDispatched()) {
					run(script, evt);
				} else {
					handleClick(script, evt);
				}
			} else {
				app.setBlockUpdateScripts(true);
				boolean ok = run(script, evt);
				app.setBlockUpdateScripts(!ok);
			}
		} catch (ScriptError e) {
			app.showError(e.getScriptError());
		}
	}

	private boolean run(Script script, Event evt) throws ScriptError {
		return script.run(evt);
	}

	/* Some scripts (especially JS) may have no impact on construction and should not
	trigger storing undo. Here we check that something significant happened in script.*/
	private boolean needsStoringUndo(EventType type) {
		return type == EventType.UPDATE || type == EventType.ADD
				|| type == EventType.REMOVE || type == EventType.UPDATE_STYLE;
	}

	private void handleClick(Script script, Event evt) throws ScriptError {
		eventsDuringClick = false;
		script.run(evt);
		if (eventsDuringClick) {
			app.storeUndoInfo();
		}
	}

	@Override
	public void reset() {
		// Nothing to do here as the script are removed with the geos.
	}
}
