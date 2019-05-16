package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;

/**
 * Factory for either 2D or 3D applets
 */
public interface AppletFactory {
	/**
	 * 
	 * @param ae
	 *            article element
	 * @param frame
	 *            applet frame
	 * @param laf
	 *            look and feel
	 * @param device
	 *            browser / tablet; used for file sync
	 * @return applet
	 */
	AppW getApplet(ArticleElementInterface ae, GeoGebraFrameFull frame,
			GLookAndFeelI laf, GDevice device);

}
