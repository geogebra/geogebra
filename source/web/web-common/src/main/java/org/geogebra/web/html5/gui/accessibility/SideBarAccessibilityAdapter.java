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

package org.geogebra.web.html5.gui.accessibility;

/**
 * Adapter for tabbing to the main controls (menu, AV)
 */
public interface SideBarAccessibilityAdapter {

	/** Sets focus to Burger menu */
	void focusMenu();

	/**
	 * Sets focus to AV Input
	 * 
	 * @param force
	 *            force to open AV tab if not active
	 * @param forceFade
	 *            force fade during animation
	 * @return if input can be focused.
	 */
	boolean focusInput(boolean force, boolean forceFade);

}
