package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

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
	 * @param forceFade
	 *            force fade during animation
	 * @return algebra input focused
	 */
	boolean focusInput(boolean force, boolean forceFade);

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

	/**
	 * Append an altText of a view to read.
	 * @param altText of a view.
	 */
	void appendAltText(GeoText altText);

	/**
	 *
	 * @param geo to check
	 * @return if geo is a dependency of the Alt Text of a view.
	 */
	boolean isIndependentFromAltTexts(GeoNumeric geo);

	/**
	 * Add geo as alt text dependency, so its value can be read
	 * along with the alt text
	 * @param geo to add.
	 */
	void addAsAltTextDependency(GeoNumeric geo);
}
