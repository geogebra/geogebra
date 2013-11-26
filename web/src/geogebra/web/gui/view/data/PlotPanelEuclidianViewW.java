package geogebra.web.gui.view.data;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.web.euclidian.EuclidianPanelWAbstract;
import geogebra.web.euclidian.EuclidianViewW;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW {

	public PlotPanelEuclidianViewW(EuclidianPanelWAbstract euclidianViewPanel,
            EuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid, int evNo, EuclidianSettings settings) {
	    super(euclidianViewPanel, euclidiancontroller, showAxes, showGrid, evNo,
	            settings);
	    // TODO Auto-generated constructor stub
    }

}
