package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * 
 */
public class TableTab extends ToolbarPanel.ToolbarTab implements SetLabels {

	private ToolbarPanel toolbarPanel;
	private AppW app;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.app;
		buildGui();
		setLabels();
	}

	@Override
	protected void onActive() {
		buildGui();
	}

	private TableValuesViewW getView() {
		return (TableValuesViewW) app.getGuiManager().getTableValuesView();
	}

	private void buildGui() {
		Widget w = getView().getWidget();
		if (w == null) {
			return;
		}
		setWidget(w);
		if (getView().isEmpty()) {
			w.getElement().getParentElement().addClassName("tableViewParent");
		}
	}

	@Override
	public void setLabels() {
		getView().setLabels();
	}

	@Override
	public void onResize() {
		super.onResize();
		int w = this.toolbarPanel.getTabWidth();
		if (w < 0) {
			return;
		}
		setWidth(2 * w + "px");
		getElement().getStyle().setLeft(2 * w, Unit.PX);
		getView().setHeight(getOffsetHeight());
	}

}
