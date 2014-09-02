package geogebra.geogebra3D.web.gui.app;

import geogebra.geogebra3D.web.main.AppWapplication3D;
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;

/**
 * Frame for 3D stuff
 * 
 * @author mathieu
 *
 */
public class GeoGebraAppFrame3D extends GeoGebraAppFrame {

	/**
	 * constructor
	 */
	public GeoGebraAppFrame3D(GLookAndFeel laf) {
		super(laf);
		//Window.alert("GeoGebraAppFrame3D : I will be threeD :-)");
	}
	
	@Override
    protected AppW createApplication(ArticleElement article, GLookAndFeel laf) {
		return new AppWapplication3D(article, this, laf);
    }

}
