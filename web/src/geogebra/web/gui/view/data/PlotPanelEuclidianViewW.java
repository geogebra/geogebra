package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.kernel.Kernel;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.panels.EuclidianDockPanelW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW implements PlotPanelEuclidianViewInterface {
	
	private final EuclidianControllerW ec;
	private final PlotPanelEuclidianViewW plotPanelEV;

	private PlotPanelEuclidianViewCommon commonFields = new PlotPanelEuclidianViewCommon(
			false);
	
	/**
	 * Action method export of GeoElements to EuclidianView. Since the action is
	 * specific to the parent container, it is injected in the constructor.
	 * */
	
	private ScheduledCommand exportToEvAction;

	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianViewW(Kernel kernel, ScheduledCommand exportAction) {
		super(new EuclidianDockPanelW(false), new PlotPanelEuclidianControllerW(kernel), PlotPanelEuclidianViewCommon.showAxes, PlotPanelEuclidianViewCommon.showGrid,
				1, null);

		// set fields
		commonFields = new PlotPanelEuclidianViewCommon(
				false);
		
		commonFields.setPlotSettings(new PlotSettings());

		// get viewID from GuiManager
		commonFields.setViewID(((GuiManagerW) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this));
		
		plotPanelEV = this;
		this.ec = this.getEuclidianController();
		this.exportToEvAction = exportAction;
	}


}
