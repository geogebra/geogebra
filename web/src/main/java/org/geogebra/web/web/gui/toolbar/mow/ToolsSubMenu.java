package org.geogebra.web.web.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolsSubMenu extends SubMenuPanel {

	private static class GroupPanel extends FlowPanel {
		private static final int BUTTON_WIDTH = 40;

		public GroupPanel() {
			addStyleName("groupPanel");
		}

		public void setColumns(int columns) {
			setWidth(((columns + 1) * BUTTON_WIDTH) + "px");
		}

	}

	private FlowPanel panelRow;
	public ToolsSubMenu(AppW app) {
		super(app, true);
		addStyleName("toolsSubMenu");
		
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		panelRow = new FlowPanel();
		panelRow.addStyleName("panelRow");
		addModesToToolbar(panelRow, ToolBar.getMOWToolsDefString());
		contentPanel.add(panelRow);
	}

	@Override
	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		int col = 0;
		GroupPanel group = new GroupPanel();
		for (Integer mode : menu) {
			if (app.isModeValid(mode)) {
				StandardButton btn = createButton(mode);
				btn.getElement().setId("mode" + mode);
				group.add(btn);
				col++;
			}
		}
		group.setColumns(col / 2 + col % 2);
		panel.add(group);
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

	@Override
	public void onOpen() {
		super.onOpen();
		int mode = app.getMode();
		Element btn = DOM.getElementById("mode" + mode);
		if (btn != null) {
			btn.setAttribute("selected", "true");
		}

	}
}
