package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VerticalPanelSmart extends VerticalPanel implements RequiresResize {
	public VerticalPanelSmart() {
		super();
	}
	public void onResize() {
	    GWT.log("Resized");
	}
}
