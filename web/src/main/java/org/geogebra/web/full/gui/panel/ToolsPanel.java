package org.geogebra.web.full.gui.panel;

import org.geogebra.common.io.layout.panel.Panel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;

class ToolsPanel implements Panel {

	private ToolbarPanel toolbarPanel;

	ToolsPanel(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
	}

	@Override
	public void open() {
		toolbarPanel.openTools(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}
}
