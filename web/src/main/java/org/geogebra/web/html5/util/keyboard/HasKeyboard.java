package org.geogebra.web.html5.util.keyboard;

import org.geogebra.common.main.KeyboardLocale;

public interface HasKeyboard {
	
	void updateKeyboardHeight();

	double getHeight();

	double getWidth();

	KeyboardLocale getLocalization();

}
