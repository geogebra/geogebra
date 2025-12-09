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
import org.geogebra.editor.share.input.KeyboardInputAdapter;

public class StringAdapter extends StringInput {

    private final String input;

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
    public StringAdapter(char keyboard, String input) {
        this(keyboard + "", input);
    }

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
    public StringAdapter(String keyboard, String input) {
        super(keyboard);
        this.input = input;
    }

    @Override
	public void commit(MathFieldInternal mfi, String unused) {
        KeyboardInputAdapter.type(mfi, this.input);
    }
}
