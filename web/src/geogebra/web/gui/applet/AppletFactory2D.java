package geogebra.web.gui.applet;

import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplet;
import geogebra.web.main.AppWapplication;
import geogebra.web.main.GDevice;

public class AppletFactory2D implements AppletFactory {

	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth gf,
	        GLookAndFeelI laf) {
	    return new AppWapplet(ae, gf, 2, (GLookAndFeel)laf);
    }

	public AppW getApplication(ArticleElement article, GeoGebraAppFrame fr, GLookAndFeel laf,
            GDevice device) {
	    return new AppWapplication(article, fr, 2, laf, device);
    }

}
