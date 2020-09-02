package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;

/**
 * Factory for either 2D or 3D applets
 */
public interface AppletFactory {

	/**
	 * @param element
	 *            element containing applet
	 * @param parameters
	 *            article element
	 * @param frame
	 *            applet frame
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser / tablet; used for file sync
	 * @return applet
	 */
	AppW getApplet(GeoGebraElement element, AppletParameters parameters,
			GeoGebraFrameFull frame, GLookAndFeelI laf, GDevice device);

}
