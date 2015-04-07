package org.geogebra.web.phone.main.app;

import org.geogebra.web.geogebra3D.web.main.AppWapplication3D;
import org.geogebra.web.html5.util.ArticleElement;
import org.geogebra.web.phone.Phone;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.laf.GLookAndFeel;
import org.geogebra.web.web.main.GDevice;

public class AppP extends AppWapplication3D {

	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, laf, device);
	}

	public AppP(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame,
	        boolean undoActive, int dimension, GLookAndFeel laf, GDevice device) {
		super(article, geoGebraAppFrame, undoActive, dimension, laf, device);
	}

	@Override
	public void openSearch(String query) {
		Phone.showBrowseView();
	}

}
