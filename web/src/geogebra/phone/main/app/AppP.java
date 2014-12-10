package geogebra.phone.main.app;

import geogebra.geogebra3D.web.main.AppWapplication3D;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.GDevice;

public class AppP extends AppWapplication3D {
	
	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame, GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, laf, device);
	}

	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
			boolean undoActive, int dimension, GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, undoActive, dimension, laf, device);
	}
	
}
