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

/**
 * An adapter for the interface KeywordListener
 * 
 * @author Calixte DENIZET
 */
public abstract class KeywordAdapter implements KeywordListener {

	private int type;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            the type of listener (ONMOUSECLICKED or ONMOUSEOVER)
	 */
	protected KeywordAdapter(int type) {
		this.type = type;
	}

	/**
	 * @return the type of this listener
	 */
	public int getType() {
		return type;
	}

	/**
	 * Called when a keyword is caught
	 * 
	 * @param e
	 *            a KeywordEvent
	 */
	public abstract void caughtKeyword(KeywordEvent e);

	/**
	 * Class to have a KeywordListener attached to a MouseClicked event
	 */
	public abstract static class MouseClickedAdapter extends KeywordAdapter {

		/**
		 * Constructor
		 */
		public MouseClickedAdapter() {
			super(ONMOUSECLICKED);
		}
	}

	/**
	 * Class to have a KeywordListener attached to a MouseOver event
	 */
	public abstract static class MouseOverAdapter extends KeywordAdapter {

		/**
		 * Constructor
		 */
		public MouseOverAdapter() {
			super(ONMOUSEOVER);
		}
	}
}
