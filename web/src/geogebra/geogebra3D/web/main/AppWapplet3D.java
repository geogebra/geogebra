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
import geogebra.html5.gui.laf.GLookAndFeel;
import geogebra.html5.util.ArticleElement;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.javax.swing.GCheckBoxMenuItem;
import geogebra.web.main.AppWapplet;

import java.util.HashMap;

public class AppWapplet3D extends AppWapplet{
	private EuclidianView3DW euclidianView3D;
	private EuclidianController3DW euclidianController3D;

	public AppWapplet3D(ArticleElement ae, GeoGebraFrame gf, GLookAndFeel laf) {
	    super(ae, gf, 3, laf);
    }

	@Override
    protected Kernel newKernel(App this_app){
		return App3DW.newKernel(this_app);
	}
	

	@Override
    public boolean hasEuclidianView3D() {
		return this.euclidianController3D != null;
	}

	@Override
    public EuclidianView3DInterface getEuclidianView3D() {
		//Window.alert("getEuclidianView3D()");
		if(this.euclidianView3D == null){
			euclidianView3D = App3DW.newEuclidianView3DW(euclidianController3D, getSettings().getEuclidian(3));
			App.printStacktrace("");
		}
		return euclidianView3D;
	}
	
	@Override
	public void initEuclidianViews() {

		super.initEuclidianViews();
		euclidianController3D = App3DW.newEuclidianController3DW(kernel);
		

	}
	
	
	@Override
    protected GuiManagerW newGuiManager() {
		return App3DW.newGuiManager(this);
	}


	private GCheckBoxMenuItem itemEuclidian3D;

	@Override
	public boolean supportsView(int viewID) {
		return true;
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
		if(showView(App.VIEW_EUCLIDIAN3D)){
			getEuclidianView3D().getStyleBar().updateStyleBar();
		}
	}
	
	@Override
    public boolean isEuclidianView3Dinited() {
		return euclidianView3D != null;
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
	

	@Override
    public void setCurrentFile(HashMap<String, String> file) {
		super.setCurrentFile(file);
		((EuclidianView3DW) getEuclidianView3D()).setCurrentFile(file);
	}
}
