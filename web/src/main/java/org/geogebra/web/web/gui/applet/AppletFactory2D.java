package org.geogebra.web.web.gui.applet;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.AppWapplet;
import org.geogebra.web.web.main.GDevice;

/**
 * Applet factory for 2D compilation
 */
public class AppletFactory2D implements AppletFactory {

	@Override
	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth gf,
			GLookAndFeelI laf, GDevice device) {
		return new AppWapplet(ae, gf, true, 2, (GLookAndFeel) laf, device);
    }

}
