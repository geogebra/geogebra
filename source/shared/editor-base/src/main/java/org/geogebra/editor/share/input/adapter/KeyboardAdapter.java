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
