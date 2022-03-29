package org.geogebra.web.full.gui.toolbarpanel;

public class DistributionTab extends ToolbarPanel.ToolbarTab {

	private final ToolbarPanel toolbarPanel;

	/**
	 * Constructor
	 * @param toolbarPanel - parent toolbar panel
	 */
	public DistributionTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		createContent();
	}

	private void createContent() {
		add(new DistributionPanel());
	}

	@Override
	protected void onActive() {

	}

	@Override
	public void setLabels() {

	}

	@Override
	public void open() {

	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}
}
