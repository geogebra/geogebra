package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.listener.KeyboardObserver;

public interface Keyboard {

    LinearKeyboard getModel();

    void registerKeyboardObserver(KeyboardObserver observer);

    void toggleAccent(String accent);

    void disableCapsLock();

    void toggleCapsLock();
}
