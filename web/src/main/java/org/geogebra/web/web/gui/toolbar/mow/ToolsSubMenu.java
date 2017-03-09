package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

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
		contentPanel.add(LayoutUtilW.panelRow(shapes, points));
	}


	@Override
	protected void createInfoPanel() {
		super.createInfoPanel();
		infoPanel.add(new Label("Here comes the info..."));
	}
}
