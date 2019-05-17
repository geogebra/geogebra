package org.geogebra.web.full.gui.toolbarpanel;

/**
 * Hosts actions for the left side panel container.
 */
class TabContainer extends ToolbarPanel.ToolbarTab {

	private ToolbarPanel toolbarPanel;

	TabContainer(ToolbarPanel toolbarPanel) {
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

	@Override
	protected void onActive() {

	}
}
