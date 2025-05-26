package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import javax.annotation.CheckForNull;

import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarTab;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Tab of Spreadsheet View.
 */
public class SpreadsheetTab extends ToolbarTab {

	private final ToolbarPanel toolbarPanel;
	private @CheckForNull SpreadsheetPanel spreadsheetPanel;
	private @CheckForNull SpreadsheetStyleBar spreadsheetStyleBar;

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
		FlowPanel tabPanel = new FlowPanel();

		if (PreviewFeature.isAvailable(PreviewFeature.SPREADSHEET_STYLEBAR)) {
			spreadsheetStyleBar = new SpreadsheetStyleBar(toolbarPanel.getApp(),
					panel.getStyleBarModel(), false);
			updateSpreadsheetStyleBarStyle(toolbarPanel.getApp().isPortrait());
			toolbarPanel.insert(spreadsheetStyleBar, 2);
		}

		tabPanel.add(panel);
		add(tabPanel);

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
		if (spreadsheetStyleBar != null) {
			updateSpreadsheetStyleBarStyle(toolbarPanel.getApp().isPortrait());
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

	/**
	 * Show or hide style bar
	 * @param show true if the style bar should be visible
	 */
	public void showStyleBar(boolean show) {
		if (spreadsheetStyleBar != null) {
			spreadsheetStyleBar.setVisible(show);
		}
	}

	/**
	 * @return {@link SpreadsheetPanel}
	 */
	public SpreadsheetPanel getSpreadsheetPanel() {
		return spreadsheetPanel;
	}

	/**
	 * Update portrait/landscape style of style bar
	 * @param isPortrait true, if view is in portrait mode
	 */
	public void updateSpreadsheetStyleBarStyle(boolean isPortrait) {
		if (spreadsheetStyleBar != null) {
			Dom.toggleClass(spreadsheetStyleBar, "portrait", "landscape", isPortrait);
		}
	}
}