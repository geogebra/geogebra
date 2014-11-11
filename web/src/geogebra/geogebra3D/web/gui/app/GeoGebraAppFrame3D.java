package geogebra.geogebra3D.web.gui.app;

import geogebra.geogebra3D.web.main.AppWapplication3D;
import geogebra.html5.main.AppW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.laf.GLookAndFeel;
import geogebra.web.main.GDevice;

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
	public GeoGebraAppFrame3D(GLookAndFeel laf, GDevice device) {
		super(laf, device);
		//Window.alert("GeoGebraAppFrame3D : I will be threeD :-)");
	}
	
	@Override
    protected AppW createApplication(ArticleElement article, GLookAndFeel laf, GDevice device) {
		return new AppWapplication3D(article, this, laf, device);
    }

}
