package geogebra.geogebra3D;

import geogebra.geogebra3D.web.main.AppWapplet3D;
import geogebra.geogebra3D.web.main.AppWapplication3D;
import geogebra.html5.gui.laf.GLookAndFeelI;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.applet.AppletFactory;
import geogebra.web.gui.applet.GeoGebraFrameBoth;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplet;
import geogebra.web.main.GDevice;

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
