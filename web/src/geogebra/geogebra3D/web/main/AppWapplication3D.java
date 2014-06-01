package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian3D.EuclidianView3DInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.AppCompanion;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.EuclidianController3DW;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.euclidian3D.openGL.GLFactoryW;
import geogebra.geogebra3D.web.gui.GuiManager3DW;
import geogebra.html5.util.ArticleElement;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GeoGebraAppFrame;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.menubar.MainMenu;
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
		return App3DW.newKernel(this_app);
	}
	
	
	

	@Override
    public boolean hasEuclidianView3D() {
		return euclidianView3D != null;
	}

	@Override
    public EuclidianView3DInterface getEuclidianView3D() {
		//Window.alert("getEuclidianView3D()");
		return euclidianView3D;
	}
	
	@Override
	public void initEuclidianViews() {

		super.initEuclidianViews();
		euclidianController3D = App3DW.newEuclidianController3DW(kernel);
		euclidianView3D = App3DW.newEuclidianView3DW(euclidianController3D);

	}
	
	
	@Override
    protected GuiManagerW newGuiManager() {
		return App3DW.newGuiManager(this);
	}


	private GCheckBoxMenuItem itemEuclidian3D;

	@Override
	public GCheckBoxMenuItem createMenuItemFor3D() {
		itemEuclidian3D = 
				new GCheckBoxMenuItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE.view_graphics224()
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
	
	
	@Override
	public void recalculateEnvironments() {
		
		super.recalculateEnvironments();
		
	    if (getEuclidianView3D() != null) {
	    	getEuclidianView3D().getEuclidianController().calculateEnvironment();
	    }
	    
	    ((App3DCompanionW) companion).recalculateEnvironments();
    }
	
	
	@Override
    protected void initFactories(){
		
		super.initFactories();
		geogebra.common.geogebra3D.euclidian3D.openGL.GLFactory.prototype = new GLFactoryW();
	}
	
	
	@Override
    public void updateViewSizes(){
		super.updateViewSizes();
		if(((GuiManager3DW) getGuiManager()).getEuclidian3DPanel()!=null){
			((GuiManager3DW) getGuiManager()).getEuclidian3DPanel().deferredOnResize();
		}
		((App3DCompanionW) companion).updateViewSizes();
	}
	
	
	@Override
	public void updateStyleBars() {
		super.updateStyleBars();
		getEuclidianView3D().getStyleBar().updateStyleBar();
	}
	
	
	

	@Override
    public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel, EuclidianController ec, 
			boolean[] showAxes, boolean showGrid, int id, EuclidianSettings settings){
		return App3DW.newEuclidianView(evPanel, ec, showAxes, showGrid, id, settings);
	}

	@Override
	public EuclidianController newEuclidianController(Kernel kernel) {
		return App3DW.newEuclidianController(kernel);

	}
	

	@Override
    protected AppCompanion newAppCompanion(){
		return new App3DCompanionW(this);
	}

}
