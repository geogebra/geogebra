package geogebra.common.plugin;

import geogebra.common.main.App;
import geogebra.common.plugin.script.Script;

public class GeoScriptRunner implements EventListener {

	private App app;

	public GeoScriptRunner(App app) {
		this.app = app;
	}

	public void sendEvent(Event evt) {
		if (app.isScriptingDisabled()) {
			return;
		}
		Script script = evt.target.getScript(evt.type);
		if (script == null) {
			return;
		}
		if (evt.type == EventType.UPDATE) {
			if (app.isBlockUpdateScripts()) {
				return;
			}
		}
		try {
			app.setBlockUpdateScripts(true);
			script.run(evt);
			if (evt.type != EventType.UPDATE) {
				app.storeUndoInfo();
			}
		} catch (ScriptError e) {
			app.showError(e.getLocalizedMessage());
		} finally {
			app.setBlockUpdateScripts(false);
		}
	}

	public void reset() {
		// Nothing to do here as the script are removed with the geos.
	}
}
