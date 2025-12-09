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
// This code has been written initially for Scilab (http://www.scilab.org/).

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
	@Override
	public int getType() {
		return type;
	}

	/**
	 * Called when a keyword is caught
	 * 
	 * @param e
	 *            a KeywordEvent
	 */
	@Override
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
