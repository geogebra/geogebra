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
	 */
	void focusNext();

	/**
	 * Focus previous screen element
	 */
	void focusPrevious();

	/** Focus first interactive element (widget or drawable) */
	public void focusFirstElement();

	/**
	 * Focus algebra input
	 * 
	 * @param force
	 *            force to open AV tab if not active
	 * 
	 * @return algebra input focused
	 */
	public boolean focusInput(boolean force);

	/**
	 * Focus the geo specified.
	 * 
	 * @param geo
	 *            to focus.
	 */
	public void focusGeo(GeoElement geo);

	/**
	 * Use this method to set an anchor you like to give the focus back to. For
	 * example setting a button as a focus anchor that opens a popup, closing it
	 * can give back the focus to the button.
	 * 
	 * @param anchor
	 *            to give back the focus.
	 */
	public void setAnchor(MayHaveFocus anchor);

	/**
	 * Get the anchor.
	 * 
	 * @return anchor to give back the focus.
	 */
	public MayHaveFocus getAnchor();

	/**
	 * Give back the focus to the anchor if set.
	 */
	public void focusAnchor();

	/**
	 * Clears the anchor.
	 */
	public void cancelAnchor();

	/**
	 * Give back the focus to the anchor if set otherwise give the focus to
	 * menu.
	 */
	void focusAnchorOrMenu();

	void register(MayHaveFocus focusable);

	void setTabOverGeos();
}
