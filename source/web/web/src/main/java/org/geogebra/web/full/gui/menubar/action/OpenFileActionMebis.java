/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Opens file in Mebis Board.
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