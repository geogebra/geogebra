package org.geogebra.web.web.gui.app;

import org.geogebra.common.main.App;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

public class EuclidianStyleBarPanel extends AbsolutePanel implements RequiresResize {

	private App application;
	EuclidianStyleBarW eviewsb = null;

	public EuclidianStyleBarPanel() {
		addStyleName("StyleBarPanel");
	}

	public void setStyleBar(EuclidianStyleBarW evs) {
		if (evs != eviewsb) {
			//if (eviewsb != null)
			//	simplep.remove(eviewsb);

			//simplep.add(eviewsb = evs);
		}
	}

	public AbsolutePanel getSimplePanel() {
	    return this;
    }

	public void onResize() {
		//App.debug("resized");
    }

	public void attachApp(App app) {
		if (application != app) {
			application = app;
			setStyleBar((EuclidianStyleBarW)application.getActiveEuclidianView().getStyleBar());
		}
	}
}
