package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Label;

public class PenSubMenu extends SubMenuPanel {

	public PenSubMenu(AppW app) {
		super(app, false);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		contentPanel.add(new Label("PEN PEN PEN PEN..."));
	}

}
