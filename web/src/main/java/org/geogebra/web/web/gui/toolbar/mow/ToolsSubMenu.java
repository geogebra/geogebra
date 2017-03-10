package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolsSubMenu extends SubMenuPanel {

	private class GroupPanel extends FlowPanel {
		private static final int BUTTON_WIDTH = 40;
		private int cols;

		public GroupPanel(int cols) {
			this.cols = cols;
			addStyleName("groupPanel");
			setWidth(((cols + 1) * BUTTON_WIDTH) + "px");
		}
	}

	private FlowPanel panelRow;
	private GroupPanel shapes;
	private GroupPanel points;
	public ToolsSubMenu(AppW app) {
		super(app, true);
		addStyleName("toolsSubMenu");
		
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		shapes = new GroupPanel(5);
		points = new GroupPanel(4);
		addModesToToolbar(shapes, ToolBar.getMOWToolsShapesDefString());
		addModesToToolbar(points, ToolBar.getMOWToolsPointsDefString());
		panelRow = LayoutUtilW.panelRow(shapes, points);
		contentPanel.add(panelRow);
	}

	@Override
	public void deselectAllCSS() {
		Log.debug("widget count: " + panelRow.getWidgetCount());

		for (int i = 0; i < panelRow.getWidgetCount(); i++) {
			FlowPanel w = (FlowPanel) panelRow.getWidget(i);
			for (int j = 0; j < w.getWidgetCount(); j++) {
				w.getWidget(j).getElement().setAttribute("selected", "false");
			}

		}
	}

	@Override
	public void setCSStoSelected(Widget source) {
		deselectAllCSS();
		super.setCSStoSelected(source);
	}

	@Override
	protected void createInfoPanel() {
		super.createInfoPanel();
	}
}
