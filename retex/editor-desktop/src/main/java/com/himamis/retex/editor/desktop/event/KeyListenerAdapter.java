/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.himamis.retex.editor.desktop.event;

import java.awt.event.KeyEvent;

import com.himamis.retex.editor.share.event.KeyListener;

public class KeyListenerAdapter implements java.awt.event.KeyListener {
	
	private KeyListener keyListener;
	
	public KeyListenerAdapter(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keyListener.onKeyTyped(wrapEvent(e));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyListener.onKeyPressed(wrapEvent(e));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyListener.onKeyReleased(wrapEvent(e));
	}
	
	private static com.himamis.retex.editor.share.event.KeyEvent wrapEvent(
			KeyEvent event) {
		int keyCode = event.getKeyCode();
		int keyModifiers = event.getModifiers();
		char charCode = event.getKeyChar();
		return new com.himamis.retex.editor.share.event.KeyEvent(keyCode, keyModifiers, charCode);
	}

}
