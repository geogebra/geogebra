package org.geogebra.web.shared;

import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;

/**
 * Helper for share / save dialog.
 *
 */
public class DialogUtil {

	/**
	 * @param app
	 *            app
	 * @param listener
	 *            popup to be hidden on logout
	 */
	public static void hideOnLogout(AppW app, final GPopupPanel listener) {
		if (app.getLoginOperation() == null) {
			return;
		}
		app.getLoginOperation().getView().add(event -> {
			if (event instanceof LogOutEvent) {
				listener.hide();
			}
		});
	}
}
