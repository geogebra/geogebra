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

package org.geogebra.web.html5.util.keyboard;

import org.gwtproject.user.client.ui.HasVisibility;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Virtual keyboard.
 */
public interface VirtualKeyboardW extends IsWidget, RequiresResize, HasVisibility {

	/**
	 * Show the keyboard.
	 */
	void show();

	/**
	 * Reset keyboard state.
	 */
	void resetKeyboardState();

	/**
	 * @return TODO
	 */
	boolean shouldBeShown();

	/**
	 * @return height in pixels
	 */
	int getOffsetHeight();

	/**
	 * Show the keyboard once the listener is focused.
	 */
	void showOnFocus();

	/**
	 * Run callback when animation is done.
	 * @param runnable callback
	 */
	void afterShown(Runnable runnable);

	/**
	 * Show the keyboard.
	 * @param animated whether to use animation
	 */
	void prepareShow(boolean animated);

	/**
	 * Show "more" button.
	 */
	void showMoreButton();

	/**
	 * Hide "more" button.
	 */
	void hideMoreButton();

}
