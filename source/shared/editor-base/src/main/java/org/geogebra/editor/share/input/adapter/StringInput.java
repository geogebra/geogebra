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

import org.geogebra.editor.share.controller.EditorState;
import org.geogebra.editor.share.controller.InputController;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.input.KeyboardInputAdapter;

public abstract class StringInput implements KeyboardAdapter {

    private String keyboard;

    public StringInput() {
        this(null);
    }

    public StringInput(String keyboard) {
        this.keyboard = keyboard;
    }

    @Override
    public boolean test(String input) {
        return keyboard.equals(input);
    }

    protected static boolean plainTextMode(MathFieldInternal mfi) {
        return mfi.getInputController().getPlainTextMode()
                || mfi.getEditorState().isInsideQuotes();
    }

    protected static void commitFunction(MathFieldInternal mfi, String function) {
        if (plainTextMode(mfi)) {
            KeyboardInputAdapter.type(mfi, function + "()");
        } else {
            EditorState editorState = mfi.getEditorState();
            InputController inputController = mfi.getInputController();
            inputController.newFunction(editorState, function);
			mfi.notifyAndUpdate(function);
        }
    }
}
