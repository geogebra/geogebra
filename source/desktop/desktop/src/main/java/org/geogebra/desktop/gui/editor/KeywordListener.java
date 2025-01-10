/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import java.util.EventListener;

/**
 * The interface KeywordListener is useful to listen to a keyword event.
 * 
 * @author Calixte DENIZET
 */
public interface KeywordListener extends EventListener {

	/**
	 * ONMOUSECLICKED
	 */
	int ONMOUSECLICKED = 1;

	/**
	 * ONMOUSEOVER
	 */
	int ONMOUSEOVER = 2;

	/**
	 * Called when a keyword is caught
	 * 
	 * @param e
	 *            a KeywordEvent
	 */
	void caughtKeyword(KeywordEvent e);

	/**
	 * @return the type of the listener
	 */
	int getType();
}
