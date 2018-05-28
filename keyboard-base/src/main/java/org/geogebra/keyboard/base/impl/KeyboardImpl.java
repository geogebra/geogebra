package org.geogebra.keyboard.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;

public class KeyboardImpl implements Keyboard {

    private KeyboardModel model;
    private CapsLockModifier capsLockModifier;
    private AccentModifier accentModifier;

    private List<KeyboardObserver> observers = new ArrayList<>();

	/**
	 * @param model
	 *            model
	 * @param capsLockModifier
	 *            caps lock modifier
	 * @param accentModifier
	 *            accent modifier
	 */
	public KeyboardImpl(KeyboardModel model, CapsLockModifier capsLockModifier,
			AccentModifier accentModifier) {
        this.model = model;
        this.capsLockModifier = capsLockModifier;
        this.accentModifier = accentModifier;
    }

    @Override
    public KeyboardModel getModel() {
        return model;
    }

    @Override
    public void registerKeyboardObserver(KeyboardObserver observer) {
        observers.add(observer);
    }

    private void fireKeyboardModelChanged() {
        for (KeyboardObserver observer : observers) {
            observer.keyboardModelChanged(this);
        }
    }

    @Override
    public void toggleAccent(String accent) {
        if (accentModifier != null) {
            boolean changed = accentModifier.toggleAccent(accent);
            if (changed) {
                fireKeyboardModelChanged();
            }
        }
    }

    @Override
    public void toggleCapsLock() {
        if (capsLockModifier != null) {
            capsLockModifier.toggleCapsLock();
            fireKeyboardModelChanged();
        }
    }

    @Override
    public void disableCapsLock() {
        if (capsLockModifier != null) {
            boolean changed = capsLockModifier.disableCapsLock();
            if (changed) {
                fireKeyboardModelChanged();
            }
        }
    }
}
