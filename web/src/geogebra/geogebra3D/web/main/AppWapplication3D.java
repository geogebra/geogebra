package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.geogebra3D.web.euclidian3D.EuclidianController3DW;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.gui.GuiManager3DW;
import geogebra.geogebra3D.web.kernel3D.Kernel3DW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppWapplication;

import com.google.gwt.user.client.Command;

/**
 * for 3D
 * @author mathieu
 *
 */
public class AppWapplication3D extends AppWapplication {
	
	
	private EuclidianView3DW euclidianView3D;
	private EuclidianController3DW euclidianController3D;

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
		return new Kernel3DW(this_app);
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
		euclidianController3D = new EuclidianController3DW(kernel);
		euclidianView3D = new EuclidianView3DW(euclidianController3D, null);

	}
	
	
	@Override
    protected GuiManagerW newGuiManager() {
		return new GuiManager3DW(this);
	}


	private GCheckBoxMenuItem itemEuclidian3D;

	@Override
	public GCheckBoxMenuItem createMenuItemFor3D() {
		itemEuclidian3D = 
				new GCheckBoxMenuItem(GeoGebraMenubarW.getMenuBarHtml(AppResources.INSTANCE.view_graphics224()
						.getSafeUri().asString(), getPlain("GraphicsView3D"), true),
						new Command() {
					public void execute() {
						int viewId = App.VIEW_EUCLIDIAN3D;
						getGuiManager().setShowView(
								!getGuiManager().showView(viewId), viewId);
						itemEuclidian3D.setSelected(getGuiManager().showView(App.VIEW_EUCLIDIAN3D));
					}
				});

		return itemEuclidian3D;
	}
	

}
