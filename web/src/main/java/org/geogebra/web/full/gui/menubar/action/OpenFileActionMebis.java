package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens file in Mebis Tafel.
 */
public class OpenFileActionMebis extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, final AppWFull app) {
		if (isLoggedOut(app)) {
			app.getGuiManager().listenToLogin();
			app.getLoginOperation().showLoginDialog();
			app.getGuiManager().setRunAfterLogin(() -> app.openSearch(null));
		} else {
			app.openSearch(null);
		}
	}

	/**
	 * @param app see {@link AppWFull}
	 * @return true if the whiteboard is active and the user logged in
	 */
	private boolean isLoggedOut(AppWFull app) {
		return app.getLoginOperation() != null
				&& !app.getLoginOperation().isLoggedIn();
	}
}