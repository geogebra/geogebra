package org.geogebra.web.full.gui.layout;

import org.geogebra.common.gui.SetLabels;

import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface DockControlPanel extends SetLabels, IsWidget, InsertPanel {
	boolean isVisible();

	Widget getParent();

	void clear();

	void setVisible(boolean show);

	void setLayout();
}
