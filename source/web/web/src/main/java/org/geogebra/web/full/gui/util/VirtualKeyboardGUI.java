package org.geogebra.web.full.gui.util;

import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;
import org.gwtproject.dom.client.Element;

public interface VirtualKeyboardGUI extends VirtualKeyboardW {

	void setStyleName();

	void endEditing();

	void setProcessing(KeyboardListener makeKeyboardListener);

	void setListener(KeyboardCloseListener listener);

	void remove(Runnable runnable);

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
}
