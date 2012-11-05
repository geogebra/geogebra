package geogebra.web.gui.app;

import geogebra.common.main.App;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class AbsolutePanelSmart extends AbsolutePanel implements RequiresResize {
	public AbsolutePanelSmart() {
		super();
	}
	public void onResize() {
		App.debug("Resized");
	}
}
