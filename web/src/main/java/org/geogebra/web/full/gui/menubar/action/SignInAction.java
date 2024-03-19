package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens sign in window.
 */
public class SignInAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		if (!app.getNetworkOperation().isOnline()) {
			return;
		}

		LogInOperation logInOperation = app.getLoginOperation();
		if (!logInOperation.isLoggedIn()) {
			logInOperation.showLoginDialog();
		}
	}
}
