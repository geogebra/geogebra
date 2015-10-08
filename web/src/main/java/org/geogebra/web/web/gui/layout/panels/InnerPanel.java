package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;

public class InnerPanel extends FlowPanel implements RequiresResize {

	private Panel content;
		private DockPanelW dock;

	public InnerPanel(DockPanelW dock, Panel cpPanel) {
			this.content = cpPanel;
			this.dock = dock;
			add(cpPanel);
		}

	public void onResize() {
		int height = dock.getComponentInteriorHeight() - dock.navHeight();
		if (height > 0) {
			content.setHeight(height + "px");
		}
	}

}