package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.web.full.gui.toolbarpanel.ToolbarPanel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;

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
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.getApp();
		tableValuesPanel = new TableValuesPanel(app);
	}

	@Override
	protected void onActive() {
		buildGui();
		tableValuesPanel.setHeight(toolbarPanel.getOffsetHeight()
				- ToolbarPanel.CLOSED_HEIGHT_PORTRAIT);
	}

	/**
	 * Rebuild the tab.
	 */
	void buildGui() {
		setWidget(tableValuesPanel);
	}

	@Override
	public void setLabels() {
		tableValuesPanel.setLabels();
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getOffsetHeight()
				- ToolbarPanel.CLOSED_HEIGHT_PORTRAIT;
		if (w < 0 || h < 0) {
			return;
		}

		setWidth(2 * w + "px");
		setHeight(h + "px");
		getElement().getStyle().setLeft(2 * w, Unit.PX);
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
