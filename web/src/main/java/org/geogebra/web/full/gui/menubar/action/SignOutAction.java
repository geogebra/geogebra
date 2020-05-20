package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Signs out.
 */
public class SignOutAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		app.getLoginOperation().logOut();
	}
}
