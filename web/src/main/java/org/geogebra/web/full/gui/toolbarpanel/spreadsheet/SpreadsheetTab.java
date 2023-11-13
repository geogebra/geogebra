package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;

/**
 * Tab of Spreadsheet View.
 */
public class SpreadsheetTab extends ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private SpreadsheetPanel spreadsheetPanel;

	/**
	 * Constructor
	 * @param toolbarPanel - toolbar panel
	 */
	public SpreadsheetTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
	}

	private void createContent() {
		this.spreadsheetPanel = new SpreadsheetPanel(toolbarPanel.getApp(), this);
		add(spreadsheetPanel);
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void onResize() {
		if (spreadsheetPanel != null) {
			spreadsheetPanel.onResize();
		}
	}

	@Override
	public void open() {
		toolbarPanel.openSpreadsheetView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close(false);
	}

	@Override
	protected void onActive() {
		if (spreadsheetPanel == null) {
			createContent();
		}
	}

	@Override
	public DockPanelData.TabIds getID() {
		return DockPanelData.TabIds.SPREADSHEET;
	}
}
