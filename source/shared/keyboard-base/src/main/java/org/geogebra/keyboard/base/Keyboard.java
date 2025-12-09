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

package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.listener.KeyboardObserver;
import org.geogebra.keyboard.base.model.KeyboardModel;

/**
 * An internal keyboard controller.
 */
public interface Keyboard {

    /**
     * The type (or internal name) of this keyboard.
     *
     * @return the keyboard type
     */
    KeyboardType getType();

    /**
     * Returns the keyboard model. Controllers should use this
     * to build and refresh the keyboard view.
     *
     * @return the model
     */
    KeyboardModel getModel();

    /**
     * Register a keyboard observer to refresh the view.
     *
     * @param observer keyboard observer
     */
    void registerKeyboardObserver(KeyboardObserver observer);

    /**
     * Toggle the accent on/off.
     *
     * @param accent one of {@link Accents} with the prefix <i>ACCENT</i>.
     */
    void toggleAccent(String accent);

    /**
     * Disable the caps lock.
     */
    void disableCapsLock();

    /**
     * Toggle the caps lock on/off.
     */
    void toggleCapsLock();
}
