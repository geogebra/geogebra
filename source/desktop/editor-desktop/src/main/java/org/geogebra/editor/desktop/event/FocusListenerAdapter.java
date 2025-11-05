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

import java.awt.event.FocusEvent;

import org.geogebra.editor.share.event.FocusListener;

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
