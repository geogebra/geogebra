package org.geogebra.web.full.gui.panel;

import org.geogebra.common.io.layout.panel.Panel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;

class PanelContainer implements Panel {

	private ToolbarPanel toolbarPanel;

	PanelContainer(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
	}

	@Override
	public void open() {
		toolbarPanel.open();
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}
}
