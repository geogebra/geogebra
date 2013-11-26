package geogebra.common.gui.view.data;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.View;

/**
 * @author gabor
 * common interface for plotpaneleuclidianviews
 *
 */
public interface PlotPanelEuclidianViewInterface extends View {
	
	/**
	 * @param kernel sets the View id
	 */
	public void setViewId(Kernel kernel);

}
