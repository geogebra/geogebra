package org.geogebra.web.full.gui.layout;

import org.geogebra.common.gui.SetLabels;
import org.gwtproject.user.client.ui.InsertPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

/**
 * Dock control panel.
 */
public interface DockControlPanel extends SetLabels, IsWidget, InsertPanel {
	boolean isVisible();

	Widget getParent();

	void clear();

	void setVisible(boolean show);

	void setLayout();
}
