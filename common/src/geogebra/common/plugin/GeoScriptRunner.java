package geogebra.common.plugin;

import geogebra.common.main.App;
import geogebra.common.plugin.script.Script;

public class GeoScriptRunner implements EventListener {

	private App app;

	public GeoScriptRunner(App app) {
		this.app = app;
	}

	public void sendEvent(Event evt) {
		if (app.isScriptingDisabled() || evt.target == null) {
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
			if (evt.type != EventType.UPDATE) {
				script.run(evt);
				app.storeUndoInfo();
			}
			else{
				app.setBlockUpdateScripts(true);
				script.run(evt);
			}
		} catch (ScriptError e) {
			app.showError(e.getScriptError());
		} finally {
			app.setBlockUpdateScripts(false);
		}
	}

	public void reset() {
		// Nothing to do here as the script are removed with the geos.
	}
}
