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

package org.geogebra.keyboard.base.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;

public class KeyboardImpl implements Keyboard {

    private final KeyboardType type;
    private final Supplier<KeyboardModel> modelSupplier;
    private KeyboardModel model;
    private final CapsLockModifier capsLockModifier;
    private final AccentModifier accentModifier;

    private final List<KeyboardObserver> observers = new ArrayList<>();

	/**
     * @param type
     *            the keyboard type (ABC, numeric, ...)
	 * @param model
	 *            the keyboard model (as rows of buttons)
	 * @param capsLockModifier
	 *            caps lock modifier
	 * @param accentModifier
	 *            accent modifier
	 */
	public KeyboardImpl(KeyboardType type,
            Supplier<KeyboardModel> model,
            CapsLockModifier capsLockModifier,
			AccentModifier accentModifier) {
        this.type = type;
        this.modelSupplier = model;
        this.capsLockModifier = capsLockModifier;
        this.accentModifier = accentModifier;
    }

    @Override
    public KeyboardType getType() {
        return type;
    }

    @Override
    public KeyboardModel getModel() {
        if (model == null) {
            model = modelSupplier.get();
        }
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
