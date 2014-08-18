package geogebra.web.gui.applet;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.util.ArticleElement;
import geogebra.web.main.AppW;
import geogebra.web.main.AppWapplet;

public class AppletFactory {

	public AppW getApplet(ArticleElement ae, GeoGebraFrame gf, GLookAndFeel laf) {
	    return new AppWapplet(ae, gf, 2, laf);
    }

}
