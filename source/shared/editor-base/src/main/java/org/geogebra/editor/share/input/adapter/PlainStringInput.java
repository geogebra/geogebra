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
import org.geogebra.editor.share.input.KeyboardInputAdapter;

public class PlainStringInput extends StringInput {

	private String input;

	/**
	 * @param keyboard
	 *            keyboard string
	 * @param input
	 *            to be added to editor
	 */
	public PlainStringInput(String keyboard, String input) {
		super(keyboard);
		this.input = input;
	}

	@Override
	public void commit(MathFieldInternal mfi, String unused) {
		boolean old = mfi.getInputController().getPlainTextMode();
		mfi.setPlainTextMode(true);
		KeyboardInputAdapter.type(mfi, this.input);
		mfi.setPlainTextMode(old);
	}
}
