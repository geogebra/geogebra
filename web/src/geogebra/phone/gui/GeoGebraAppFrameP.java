package geogebra.phone.gui;

import geogebra.phone.Phone;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.AppletFactory;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.GDevice;

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
