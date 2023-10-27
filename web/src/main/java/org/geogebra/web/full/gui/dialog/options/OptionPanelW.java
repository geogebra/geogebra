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
