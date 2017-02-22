package org.geogebra.web.web.gui.applet;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.main.GDevice;

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
	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth frame,
			GLookAndFeelI laf, GDevice device);

}
