package org.geogebra.common.main.undo;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;

public class ConstructionActionExecutor
		implements ActionExecutor {

	private final App app;

	public ConstructionActionExecutor(App app) {
		this.app = app;
	}
	
	@Override
	public boolean executeAction(EventType action, String... args) {
		if (action == EventType.REMOVE) {
			app.getGgbApi().deleteObject(args[0]);
		} else if (action == EventType.ADD) {
			app.getGgbApi().evalXML(args[1]);
		} else if (action == EventType.UPDATE) {
			app.getGgbApi().evalXML(args[2]);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean undoAction(EventType action, String... args) {
		if (action == EventType.ADD) {
			executeAction(EventType.REMOVE, args[0]);
			return true;
		} else if (action == EventType.UPDATE) {
			executeAction(EventType.UPDATE, args[0], args[2], args[1]);
			return true;
		}
		return false;
	}
}
