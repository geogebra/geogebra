package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.kernel.Kernel;
import geogebra.html5.awt.GDimensionW;
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

		setViewId(kernel);
		
		plotPanelEV = this;
		this.ec = this.getEuclidianController();
		this.exportToEvAction = exportAction;
		
		/*Mouse handling needed? We will see...
		 * 
		 * 
		 * // enable/disable mouseListeners
		setMouseEnabled(false, true);
		setMouseMotionEnabled(false);
		setMouseWheelEnabled(false);
		this.addMouseMotionListener(new MyMouseMotionListener());

		 * 
		 * 
		 * 
		 * 
		 */
		
		// set preferred size so that updateSize will work and this EV can be
		// properly initialized
		setPreferredSize(new GDimensionW(300, 200));
		updateSize();

		
		
	}

	public void setViewId(Kernel kernel) {
	    // get viewID from GuiManager
		commonFields.setViewID(((GuiManagerW) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this));
    }

	public void setEVParams() {
	    // TODO Auto-generated method stub
	    
    }

	public double getPixelOffset() {
		return (30 * getApplication().getFontSize()
				) / 12.0;
    }


}
