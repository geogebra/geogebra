package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens file in Mebis Tafel.
 */
public class OpenFileActionMebis extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, final AppWFull app) {
		if (isLoggedOut(app)) {
			app.getActivity().markSearchOpen();
			app.getGuiManager().listenToLogin();
			app.getLoginOperation().showLoginDialog();
			app.getGuiManager().setRunAfterLogin(() -> app.openSearch(null));
		} else {
			app.openSearch(null);
		}
	}

	/**
	 * @return true if the whiteboard is active and the user logged in
	 */
	static boolean isLoggedOut(App app) {
		return app.getLoginOperation() != null
				&& !app.getLoginOperation().isLoggedIn();
	}
}