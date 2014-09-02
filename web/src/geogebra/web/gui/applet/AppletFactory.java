package geogebra.web.gui.applet;

import geogebra.html5.gui.GeoGebraFrame;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplet;

public class AppletFactory {

	public AppW getApplet(ArticleElement ae, GeoGebraFrame gf, GLookAndFeelI laf) {
	    return new AppWapplet(ae, gf, 2, (GLookAndFeel)laf);
    }

}
