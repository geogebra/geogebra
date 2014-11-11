package geogebra.tablet;

import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.AppWapplication;
import geogebra.web.main.GDevice;

public class TabletGeoGebraAppFrame extends GeoGebraAppFrame {

	public TabletGeoGebraAppFrame(final GLookAndFeel laf, GDevice device) {
	    super(laf, device);
    }
	
	@Override
	protected AppW createApplication(final ArticleElement article, final GLookAndFeel laf, GDevice device) {
		return new AppWapplication(article, this, 2, laf, device);
    }

}
