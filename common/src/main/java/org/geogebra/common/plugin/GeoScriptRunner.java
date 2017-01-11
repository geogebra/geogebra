package org.geogebra.common.plugin;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.script.Script;

/**
 * Runs GGB Scripts
 */
public class GeoScriptRunner implements EventListener {

	private App app;

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
		Script script = evt.target.getScript(evt.type);
		if (script == null) {
			return;
		}
		if (evt.type == EventType.UPDATE) {
			if (app.isBlockUpdateScripts() && !evt.isAlwaysDispatched()) {
				return;
			}
		}
		try {
			if (evt.type != EventType.UPDATE) {
				script.run(evt);
				app.storeUndoInfo();
			} else {
				app.setBlockUpdateScripts(true);
				boolean ok = script.run(evt);
				app.setBlockUpdateScripts(!ok);
			}
		} catch (ScriptError e) {
			app.showError(e.getScriptError());
		}
	}

	@Override
	public void reset() {
		// Nothing to do here as the script are removed with the geos.
	}
}
