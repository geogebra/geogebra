package org.geogebra.web.full.gui.panel;

import org.geogebra.common.io.layout.panel.Panel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;

class TableViewPanel implements Panel {

	private ToolbarPanel toolbarPanel;

	TableViewPanel(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
	}

	@Override
	public void open() {
		toolbarPanel.openTableView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}
}
