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
	private @CheckForNull FlowPanel tabPanel;
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
		FlowPanel wrappingPanel = new FlowPanel();
		wrappingPanel.addStyleName("spreadsheetTabPanel");

		if (isStyleBarAllowed()) {
			wrappingPanel.addStyleName("withStyleBar");
		}

		wrappingPanel.add(panel);
		add(wrappingPanel);

		wrappingPanel.getElement().getParentElement().getStyle().setHeight(100, Unit.PCT);
		spreadsheetPanel = panel;
		tabPanel = wrappingPanel;
	}

	private boolean isStyleBarAllowed() {
		return PreviewFeature.isAvailable(PreviewFeature.SPREADSHEET_STYLEBAR)
				&& toolbarPanel.spreadsheetStyleBarAllowed();
	}

	@Override
	public void setLabels() {
		// fill
	}

	@Override
	public void onResize() {
		if (tabPanel != null) {
			Dom.toggleClass(tabPanel, "withStyleBar",
					!toolbarPanel.isHeadingVisible() && isStyleBarAllowed());
		}
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

	/**
	 * @return {@link SpreadsheetPanel}
	 */
	public SpreadsheetPanel getSpreadsheetPanel() {
		return spreadsheetPanel;
	}

}