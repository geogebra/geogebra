package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MediaSubMenu extends SubMenuPanel implements ClickHandler, FastClickHandler {

	public MediaSubMenu(AppW app) {
		super(app, true);
	}

	@Override
	protected void createContentPanel() {
		super.createContentPanel();
		addModesToToolbar(ToolBar.getMOWMediaToolBarDefString());
	}




	@Override
	protected void createInfoPanel() {
		super.createInfoPanel();
		infoPanel.add(new Label("Here comes the info..."));
	}

	@Override
	public void onClick(Widget source) {
		/*
		 * Log.debug("onClick widget");
		 * 
		 * int mode =
		 * Integer.parseInt(source.getElement().getAttribute("mode"));
		 * Log.debug("onClick mode: " + mode); app.setMode(mode);
		 * 
		 */
	}

	@Override
	public void onClick(ClickEvent event) {
		// Log.debug("onClick event");
		// TODO Auto-generated method stub

	}
}
