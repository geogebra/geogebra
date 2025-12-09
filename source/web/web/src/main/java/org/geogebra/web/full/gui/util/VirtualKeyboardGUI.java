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

package org.geogebra.web.full.gui.util;

import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;
import org.gwtproject.dom.client.Element;

/**
 * Extended virtual keyboard interface.
 */
public interface VirtualKeyboardGUI extends VirtualKeyboardW {

	/**
	 * Update style name.
	 */
	void setStyleName();

	/**
	 * Set component processing the pressed keys.
	 * @param makeKeyboardListener keyboard listener
	 */
	void setProcessing(KeyboardListener makeKeyboardListener);

	/**
	 * Set the close event listener.
	 * @param listener close listener
	 */
	void setListener(KeyboardCloseListener listener);

	/**
	 * Hide the keyboard.
	 * @param runnable called when (animated) hiding is finished.
	 */
	void remove(Runnable runnable);

	/**
	 * Update keys for current localization.
	 */
	void checkLanguage();

	/**
	 * @param popup
	 *            popup that should not close when clicking the keyboard
	 */
	void addAutoHidePartner(GPopupPanel popup);

	/**
	 * @param type
	 *            selected tab
	 */
	void selectTab(KeyboardType type);

	/**
	 * @return DOM element
	 */
	Element getElement();

	/**
	 * rebuild the keyboard layout
	 */
	void clearAndUpdate();

	/**
	 * Remove classes that trigger animation events.
	 */
	void finishAnimation();
}
