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

package org.geogebra.editor.desktop.event;

import java.awt.event.KeyEvent;

import org.geogebra.editor.share.event.KeyListener;

public class KeyListenerAdapter implements java.awt.event.KeyListener {
	
	private KeyListener keyListener;
	
	public KeyListenerAdapter(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (!e.isAltDown() && !e.isControlDown()) {
			keyListener.onKeyTyped(wrapEvent(e));
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyListener.onKeyPressed(wrapEvent(e));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyListener.onKeyReleased(wrapEvent(e));
	}
	
	private static org.geogebra.editor.share.event.KeyEvent wrapEvent(
			KeyEvent event) {
		int keyCode = event.getKeyCode();
		int keyModifiers = event.getModifiers();
		char charCode = event.getKeyChar();
		return new org.geogebra.editor.share.event.KeyEvent(keyCode, keyModifiers, charCode,
				org.geogebra.editor.share.event.KeyEvent.KeyboardType.EXTERNAL);
	}

}
