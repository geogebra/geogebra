/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
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
