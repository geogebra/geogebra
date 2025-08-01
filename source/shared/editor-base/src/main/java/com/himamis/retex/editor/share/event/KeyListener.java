/*
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

package com.himamis.retex.editor.share.event;

/**
 * Key event listener.
 */
public interface KeyListener {

	/**
	 * Called when key is pressed.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyPressed(KeyEvent keyEvent);

	/**
	 * Called when key is released.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyReleased(KeyEvent keyEvent);

	/**
	 * Called when key is typed.
	 * @param keyEvent key event
	 * @return whether it was handled by this listener
	 */
	boolean onKeyTyped(KeyEvent keyEvent);
}
