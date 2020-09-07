package org.geogebra.web.full2d;

import org.geogebra.web.full.Web;
import org.geogebra.web.full.gui.applet.AppletFactory;

public class Web2D extends Web {
	@Override
	protected AppletFactory getAppletFactory() {
		return new AppletFactory2D();
	}
}
