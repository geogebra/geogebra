package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElementInterface;

/**
 * Applet factory for 2D compilation
 */
public class AppletFactory2D implements AppletFactory {

	@Override
	public AppW getApplet(ArticleElementInterface ae, GeoGebraFrameFull gf,
			GLookAndFeelI laf, GDevice device) {
		return new AppWFull(ae, 2, laf, device, gf);
    }

}
