package geogebra.phone;

import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;

public class GeoGebraAppFrameP extends GeoGebraAppFrame {
	
	public GeoGebraAppFrameP(GLookAndFeel laf) {
	    super(laf);
    }
	
	@Override
	protected AppW createApplication(final ArticleElement article, GLookAndFeel laf) {
		return new AppP(article, this, 2, laf);
    }
}
