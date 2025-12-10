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

package org.geogebra.common.gui;

import org.geogebra.common.main.App;

/**
 * Groups of objects accessible by tabbing.
 */
public enum AccessibilityGroup {
	MENU, GEOGEBRA_LOGO, SUBAPP_CHOOSER, SHARE, ASSIGN, SIGN_IN_TEXT, SIGN_IN_ICON, AVATAR,
	UNDO, REDO, ALGEBRA_CLOSE, UNDO_GRAPHICS, REDO_GRAPHICS,
	SETTINGS_CLOSE_BUTTON, SETTINGS_TAB_BUTTON, SETTINGS_ITEM,
	ZOOM_NOTES_PLUS, ZOOM_NOTES_MINUS,
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
		SETTINGS_VIEW,
		ZOOM_NOTES_SPOTLIGHT, PLAY_BUTTON,
		ZOOM_PANEL_HOME, ZOOM_PANEL_ZOOM_TO_FIT,
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
