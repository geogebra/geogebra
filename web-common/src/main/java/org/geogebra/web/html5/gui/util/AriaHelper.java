package org.geogebra.web.html5.gui.util;

import org.geogebra.gwtutil.NavigatorUtil;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Helper class for accessibility methods
 */
public class AriaHelper {

	/**
	 * Avoid setting title (so that screen reader only reads the image alt) Set
	 * aria-label for desktop screen reader and data-title for visual tooltips.
	 *
	 * @param ui
	 *            UI element
	 * @param title
	 *            title
	 */
	public static void setTitle(UIObject ui, String title) {
		if (!NavigatorUtil.isMobile()) {
			ui.getElement().setAttribute("data-title", title);
		}
		ui.getElement().removeAttribute("title");
		ui.getElement().setAttribute("aria-label", title);
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget
	 *            widget to be hidden
	 */
	public static void hide(UIObject widget) {
		widget.getElement().setAttribute("aria-hidden", "true");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget
	 *            widget to be hidden
	 * @param hidden
	 *            whether to hide it
	 */
	public static void setHidden(UIObject widget, boolean hidden) {
		widget.getElement().setAttribute("aria-hidden", String.valueOf(hidden));
	}

	/**
	 * @param uiObject
	 *            element
	 * @param label
	 *            localized string
	 */
	public static void setLabel(UIObject uiObject, String label) {
		uiObject.getElement().setAttribute("aria-label", label);
	}

	/**
	 * @param uiObject
	 *            element
	 * @param attribute
	 *            the attribute to be set
	 * @param value
	 *            localized string
	 */
	public static void setAttribute(UIObject uiObject, String attribute, String value) {
		uiObject.getElement().setAttribute(attribute, value);
	}

	/**
	 * @param uiObject - element
	 * @param role - role, e.g. radio or checkbox
	 */
	public static void setRole(UIObject uiObject, String role) {
		uiObject.getElement().setAttribute("role", role);
	}

	/**
	 * @param uiObject - element
	 * @param checked - true or false
	 */
	public static void setChecked(UIObject uiObject, String checked) {
		uiObject.getElement().setAttribute("aria-checked", checked);
	}

	/**
	 * We need this for radio buttons, in order to have the screen reader say radio button 2 of 3
	 * (where the radio panel containing 3 radio buttons and the second is focused)
	 *
	 * @param uiObject - element
	 * @param position - position of radio button
	 * @param size - size of radio panel
	 */
	public static void setPositionInfo(UIObject uiObject, String position, String size) {
		uiObject.getElement().setAttribute("aria-posinset", position);
		uiObject.getElement().setAttribute("aria-setsize", size);
	}

	/**
	 * @param uiObject - element
	 * @param tabIndex - tab index
	 */
	public static void setTabIndex(UIObject uiObject, int tabIndex) {
		uiObject.getElement().setAttribute("tabindex", tabIndex + "");
	}
}
