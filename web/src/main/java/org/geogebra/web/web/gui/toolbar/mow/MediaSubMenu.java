package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Label;

public class MediaSubMenu extends SubMenuPanel {

	public MediaSubMenu(AppW app) {
		super(app, true);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		contentPanel.add(new Label("Media SubMenu"));
	}

	protected void createInfoPanel() {
		super.createInfoPanel();
		contentPanel.add(new Label("Here comes the info..."));
	}
}
