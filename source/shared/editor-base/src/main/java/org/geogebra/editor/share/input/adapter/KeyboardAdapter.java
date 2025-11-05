/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.input.adapter;

import org.geogebra.editor.share.editor.MathFieldInternal;

/**
 * Handles input sequences that can be produced by a single key press on virtual keyboard.
 * One or more keys may use the same KeyboardAdapter.
 */
public interface KeyboardAdapter {

    /**
     * Change the state of the formula input field based on the input sequence.
     * @param mfi math field
     * @param input input sequence
     */
    void commit(MathFieldInternal mfi, String input);

    /**
     * @param keyboard keyboard input sequence
     * @return whether this can handle given input sequence
     */
    boolean test(String keyboard);
}
