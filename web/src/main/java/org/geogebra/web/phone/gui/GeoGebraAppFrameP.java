package org.geogebra.web.phone.gui;

import org.geogebra.web.phone.Phone;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;

public class GeoGebraAppFrameP extends GeoGebraAppFrame {

	public GeoGebraAppFrameP(GLookAndFeel laf, GDevice device,
	        AppletFactory factory) {
		super(laf, device, factory);
	}

	@Override
	public void init() {
		super.init();
		Phone.initGUI();
	}

}
