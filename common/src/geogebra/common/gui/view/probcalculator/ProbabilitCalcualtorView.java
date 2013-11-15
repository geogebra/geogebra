package geogebra.common.gui.view.probcalculator;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

/**
 * @author gabor
 * 
 * Commmon view for ProbabilityCalculator
 *
 */
public abstract class ProbabilitCalcualtorView implements View {
	
	/**
	 * Application
	 */
	protected App app;
	/**
	 * Kernel
	 */
	protected Kernel kernel;
	/**
	 * Localization
	 */
	protected Localization loc;
	/**
	 * Construction
	 */
	protected Construction cons;
	
	/**
	 * @param app Application
	 */
	public ProbabilitCalcualtorView(App app) {
		this.app = app;
		this.loc = app.getLocalization();
		kernel = app.getKernel();
		cons = kernel.getConstruction();
	}
	

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void setMode(int mode, ModeSetter m) {
		// TODO Auto-generated method stub

	}

	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShowing() {
		// TODO Auto-generated method stub
		return false;
	}

}
