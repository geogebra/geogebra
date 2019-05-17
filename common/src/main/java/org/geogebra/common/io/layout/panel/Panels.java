package org.geogebra.common.io.layout.panel;

public abstract class Panels {

	private Panel algebraPanel;
	private Panel toolsPanel;
	private Panel tableViewPanel;
	private Panel panelContainer;

	public Panel getPanel(String panelId) {
		switch (panelId) {
			case "A":
				return getAlgebraPanel();
			case "T":
				return getToolsPanel();
			case "TV":
				return getTableViewPanel();
			case "P":
				return getPanelContainer();
		}
		return null;
	}

	private Panel getAlgebraPanel() {
		if (algebraPanel == null) {
			algebraPanel = newAlgebraPanel();
		}
		return algebraPanel;
	}

	private Panel getToolsPanel() {
		if (toolsPanel == null) {
			toolsPanel = newToolsPanel();
		}
		return toolsPanel;
	}

	private Panel getTableViewPanel() {
		if (tableViewPanel == null) {
			tableViewPanel = newTableOfValuesPanel();
		}
		return tableViewPanel;
	}

	private Panel getPanelContainer() {
		if (panelContainer == null) {
			panelContainer = newPanelContainer();
		}
		return panelContainer;
	}

	protected abstract Panel newAlgebraPanel();
	protected abstract Panel newToolsPanel();
	protected abstract Panel newTableOfValuesPanel();
	protected abstract Panel newPanelContainer();
}
