package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MediaSubMenu extends SubMenuPanel {

	public MediaSubMenu(AppW app) {
		super(app, true);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		addModesToToolbar(ToolBar.getMOWMediaToolBarDefString());
	}

	@Override
	public void deselectAllCSS() {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			Widget w = contentPanel.getWidget(i);
			w.getElement().setAttribute("selected", "false");
		}
	}


	@Override
	protected void createInfoPanel() {
		super.createInfoPanel();
		infoPanel.add(new Label("Here comes the info..."));
	}

}
