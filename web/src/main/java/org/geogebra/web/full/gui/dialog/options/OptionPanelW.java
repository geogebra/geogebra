package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.Widget;

/**
 * A panel of the Settings view
 */
public interface OptionPanelW {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI();

	/**
	 * @return UI element
	 */
	public Widget getWrappedPanel();

	/**
	 * @param height
	 *            new height
	 * @param width
	 *            new width
	 */
	public void onResize(int height, int width);

	/**
	 * @return tab panel
	 */
	public MultiRowsTabPanel getTabPanel();
}
