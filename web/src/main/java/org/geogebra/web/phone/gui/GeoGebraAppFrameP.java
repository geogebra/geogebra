package org.geogebra.web.phone.gui;

import org.geogebra.web.phone.Phone;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;

public class GeoGebraAppFrameP extends GeoGebraAppFrame {

	private Phone phone;

	public GeoGebraAppFrameP(GLookAndFeel laf, GDevice device,
			AppletFactory factory, Phone phone) {
		super(laf, device, factory);
		this.phone = phone;
	}

	@Override
	public void init() {
		super.init();
		phone.initGUI();
	}

}
