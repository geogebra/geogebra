package geogebra.phone.main.app;

import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.AppletFactory;
import geogebra.web.gui.applet.GeoGebraFrameBoth;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.GDevice;

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
