package org.geogebra.web.web.gui.app;

import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class EuclidianStyleBarPanel extends AbsolutePanel implements RequiresResize {

	private App application;

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
