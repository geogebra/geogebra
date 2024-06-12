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
		popup.setPopupPosition((int) (anchor.getAbsoluteLeft() + anchor.getOffsetWidth()
						+ TOOLBOX_PADDING - app.getAbsLeft()),
				(int) (anchor.getAbsoluteTop() - app.getAbsTop()));
		popup.show();
	}
}
