package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Label;

public class ToolsSubMenu extends SubMenuPanel {

	public ToolsSubMenu(AppW app) {
		super(app, true);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		contentPanel.add(new Label("Here comes the tools"));
	}

	protected void createInfoPanel() {
		super.createInfoPanel();
		infoPanel.add(new Label("Here comes the info..."));
	}
}
