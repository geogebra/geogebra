package org.geogebra.web.web.gui.app;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class EuclidianStyleBarPanel extends AbsolutePanel implements RequiresResize {

	public EuclidianStyleBarPanel() {
		addStyleName("StyleBarPanel");
	}

	public AbsolutePanel getSimplePanel() {
	    return this;
    }

	public void onResize() {
		// Log.debug("resized");
    }

}
