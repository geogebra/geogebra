package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VerticalPanelSmart extends VerticalPanel implements RequiresResize {
	public VerticalPanelSmart() {
		super();
	}
	public void onResize() {
		AbstractApplication.debug("Resized");
	}
}
