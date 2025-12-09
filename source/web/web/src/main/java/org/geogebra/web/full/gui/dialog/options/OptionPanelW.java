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

package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * A panel of the Settings view
 */
public interface OptionPanelW {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	void updateGUI();

	/**
	 * @return UI element
	 */
	Widget getWrappedPanel();

	/**
	 * @param height
	 *            new height
	 * @param width
	 *            new width
	 */
	void onResize(int height, int width);

	/**
	 * @return tab panel
	 */
	MultiRowsTabPanel getTabPanel();
}
