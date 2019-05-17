package org.geogebra.web.full.gui.panel;

import org.geogebra.common.io.layout.panel.Panel;
import org.geogebra.common.io.layout.panel.Panels;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.html5.main.AppW;

public class PanelsW extends Panels {

	private AppW app;

	public PanelsW(AppW app) {
		this.app = app;
	}

	@Override
	protected Panel newAlgebraPanel() {
		return new AlgebraPanel(getToolbarPanel());
	}

	@Override
	protected Panel newToolsPanel() {
		return new ToolsPanel(getToolbarPanel());
	}

	@Override
	protected Panel newTableOfValuesPanel() {
		return new TableViewPanel(getToolbarPanel());
	}

	@Override
	protected Panel newPanelContainer() {
		return new PanelContainer(getToolbarPanel());
	}

	private ToolbarPanel getToolbarPanel() {
		GuiManagerW guiManager = (GuiManagerW) app.getGuiManager();
		return guiManager.getUnbundledToolbar();
	}
}
