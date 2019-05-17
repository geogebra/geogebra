package org.geogebra.web.full.gui.panel;

import org.geogebra.common.io.layout.panel.Panel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;

class AlgebraPanel implements Panel {

	private ToolbarPanel toolbarPanel;

	AlgebraPanel(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
	}

	@Override
	public void open() {
		toolbarPanel.openAlgebra(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}
}
