package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

import elemental2.dom.DomGlobal;

/**
 * Opens profile page.
 */
public class OpenProfilePage extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		DomGlobal.window.open(getProfileLink(app), "_blank", "");
	}

	private String getProfileLink(AppWFull app) {
		GeoGebraTubeUser user = app.getLoginOperation().getModel().getLoggedInUser();
		return user != null ? user.getProfileURL() : "https://accounts.geogebra.org/";
	}
}
