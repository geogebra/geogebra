package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;

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
