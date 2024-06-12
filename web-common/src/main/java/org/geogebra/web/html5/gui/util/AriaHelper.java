package org.geogebra.web.html5.gui.util;

import org.geogebra.gwtutil.NavigatorUtil;
import org.gwtproject.user.client.ui.UIObject;

/**
 * Helper class for accessibility methods
 */
public class AriaHelper {

	/**
	 * Avoid setting title (so that screen reader only reads the image alt) Set
	 * aria-label for desktop screen reader and data-title for visual tooltips.
	 *
	 * @param ui - UI element
	 * @param title - title
	 */
	public static void setTitle(UIObject ui, String title) {
		setDataTitle(ui, title);
		ui.getElement().removeAttribute("title");
		ui.getElement().setAttribute("aria-label", title);
	}

	/**
	 * Remove aria-label and data-title
	 * @param ui - UI element
	 */
	public static void removeTitle(UIObject ui) {
		ui.getElement().removeAttribute("data-title");
		ui.getElement().removeAttribute("aria-label");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget - widget to be hidden
	 */
	public static void hide(UIObject widget) {
		widget.getElement().setAttribute("aria-hidden", "true");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget - widget to be hidden
	 * @param hidden - whether to hide it
	 */
	public static void setHidden(UIObject widget, boolean hidden) {
		widget.getElement().setAttribute("aria-hidden", String.valueOf(hidden));
	}

	/**
	 * @param uiObject - element
	 * @param label - localized string
	 */
	public static void setLabel(UIObject uiObject, String label) {
		uiObject.getElement().setAttribute("aria-label", label);
	}

	/**
	 * @param uiObject - element
	 * @param attribute - the attribute to be set
	 * @param value - localized string
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
	public static void setChecked(UIObject uiObject, boolean checked) {
		uiObject.getElement().setAttribute("aria-checked", String.valueOf(checked));
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

	/**
	 * @param uiObject - element
	 * @param pressed - true or false
	 */
	public static void setPressedState(UIObject uiObject, boolean pressed) {
		uiObject.getElement().setAttribute("aria-pressed", String.valueOf(pressed));
	}

	/**
	 * @param uiObject - element
	 * @param disabled - true or false
	 */
	public static void setAriaDisabled(UIObject uiObject, boolean disabled) {
		uiObject.getElement().setAttribute("aria-disabled", String.valueOf(disabled));
	}

	/**
	 * @param uiObject - element
	 * @param draggable - true or false
	 */
	public static void setDraggable(UIObject uiObject, boolean draggable) {
		uiObject.getElement().setAttribute("draggable", String.valueOf(draggable));
	}

	/**
	 * @param uiObject - element
	 * @param title - title
	 */
	public static void setDataTitle(UIObject uiObject, String title) {
		if (!NavigatorUtil.isMobile()) {
			uiObject.getElement().setAttribute("data-title", title);
		}
	}

	/**
	 * @param uiObject - element
	 */
	public static void setAriaHasPopup(UIObject uiObject) {
		uiObject.getElement().setAttribute("aria-haspopup", "true");
	}

	/**
	 * @param uiObject - element
	 * @param expanded - true or false
	 */
	public static void setAriaExpanded(UIObject uiObject, boolean expanded) {
		uiObject.getElement().setAttribute("aria-expanded", String.valueOf(expanded));
	}
}
