package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.UIObject;

public final class ToolboxPopupPositioner {

	private final static int TOOLBOX_PADDING = 8;

	/**
	 * Show and position a popup relative to the toolbox button
	 * @param popup popup to be shown
	 * @param anchor element used for vertical positioning
	 * @param app provides bounds for horizontal position
	 */
	public static void showRelativeToToolbox(GPopupPanel popup, UIObject anchor, AppW app) {
		closePopupsRegisterNewPopup(popup, app);

		int left = (int) (anchor.getAbsoluteLeft() + anchor.getOffsetWidth()
						+ TOOLBOX_PADDING - app.getAbsLeft());
		int top = (int) (anchor.getAbsoluteTop() - app.getAbsTop());

		popup.setPopupPosition(left, (int) (top / app.getGeoGebraElement().getScaleY()));
		popup.show();
	}

	private static void closePopupsRegisterNewPopup(GPopupPanel popup, AppW app) {
		app.closePopups();
		app.registerPopup(popup);
	}
}
