package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.user.client.ui.Widget;

public interface OptionPanelW {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI();
	public Widget getWrappedPanel();
	public void onResize(int height, int width);

	public MultiRowsTabPanel getTabPanel();
}
