package geogebra.tablet;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.tablet.main.TabletApp;
import geogebra.web.gui.app.GeoGebraAppFrame;

public class TabletGeoGebraAppFrame extends GeoGebraAppFrame {

	public TabletGeoGebraAppFrame(final GLookAndFeel laf) {
	    super(laf);
    }
	
	@Override
	protected AppW createApplication(final ArticleElement article, final GLookAndFeel laf) {
		return new TabletApp(article, this, 2, laf);
    }
}
