package org.geogebra.web.geogebra3D;

import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

/**
 * 3D Applets factory class
 *
 */
public class AppletFactory3D implements AppletFactory {

	@Override
	public AppWFull getApplet(GeoGebraElement element,
			AppletParameters parameters, GeoGebraFrameFull fr,
			GLookAndFeelI laf, GDevice device) {
		return new AppWapplet3D(element, parameters, fr, (GLookAndFeel) laf, device);
	}
}
