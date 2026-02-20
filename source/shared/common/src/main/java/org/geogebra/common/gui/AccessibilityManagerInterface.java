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

import org.geogebra.common.gui.compositefocus.FocusableComposite;
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
	void setAnchor(FocusableComponent anchor);

	/**
	 * Get the anchor.
	 * 
	 * @return anchor to give back the focus.
	 */
	FocusableComponent getAnchor();

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

	/**
	 * Register a focusable element.
	 * @param focusable focusable element
	 */
	void register(FocusableComponent focusable);

	/**
	 * Unregister a focusable element.
	 * @param focusable focusable element
	 */
	void unregister(FocusableComponent focusable);

	/**
	 * Start tabbing over construction elements.
	 */
	void setTabOverGeos();

	/**
	 * Read slider update.
	 * @param geo slider
	 */
	void readSliderUpdate(GeoNumeric geo);

	/**
	 * @param altText to append
	 */
	void appendAltText(GeoText altText);

	/**
	 * Cancels reading the view alt texts
	 */
	void cancelReadCollectedAltTexts();

	/**
	 * Preloads an alt text so it is only read upon change or selection
	 * @param geoText {@link GeoText}
	 */
	void preloadAltText(GeoText geoText);

	/**
	 * Registers a composite focus container for participation in composite focus traversal.
	 *
	 * @param compositeFocus the composite focus container to register
	 */
	void registerCompositeFocusContainer(FocusableComposite compositeFocus);

	/**
	 * Unregisters a previously registered composite focus container.
	 *
	 * @param compositeFocus the composite focus container to unregister
	 */
	void unregisterCompositeFocusContainer(FocusableComposite compositeFocus);

	/**
	 * @return {@code true} if the currently active composite has internal focus;
	 *         {@code false} otherwise
	 */
	boolean hasFocusInComposite();

	/**
	 * Moves focus to the next focusable part within the currently focused element.
	 *
	 * <p>This is used for internal navigation of composite elements (for example,
	 * the input row, output row, toggle, or menu buttons of an Algebra View item).
	 * Global focus traversal between items is handled separately (e.g. via Tab).</p>
	 *
	 * @return true if a next internal part exists; false otherwise
	 */
	boolean focusNextInComposite();

	/**
	 * Moves focus to the previous focusable part within the currently focused element.
	 *
	 * <p>This is used for internal navigation of composite elements (for example,
	 * the input row, output row, toggle, or menu buttons of an Algebra View item).</p>
	 *
	 * @return true if a previous internal part exists; false otherwise
	 */
	boolean focusPreviousInComposite();

	/**
	 * Clears any active composite focus.
	 *
	 */
	void blurCompositeFocus();

	/**
	 * @return whether native focus should be applied while a composite focus is active.
	 */
	boolean handlesEnterInComposite();

}
