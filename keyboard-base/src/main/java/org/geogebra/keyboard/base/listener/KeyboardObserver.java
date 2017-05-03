package org.geogebra.keyboard.base.listener;

import org.geogebra.keyboard.base.Keyboard;

/**
 * Classes implementing this can register themselves to observe keyboard model changes in
 * {@link Keyboard#registerKeyboardObserver(KeyboardObserver)} such as caps lock or accent toggle.
 * Controllers implementing this should refresh the view from the model {@link Keyboard#getModel()}.
 * Internal use only.
 */
public interface KeyboardObserver {

    void keyboardModelChanged(Keyboard keyboard);
}
