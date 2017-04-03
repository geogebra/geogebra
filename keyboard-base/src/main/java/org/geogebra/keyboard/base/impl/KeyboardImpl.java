package org.geogebra.keyboard.base.impl;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.AccentModifier;
import org.geogebra.keyboard.base.linear.impl.CapsLockModifier;
import org.geogebra.keyboard.base.listener.KeyboardObserver;

import java.util.ArrayList;
import java.util.List;

public class KeyboardImpl implements Keyboard {

    private LinearKeyboard model;
    private CapsLockModifier capsLockModifier;
    private AccentModifier accentModifier;

    private List<KeyboardObserver> observers = new ArrayList<>();

    public KeyboardImpl(LinearKeyboard model, CapsLockModifier capsLockModifier, AccentModifier accentModifier) {
        this.model = model;
        this.capsLockModifier = capsLockModifier;
        this.accentModifier = accentModifier;
    }

    @Override
    public LinearKeyboard getModel() {
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
            boolean changed = accentModifier.setAccent(accent);
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
