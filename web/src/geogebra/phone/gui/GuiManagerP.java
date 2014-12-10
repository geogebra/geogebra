package geogebra.phone.gui;

import geogebra.common.gui.Layout;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.GDevice;

public class GuiManagerP extends GuiManagerW {

	public GuiManagerP(AppW app, GDevice device) {
		super(app, device);
	}

	@Override
	public void setLayout(Layout layout) {
		// no layout for phones
	}

	@Override
	public void initialize() {
		initAlgebraController();
	}
}
