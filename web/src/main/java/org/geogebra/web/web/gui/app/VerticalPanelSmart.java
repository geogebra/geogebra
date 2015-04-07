package org.geogebra.web.web.gui.app;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VerticalPanelSmart extends VerticalPanel implements RequiresResize {
	public VerticalPanelSmart() {
		super();
	}
	public void onResize() {
		App.debug("Resized");
	}
}
