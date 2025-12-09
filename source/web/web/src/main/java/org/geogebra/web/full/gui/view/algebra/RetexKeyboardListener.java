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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.editor.share.util.AltKeys;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.Widget;

/**
 * ReTeX connector for keyboard
 */
public class RetexKeyboardListener implements MathKeyboardListener {

	private Canvas canvas;
	private MathFieldW mf;
	private boolean acceptsCommandInserts;

	/**
	 * @param canvas
	 *            canvas
	 * @param mf
	 *            math input field
	 */
	public RetexKeyboardListener(Canvas canvas, MathFieldW mf) {
		this.canvas = canvas;
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus) {
		// canvas.setFocus(focus);
		mf.setFocus(focus);
	}

	@Override
	public void ensureEditing() {
		mf.requestViewFocus();

	}

	@Override
	public Widget asWidget() {
		return canvas;
	}

	/**
	 * @return math input field
	 */
	public MathFieldW getMathField() {
		return mf;
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public boolean hasFocus() {
		return getMathField().hasFocus();
	}

	/**
	 * @param unicodeKeyChar
	 *            code
	 * @param shift
	 *            whether shift is pressed also
	 * @return sequence for alt+code
	 */
	public String alt(int unicodeKeyChar, boolean shift) {
		return AltKeys.getAltSymbols(unicodeKeyChar, shift, true);
	}

	/**
	 * @return Whether the commands can be inserted into this text field
	 * from the keyboard's More button.
	 */
	@Override
	public boolean acceptsCommandInserts() {
		return acceptsCommandInserts;
	}

	/**
	 * @param acceptsCommandInserts Whether the commands can be inserted into this text field
	 *                              from the keyboard's More button.
	 */
	public void setAcceptsCommandInserts(boolean acceptsCommandInserts) {
		this.acceptsCommandInserts = acceptsCommandInserts;
	}
}
