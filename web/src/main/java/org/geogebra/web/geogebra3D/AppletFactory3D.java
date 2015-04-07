package org.geogebra.web.geogebra3D;

import org.geogebra.web.geogebra3D.web.main.AppWapplet3D;
import org.geogebra.web.geogebra3D.web.main.AppWapplication3D;
import org.geogebra.web.html5.gui.laf.GLookAndFeelI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;
import org.geogebra.web.web.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.AppWapplet;
import org.geogebra.web.web.main.GDevice;

public class AppletFactory3D implements AppletFactory{
	
		@Override
	public AppWapplet getApplet(ArticleElement ae, GeoGebraFrameBoth fr,
	        GLookAndFeelI laf) {
			return new AppWapplet3D(ae, fr, (GLookAndFeel) laf);
		}
		
		public AppW getApplication(ArticleElement article, GeoGebraAppFrame fr, GLookAndFeel laf,
	            GDevice device) {
		    return new AppWapplication3D(article, fr,  laf, device);
	    }
}
