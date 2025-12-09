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

package org.geogebra.web.html5.gui;

import org.geogebra.common.kernel.ModeSetter;
import org.gwtproject.user.client.ui.HasVisibility;

/**
 * Classic toolbar or its component.
 */
public interface ToolBarInterface extends HasVisibility {

	/**
	 * Notify when app mode changes.
	 * @param mode app mode
	 * @param m mode setter
	 * @return new mode
	 */
	int setMode(int mode, ModeSetter m);

	/**
	 * Close all submenus
	 */
	void closeAllSubmenu();

	/**
	 * @return whether this has mobile layout
	 */
	boolean isMobileToolbar();

}
