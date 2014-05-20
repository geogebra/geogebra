package geogebra.geogebra3D.web.main;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.geogebra3D.web.euclidianFor3D.EuclidianViewFor3DW;
import geogebra.geogebra3D.web.gui.GuiManager3DW;
import geogebra.geogebra3D.web.kernel3D.Kernel3DW;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

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
		App.debug("================= EuclidianViewFor3DW =====================");
		return new EuclidianViewFor3DW(evPanel, ec, showAxes, showGrid, id, settings);
	}

	/**
	 * 
	 * @param kernel
	 * @return new euclidian controller
	 */
	static final public EuclidianController newEuclidianController(Kernel kernel) {
		return new EuclidianControllerW(kernel);

	}
		
	
}
