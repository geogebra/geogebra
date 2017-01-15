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

import java.awt.event.FocusEvent;

import com.himamis.retex.editor.share.event.FocusListener;

public class FocusListenerAdapter implements java.awt.event.FocusListener {
	
	private FocusListener focusListener;
	
	public FocusListenerAdapter(FocusListener focusListener) {
		this.focusListener = focusListener;
	}

	@Override
	public void focusGained(FocusEvent e) {
		focusListener.onFocusGained();
	}

	@Override
	public void focusLost(FocusEvent e) {
		focusListener.onFocusLost();
	}
	
}
