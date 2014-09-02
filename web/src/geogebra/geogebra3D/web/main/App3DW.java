package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidian3D.EuclidianController3DW;
import geogebra.geogebra3D.web.euclidian3D.EuclidianView3DW;
import geogebra.geogebra3D.web.euclidian3DnoWebGL.EuclidianController3DWnoWebGL;
import geogebra.geogebra3D.web.euclidian3DnoWebGL.EuclidianView3DWnoWebGL;
import geogebra.geogebra3D.web.euclidianFor3D.EuclidianControllerFor3DW;
import geogebra.geogebra3D.web.euclidianFor3D.EuclidianViewFor3DW;
import geogebra.geogebra3D.web.gui.GuiManager3DW;
import geogebra.geogebra3D.web.kernel3D.Kernel3DW;
import geogebra.html5.Browser;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.gui.GuiManagerW;

/**
 * @author mathieu
 *
 */
public class App3DW {

	/**
	 * 
	 * @param this_app
	 * @return new kernel
	 */
	static final protected Kernel newKernel(App this_app){
		return new Kernel3DW(this_app);
	}
	
	/**
	 * 
	 * @param this_app
	 * @return new Gui manager
	 */
	static final protected GuiManagerW newGuiManager(AppW this_app) {
		return new GuiManager3DW(this_app);
	}
	
	/**
	 * 
	 * @param evPanel
	 * @param ec
	 * @param showAxes
	 * @param showGrid
	 * @param id
	 * @param settings
	 * @return new euclidian view
	 */
	static final public EuclidianViewW newEuclidianView(EuclidianPanelWAbstract evPanel, EuclidianController ec, 
			boolean[] showAxes, boolean showGrid, int id, EuclidianSettings settings){
		return new EuclidianViewFor3DW(evPanel, ec, showAxes, showGrid, id, settings);
	}

	/**
	 * 
	 * @param kernel
	 * @return new euclidian controller
	 */
	static final public EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerFor3DW(kernel);

	}
	
	/**
	 * 
	 * @param kernel kernel
	 * @return new controller for 3D view
	 */
	static final public EuclidianController3DW newEuclidianController3DW(Kernel kernel){
		if(Browser.supportsWebGL()){
			return new EuclidianController3DW(kernel);
		}
		
		return new EuclidianController3DWnoWebGL(kernel);
	}
		
	/**
	 * 
	 * @param ec controller for 3D view
	 * @return new 3D view
	 */
	static final public EuclidianView3DW newEuclidianView3DW(EuclidianController3DW ec, EuclidianSettings settings){
		if(Browser.supportsWebGL()){
			return new EuclidianView3DW(ec, settings);
		}
		
		return new EuclidianView3DWnoWebGL(ec, settings);
	}

}
