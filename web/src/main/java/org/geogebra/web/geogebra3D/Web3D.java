package org.geogebra.web.geogebra3D;

import org.geogebra.web.full.Web;
import org.geogebra.web.full.gui.applet.AppletFactory;

public class Web3D extends Web {

	@Override
	protected AppletFactory getAppletFactory() {
		return new AppletFactory3D();
	}
}
