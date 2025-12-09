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

package org.geogebra.web.full.gui;

import org.geogebra.keyboard.web.KeyboardCloseListener;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.HasAppletProperties;
import org.gwtproject.dom.client.Element;

/**
 * Interface for app frame
 *
 */
public interface HeaderPanelDeck
		extends HasAppletProperties, KeyboardCloseListener {
	/**
	 * Hide the full-sized GUI, e.g. material browser
	 * 
	 * @param panel
	 *            full-sized GUI
	 */
	void hidePanel(MyHeaderPanel panel);

	/** @return toolbar */
	GGWToolBar getToolbar();

	/**
	 * Update component heights to account for input bar
	 * 
	 * @param inputShowing
	 *            whether horizontal input bar is shown
	 */
	void setMenuHeight(boolean inputShowing);

	/**
	 * @return frame element
	 */
	Element getElement();

	/**
	 * Make sure keyboard visibility corresponds to both app.isKeyboardNeeded()
	 * and appNeedsKeyboard() TODO rename one of those functions
	 */
	void refreshKeyboard();

	/**
	 * @param show
	 *            whether to show it
	 * @param textField
	 *            listening text field
	 * @param forceShow
	 *            whether to force showing
	 * @return whether keyboard visibility changed
	 */
	boolean showKeyboard(boolean show, MathKeyboardListener textField,
			boolean forceShow);

}
