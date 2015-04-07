package org.geogebra.web.web.gui.applet;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.AppWapplet;
import org.geogebra.web.web.main.AppWapplication;
import org.geogebra.web.web.main.GDevice;

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
