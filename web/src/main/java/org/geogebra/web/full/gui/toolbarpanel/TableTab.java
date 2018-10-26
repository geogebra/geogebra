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

	private TableValuesViewW view;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.app;
		view = new TableValuesViewW(app);
		buildGui();
		setLabels();
	}

	private void buildGui() {
		Widget w = view.getWidget();
		if (w == null) {
			return;
		}

		setWidget(w);

		if (view.isEmpty()) {
			w.getElement().getParentElement().addClassName("tableViewParent");
		}
	}

	@Override
	public void setLabels() {
		view.setLabels();
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
		view.setHeight(getOffsetHeight());
	}

}
