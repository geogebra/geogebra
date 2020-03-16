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
	public void setAnchor(Object anchor);

	/**
	 * Get the anchor.
	 * 
	 * @return anchor to give back the focus.
	 */
	public Object getAnchor();

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

	/**
	 * Called if tab selection leaves the first or last geo.
	 * 
	 * @param forward
	 *            true for TAB, false for Shift+TAB
	 * @return true if the situation is handled here.
	 */
	boolean handleTabExitGeos(boolean forward);

	/**
	 * 
	 * @param forward
	 *            true for TAB, false for Shift+TAB
	 * @return true if tab selection leaves EV controls (animation / rotate
	 *         view).
	 */
	public boolean tabEuclidianControl(boolean forward);

	/**
	 * Selects/unselects EV play if available.
	 * 
	 * @param b
	 *            to set.
	 * @param viewID
	 *            ID of the view with play button
	 */
	public void setPlaySelectedIfVisible(boolean b, int viewID);

	/**
	 * Handle slider change
	 * 
	 * @param step
	 *            slider increment (may be negative)
	 * @param input
	 *            type of slider action
	 */
	public void sliderChange(double step, SliderInput input);

	/**
	 * Called when user press tab but construction is empty.
	 *
	 * @param forward
	 *            if geo is selected by tab or shift+tab.
	 */
	public void onEmptyConstuction(boolean forward);

	/**
	 * @param geo
	 *            element associated with the action
	 * @return action description (eg Run script)
	 */
	public String getAction(GeoElement geo);

	void register(MayHaveFocus focusable);

	void setTabOverGeos();
}
