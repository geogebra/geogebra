package org.geogebra.web.full.gui.layout;

import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DockPanelDecorator implements RequiresResize {

	public Panel decorate(ScrollPanel algebrap, AppW app) {
		return algebrap;
	}

	public void onResize() {
		// nothing to do
	}

}
