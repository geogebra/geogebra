package org.geogebra.common.gui;

import org.geogebra.common.main.App;

/**
 * Groups of objects accessible by tabbing.
 */
public enum AccessibilityGroup {
	MENU, GEOGEBRA_LOGO, SUBAPP_CHOOSER, SHARE, ASSIGN, SIGN_IN_TEXT, SIGN_IN_ICON, AVATAR, UNDO,
	REDO, ALGEBRA_CLOSE, UNDO_GRAPHICS, REDO_GRAPHICS, ZOOM_NOTES_PLUS, ZOOM_NOTES_MINUS,
	ZOOM_NOTES_HOME, ZOOM_NOTES_TO_FIT, ZOOM_NOTES_DRAG_VIEW, FULL_SCREEN_NOTES, SETTINGS_NOTES,
	EV_CONTROLS, EV2_CONTROLS, EV3D_CONTROLS, PAGE_LIST_OPEN,
	ALT_GEOTEXT, GEO_ELEMENT, EXTERNAL, ALGEBRA_ITEM;

	/**
	 * Controls in a single view.
	 */
	public enum ViewControlId {
		ALT_GEO,
		RESET_BUTTON,
		SETTINGS_BUTTON,
		ZOOM_NOTES_SPOTLIGHT, PLAY_BUTTON, ZOOM_PANEL_HOME,
		ZOOM_PANEL_PLUS, ZOOM_PANEL_MINUS, FULL_SCREEN
	}

	/**
	 * @param viewId view ID
	 * @return accessibility group for view controls
	 */
	public static AccessibilityGroup getViewGroup(int viewId) {
		switch (viewId) {
			case App.VIEW_EUCLIDIAN3D:
				return AccessibilityGroup.EV3D_CONTROLS;
			case App.VIEW_EUCLIDIAN2:
				return AccessibilityGroup.EV2_CONTROLS;
			default:
				return AccessibilityGroup.EV_CONTROLS;
		}
	}
}
