package org.geogebra.web.phone.main.app;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;

public class AppletFactoryPhone implements AppletFactory {

	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth gf,
	        GLookAndFeelI laf) {
		return new AppPapplet(ae, gf, (GLookAndFeel) laf);
	}

	public AppW getApplication(ArticleElement article, GeoGebraAppFrame fr,
	        GLookAndFeel laf, GDevice device) {
		return new AppP(article, fr, laf, device);
	}

}
