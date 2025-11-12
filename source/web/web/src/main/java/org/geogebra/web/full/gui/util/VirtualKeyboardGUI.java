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
