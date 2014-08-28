package geogebra.phone;

import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.main.AppW;

public class GeoGebraAppFrameP extends GeoGebraAppFrame {
	
	public GeoGebraAppFrameP(GLookAndFeel laf) {
	    super(laf);
    }
	
	@Override
	protected AppW createApplication(final ArticleElement article, GLookAndFeel laf) {
		return new AppP(article, this, 2, laf);
    }
}
