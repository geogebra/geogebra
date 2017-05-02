package org.geogebra.web.web.gui.util;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboardW;

public interface VirtualKeyboardGUI extends VirtualKeyboardW {

	void updateSize();

	void setStyleName();

	void endEditing();

	void setProcessing(KeyboardListener makeKeyboardListener);

	void setListener(UpdateKeyBoardListener listener);

	void remove(Runnable runnable);

}
