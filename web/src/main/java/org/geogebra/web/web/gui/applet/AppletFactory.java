package org.geogebra.web.web.gui.applet;

import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;


public interface AppletFactory {
	public AppW getApplet(ArticleElement ae, GeoGebraFrameBoth gf,
	        GLookAndFeelI laf);
	public AppW getApplication(ArticleElement article, GeoGebraAppFrame fr, GLookAndFeel laf,
            GDevice device);

}
