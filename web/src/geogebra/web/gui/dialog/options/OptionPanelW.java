package geogebra.web.gui.dialog.options;

import com.google.gwt.user.client.ui.Widget;

public interface OptionPanelW {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI();
	public Widget getWrappedPanel();
}
