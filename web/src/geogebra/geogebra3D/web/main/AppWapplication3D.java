package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidian3D.EuclidianControllerW3D;
import geogebra.geogebra3D.web.euclidian3D.EuclidianViewW3D;
import geogebra.geogebra3D.web.gui.GuiManagerW3D;
import geogebra.geogebra3D.web.kernel3D.KernelW3D;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.main.AppWapplication;

/**
 * for 3D
 * @author mathieu
 *
 */
public class AppWapplication3D extends AppWapplication {
	
	
	private EuclidianViewW3D euclidianView3D;
	private EuclidianControllerW3D euclidianController3D;

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
	

	@Override
    public boolean hasEuclidianView3D() {
		return false;
	}

	@Override
    public EuclidianView3DInterface getEuclidianView3D() {
		//Window.alert("getEuclidianView3D()");
		return euclidianView3D;
	}
	
	@Override
    public void initEuclidianViews() {

		super.initEuclidianViews();
		euclidianController3D = new EuclidianControllerW3D(kernel);
		euclidianView3D = new EuclidianViewW3D(euclidianController3D, null);

	}
	
	
	@Override
    protected GuiManagerW newGuiManager() {
		return new GuiManagerW3D(this);
	}


}
