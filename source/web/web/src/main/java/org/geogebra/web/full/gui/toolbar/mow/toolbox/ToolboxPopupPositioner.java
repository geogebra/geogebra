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

package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.UIObject;

public final class ToolboxPopupPositioner {
	private final static int TOOLBOX_PADDING = 8;
	private final static int DISTANCE_FROM_TOOLBOX = 8;

	/**
	 * Show and position a popup relative to the toolbox button
	 * @param popup popup to be shown
	 * @param anchor element used for vertical positioning
	 * @param app provides bounds for horizontal position
	 */
	public static void showRelativeToToolbox(GPopupPanel popup, UIObject anchor, AppW app) {
		closePopupsRegisterNewPopup(popup, app);

		int left = (int) (anchor.getAbsoluteLeft() + anchor.getOffsetWidth()
						+ TOOLBOX_PADDING + DISTANCE_FROM_TOOLBOX - app.getAbsLeft());
		int top = (int) (anchor.getAbsoluteTop() - app.getAbsTop());

		popup.setPopupPosition(left, (int) (top / app.getGeoGebraElement().getScaleY()));
		popup.show();
	}

	private static void closePopupsRegisterNewPopup(GPopupPanel popup, AppW app) {
		app.closePopups();
		app.registerPopup(popup);
	}
}
