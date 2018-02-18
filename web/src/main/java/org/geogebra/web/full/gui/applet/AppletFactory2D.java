package org.geogebra.web.full.gui.applet;

import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.full.main.AppWapplet;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;

/**
 * Applet factory for 2D compilation
 */
public class AppletFactory2D implements AppletFactory {

	@Override
	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth gf,
			GLookAndFeelI laf, GDevice device) {
		return new AppWapplet(ae, gf, 2, (GLookAndFeel) laf, device);
    }

}
