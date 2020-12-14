package org.geogebra.web.full.gui.keyboard;

import org.geogebra.keyboard.web.HasKeyboard;

public class InputBoxTabbedKeyboard extends OnscreenTabbedKeyboard {

	/**
	 * @param appKeyboard {@link HasKeyboard}
	 */
	public InputBoxTabbedKeyboard(HasKeyboard appKeyboard) {
		super(appKeyboard, false);
		//buildGUI(false);
	}

}
