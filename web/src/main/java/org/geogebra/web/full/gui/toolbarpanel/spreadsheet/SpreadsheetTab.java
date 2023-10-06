package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;

/**
 * Tab of Spreadsheet View.
 */
public class SpreadsheetTab extends ToolbarTab {

	private ToolbarPanel toolbarPanel;

	/**
	 * Constructor
	 * @param toolbarPanel
	 */
	public SpreadsheetTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		createContent();
	}

	private void createContent() {
		add(new SpreadsheetPanel());
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void onResize() {
		// fill
	}

	@Override
	public void open() {
		// fill
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	protected void onActive() {
		// fill
	}
}
