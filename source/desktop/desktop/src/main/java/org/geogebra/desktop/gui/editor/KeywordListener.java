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
