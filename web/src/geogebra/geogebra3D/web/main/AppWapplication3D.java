package geogebra.geogebra3D.web.main;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.kernel3D.KernelW3D;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.main.AppWapplication;

/**
 * for 3D
 * @author mathieu
 *
 */
public class AppWapplication3D extends AppWapplication {

	/**
	 * constructor
	 * @param article
	 * @param geoGebraAppFrame
	 */
	public AppWapplication3D(ArticleElement article, GeoGebraAppFrame geoGebraAppFrame)  {
	    super(article, geoGebraAppFrame);
	    //Window.alert("AppWapplication3D : I will be threeD :-)");
    }
	
	
	@Override
    protected Kernel newKernel(App this_app){
		return new KernelW3D(this_app);
	}

}
