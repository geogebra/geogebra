package org.geogebra.test.euclidian;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.editor.share.util.KeyCodes;

final class KeyEventC extends KeyEvent {
	private KeyCodes keyCode;

	public KeyEventC(KeyCodes keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public boolean isEnterKey() {
		return keyCode == KeyCodes.ENTER;
	}

	@Override
	public boolean isCtrlDown() {
		return false;
	}

	@Override
	public boolean isAltDown() {
		return false;
	}

	@Override
	public char getCharCode() {
		return (char) keyCode.getJavaKeyCode();
	}

	@Override
	public void preventDefault() {
		// nothing to do
	}
}