package org.geogebra.web.full.gui.layout;

import org.geogebra.common.gui.SetLabels;
import org.gwtproject.user.client.ui.HasVisibility;
import org.gwtproject.user.client.ui.InsertPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

/**
 * Dock control panel.
 */
public interface DockControlPanel extends SetLabels, IsWidget, InsertPanel, HasVisibility {

	/**
	 * @return parent widget
	 */
	Widget getParent();

	/**
	 * Clear the panel.
	 */
	void clear();

	/**
	 * Set layout.
	 */
	void setLayout();
}
