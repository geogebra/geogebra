package org.geogebra.web.full.gui.toolbarpanel;

/**
 * Hosts actions for the left side panel container.
 */
public class TabContainer extends ToolbarPanel.ToolbarTab {

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

	@Override
	protected void onActive() {
		// not needed
	}
}
