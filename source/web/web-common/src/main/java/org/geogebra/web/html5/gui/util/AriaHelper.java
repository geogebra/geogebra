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
	 * @param ui UI element
	 * @param title title
	 */
	public static void setTitle(UIObject ui, String title) {
		setDataTitle(ui, title);
		ui.getElement().removeAttribute("title");
		ui.getElement().setAttribute("aria-label", title);
	}

	/**
	 * Remove aria-label and data-title
	 * @param ui UI element
	 */
	public static void removeTitle(UIObject ui) {
		ui.getElement().removeAttribute("data-title");
		ui.getElement().removeAttribute("aria-label");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget widget to be hidden
	 */
	public static void hide(UIObject widget) {
		widget.getElement().setAttribute("aria-hidden", "true");
	}

	/**
	 * Adds aria-hidden to given element
	 * 
	 * @param widget widget to be hidden
	 * @param hidden whether to hide it
	 */
	public static void setHidden(UIObject widget, boolean hidden) {
		widget.getElement().setAttribute("aria-hidden", String.valueOf(hidden));
	}

	/**
	 * @param uiObject element
	 * @param label localized string
	 */
	public static void setLabel(UIObject uiObject, String label) {
		setNullableAttribute(uiObject, "label", label);
	}

	/**
	 * @param uiObject element
	 * @param attribute the attribute to be set
	 * @param value localized string
	 */
	public static void setAttribute(UIObject uiObject, String attribute, String value) {
		uiObject.getElement().setAttribute(attribute, value);
	}

	/**
	 * @param uiObject element
	 * @param role role, e.g. radio or checkbox
	 */
	public static void setRole(UIObject uiObject, String role) {
		uiObject.getElement().setAttribute("role", role);
	}

	/**
	 * @param uiObject UI element
	 * @param autocomplete autocomplete
	 */
	public static void setAutocomplete(UIObject uiObject, String autocomplete) {
		uiObject.getElement().setAttribute("aria-autocomplete", autocomplete);
	}

	/**
	 * @param uiObject element
	 * @param checked true or false
	 */
	public static void setChecked(UIObject uiObject, boolean checked) {
		uiObject.getElement().setAttribute("aria-checked", String.valueOf(checked));
	}

	/**
	 * We need this for radio buttons, in order to have the screen reader say radio button 2 of 3
	 * (where the radio panel containing 3 radio buttons and the second is focused)
	 *
	 * @param uiObject element
	 * @param position position of radio button
	 * @param size size of radio panel
	 */
	public static void setPositionInfo(UIObject uiObject, String position, String size) {
		uiObject.getElement().setAttribute("aria-posinset", position);
		uiObject.getElement().setAttribute("aria-setsize", size);
	}

	/**
	 * @param uiObject element
	 * @param tabIndex tab index
	 */
	public static void setTabIndex(UIObject uiObject, int tabIndex) {
		uiObject.getElement().setAttribute("tabindex", tabIndex + "");
	}

	/**
	 * @param uiObject element
	 * @param pressed true or false
	 */
	public static void setPressedState(UIObject uiObject, boolean pressed) {
		uiObject.getElement().setAttribute("aria-pressed", String.valueOf(pressed));
	}

	/**
	 * @param uiObject element
	 * @param disabled true or false
	 */
	public static void setAriaDisabled(UIObject uiObject, boolean disabled) {
		uiObject.getElement().setAttribute("aria-disabled", String.valueOf(disabled));
	}

	/**
	 * @param uiObject element
	 * @param draggable true or false
	 */
	public static void setDraggable(UIObject uiObject, boolean draggable) {
		uiObject.getElement().setAttribute("draggable", String.valueOf(draggable));
	}

	/**
	 * @param uiObject element
	 * @param title title
	 */
	public static void setDataTitle(UIObject uiObject, String title) {
		if (!NavigatorUtil.isMobile()) {
			uiObject.getElement().setAttribute("data-title", title);
		}
	}

	/**
	 * @param uiObject element
	 */
	public static void setAriaHasPopup(UIObject uiObject) {
		uiObject.getElement().setAttribute("aria-haspopup", "true");
	}

	/**
	 * @param uiObject element
	 * @param expanded true or false
	 */
	public static void setAriaExpanded(UIObject uiObject, boolean expanded) {
		uiObject.getElement().setAttribute("aria-expanded", String.valueOf(expanded));
	}

	/**
	 * @param uiObject element
	 * @param selected true or false
	 */
	public static void setAriaSelected(UIObject uiObject, boolean selected) {
		uiObject.getElement().setAttribute("aria-selected", String.valueOf(selected));
	}

	/**
	 * @param uiObject element
	 * @param alt text description
	 */
	public static void setAlt(UIObject uiObject, String alt) {
		uiObject.getElement().setAttribute("alt", alt);
	}

	/**
	 * @param uiObject element
	 * @param value aria-haspopup value
	 */
	public static void setAriaHaspopup(UIObject uiObject, String value) {
		uiObject.getElement().setAttribute("aria-haspopup", value);
	}

	/**
	 * @param uiObject element
	 */
	public static void setAriaHidden(UIObject uiObject) {
		uiObject.getElement().setAttribute("aria-hidden", "true");
	}

	/**
	 * @param uiObject UI element
	 * @param controlsID ID of related controls
	 */
	public static void setControls(UIObject uiObject, String controlsID) {
		uiObject.getElement().setAttribute("aria-controls", controlsID);
	}

	/**
	 * @param uiObject UI element
	 * @param id id of active descendant, null to reset
	 */
	public static void setActiveDescendant(UIObject uiObject, String id) {
		setNullableAttribute(uiObject, "activedescendant", id);
	}

	/**
	 * @param uiObject UI element
	 * @param message error message (null to reset)
	 */
	public static void setErrorMessage(UIObject uiObject, String message) {
		setNullableAttribute(uiObject, "errormessage", message);
		uiObject.getElement().setAttribute("aria-invalid", String.valueOf(message != null));
	}

	private static void setNullableAttribute(UIObject uiObject, String name, String value) {
		if (value == null) {
			uiObject.getElement().removeAttribute("aria-" + name);
		} else {
			uiObject.getElement().setAttribute("aria-" + name, value);
		}
	}

	/**
	 * Toggle the button between enabled and disabled
	 * Changes "disabled" property in DOM, so use :disabled in css
	 * @param uiObject UI element
	 * @param disabled whether to remove or add the "disabled" property
	 */
	public static void setDisabled(UIObject uiObject, boolean disabled) {
		if (disabled) {
			uiObject.getElement().setAttribute("disabled", "disabled");
		} else {
			uiObject.getElement().removeAttribute("disabled");
		}
	}
}
