package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.dom.style.shared.Unit;

/**
 * Tab of Spreadsheet View.
 */
public class SpreadsheetTab extends ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private @CheckForNull SpreadsheetPanel spreadsheetPanel;

	/**
	 * Constructor
	 * @param toolbarPanel - toolbar panel
	 */
	public SpreadsheetTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
	}

	private void createContent() {
		SpreadsheetPanel panel = new SpreadsheetPanel(toolbarPanel.getApp());
		add(panel);
		panel.getElement().getParentElement().getStyle().setHeight(100, Unit.PCT);
		spreadsheetPanel = panel;
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

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return spreadsheetPanel == null ? null : spreadsheetPanel.getKeyboardListener();
	}
}
