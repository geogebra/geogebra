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
