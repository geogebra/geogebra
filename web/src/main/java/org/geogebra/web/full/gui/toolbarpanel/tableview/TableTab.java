package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.html5.main.AppW;

/**
 * Tab of Table Values View.
 * 
 * @author laszlo
 */
public class TableTab extends ToolbarPanel.ToolbarTab implements SetLabels {

	private ToolbarPanel toolbarPanel;
	private AppW app;
	private TableValuesPanel tableValuesPanel;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		super(toolbarPanel);
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.getApp();
		tableValuesPanel = new TableValuesPanel(app);
	}

	@Override
	protected void onActive() {
		buildGui();
		tableValuesPanel.setHeight(toolbarPanel.getTabHeight());
	}

	/**
	 * Rebuild the tab.
	 */
	private void buildGui() {
		setWidget(tableValuesPanel);
	}

	@Override
	public void setLabels() {
		tableValuesPanel.setLabels();
	}

	@Override
	public void open() {
		toolbarPanel.openTableView(true);
	}

	@Override
	public void close() {
		toolbarPanel.close();
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getTabHeight();
		if (w < 0 || h < 0) {
			return;
		}

		setWidth(w + "px");
		setHeight(h + "px");
		tableValuesPanel.setHeight(h);
	}

	/**
	 * Scroll table view to the corresponding column of the geo.
	 * 
	 * @param geo
	 *            to scroll.
	 */
	public void scrollTo(GeoEvaluatable geo) {
		tableValuesPanel.scrollTo(geo);
	}
}
