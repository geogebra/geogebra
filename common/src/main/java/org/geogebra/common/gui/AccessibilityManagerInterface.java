package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * @author Laszlo
 *
 */
public interface AccessibilityManagerInterface {

	/**
	 * Focus next screen element
	 * @return if there was a next element to focus
	 */
	boolean focusNext();

	/**
	 * Focus previous screen element
	 * @return if there was a previous element to focus
	 */
	boolean focusPrevious();

	/** Focus first interactive element (widget or drawable) */
	void focusFirstElement();

	/**
	 * Focus algebra input
	 * 
	 * @param force
	 *            force to open AV tab if not active
	 * 
	 * @return algebra input focused
	 */
	boolean focusInput(boolean force);

	/**
	 * Focus the geo specified.
	 * 
	 * @param geo
	 *            to focus.
	 */
	void focusGeo(GeoElement geo);

	/**
	 * Use this method to set an anchor you like to give the focus back to. For
	 * example setting a button as a focus anchor that opens a popup, closing it
	 * can give back the focus to the button.
	 * 
	 * @param anchor
	 *            to give back the focus.
	 */
	void setAnchor(MayHaveFocus anchor);

	/**
	 * Get the anchor.
	 * 
	 * @return anchor to give back the focus.
	 */
	MayHaveFocus getAnchor();

	/**
	 * Give back the focus to the anchor if set.
	 */
	void focusAnchor();

	/**
	 * Clears the anchor.
	 */
	void cancelAnchor();

	/**
	 * Give back the focus to the anchor if set otherwise give the focus to
	 * menu.
	 */
	void focusAnchorOrMenu();

	void register(MayHaveFocus focusable);

	void setTabOverGeos();
}
