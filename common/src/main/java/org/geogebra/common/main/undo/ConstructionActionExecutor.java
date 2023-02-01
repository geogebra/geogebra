package org.geogebra.common.main.undo;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.ActionType;

public class ConstructionActionExecutor
		implements ActionExecutor {

	private final App app;

	public ConstructionActionExecutor(App app) {
		this.app = app;
	}
	
	@Override
	public boolean executeAction(ActionType action, String... args) {
		if (action == ActionType.REMOVE) {
			for (String arg: args) {
				app.getGgbApi().deleteObject(arg);
			}
		} else if (action == ActionType.ADD) {
			for (String arg: args) {
				app.getGgbApi().evalXML(arg);
			}
			app.getActiveEuclidianView().invalidateDrawableList();
		} else if (action == ActionType.UPDATE) {
			for (String arg: args) {
				if (arg.charAt(0) == '<') {
					app.getGgbApi().evalXML(arg);
				} else {
					app.getGgbApi().evalCommand(arg);
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
