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

package org.geogebra.web.editor;

import org.geogebra.editor.share.event.KeyEvent;
import org.geogebra.editor.share.input.KeyboardInputAdapter;
import org.geogebra.editor.share.util.JavaKeyCodes;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.keyboard.web.KeyboardListener;

/**
 * Virtual keyboard bindings for ReTeX
 */
public class MathFieldProcessing implements KeyboardListener {

	protected MathFieldW mf;

	/**
	 * @param mf
	 *            math input field
	 * 
	 */
	public MathFieldProcessing(MathFieldW mf) {
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus) {
		mf.setFocus(focus);
	}

	@Override
	public void onEnter() {
		mf.getKeyListener().onKeyPressed(new KeyEvent(JavaKeyCodes.VK_ENTER,
				KeyEvent.KeyboardType.INTERNAL));
		mf.getKeyListener().onKeyReleased(new KeyEvent(JavaKeyCodes.VK_ENTER,
				KeyEvent.KeyboardType.INTERNAL));
	}

	@Override
	public void onBackSpace() {
		mf.getKeyListener()
				.onKeyPressed(new KeyEvent(JavaKeyCodes.VK_BACK_SPACE,
						KeyEvent.KeyboardType.INTERNAL));
	}

	@Override
	public void onArrow(ArrowType type) {
		int arrowType = JavaKeyCodes.VK_RIGHT;
		switch (type) {
		case right:
			arrowType = JavaKeyCodes.VK_RIGHT;
			break;
		case left:
			arrowType = JavaKeyCodes.VK_LEFT;
			break;
		case up:
			arrowType = JavaKeyCodes.VK_UP;
			break;
		case down:
			arrowType = JavaKeyCodes.VK_DOWN;
			break;
		}

		KeyEvent keyEvent = new KeyEvent(arrowType, 0, KeyEvent.KeyboardType.INTERNAL);
		mf.getKeyListener().onKeyPressed(keyEvent);
		mf.readPosition();
	}

	@Override
	public void insertString(String text) {
		KeyboardInputAdapter.onKeyboardInput(mf.getInternal(), text);
	}

	/**
	 * @param text
	 *            text to be inserted
	 */
	public void autocomplete(String text) {
		mf.deleteCurrentWord();
		insertString(text);
		mf.setFocus(true);
	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		mf.blur();
	}

	@Override
	public MathFieldW getField() {
		return mf;
	}

	@Override
	public void onKeyboardClosed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void ansPressed() {
		// only for AV (subclass)
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}
