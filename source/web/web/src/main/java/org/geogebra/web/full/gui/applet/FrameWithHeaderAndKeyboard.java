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

package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.gui.HeaderPanelDeck;
import org.gwtproject.user.client.ui.HasWidgets;
import org.gwtproject.user.client.ui.InsertPanel;

/**
 * Frame with header and keyboard.
 */
public interface FrameWithHeaderAndKeyboard extends HeaderPanelDeck, HasWidgets, InsertPanel {

	/**
	 * @return True if the frame is shown in a small window or if the frame has a compact header.
	 */
	boolean hasSmallWindowOrCompactHeader();

	/**
	 * Gets the object's offset height in pixels. This is the total height of the
	 * object, including decorations such as border and padding, but not margin.
	 *
	 * @return the object's offset height
	 */
	int getOffsetHeight();

	/**
	 * Gets the object's offset width in pixels. This is the total width of the
	 * object, including decorations such as border and padding, but not margin.
	 *
	 * @return the object's offset width
	 */
	int getOffsetWidth();

	/**
	 * This method can be called after the full-screen panel is hidden.
	 */
	void onPanelHidden();
}
