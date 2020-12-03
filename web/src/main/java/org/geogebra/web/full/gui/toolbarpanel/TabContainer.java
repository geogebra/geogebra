package org.geogebra.web.full.gui.toolbarpanel;

/**
 * Hosts actions for the left side panel container.
 */
public class TabContainer implements ShowableTab {

	private ToolbarPanel toolbarPanel;

	public TabContainer(ToolbarPanel toolbarPanel) {
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
