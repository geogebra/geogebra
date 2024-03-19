package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens file in Mebis Tafel.
 */
public class OpenFileActionMebis extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(final AppWFull app) {
		if (isLoggedOut(app)) {
			app.getActivity().markSearchOpen();
			// no listening for login needed, will get redirected
			app.getLoginOperation().showLoginDialog();
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