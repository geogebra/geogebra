package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import geogebra.common.gui.view.data.PlotSettings;
import geogebra.common.kernel.Kernel;
import geogebra.html5.awt.GDimensionW;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.layout.panels.ProbabilityCalculatorDockPanelW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW implements PlotPanelEuclidianViewInterface {
	
	/**
	 * default height ot PlotPanelEuclidianViewW
	 */
	public static int DEFAULT_HEIGHT = 300;
	
	private EuclidianControllerW ec;
	public PlotPanelEuclidianViewCommon commonFields;
	
	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianViewW(Kernel kernel, ScheduledCommand exportAction) {
		super(new PlotPanelEuclidianControllerW(kernel), PlotPanelEuclidianViewCommon.showAxes, PlotPanelEuclidianViewCommon.showGrid,
				null);

		
		
		if (commonFields == null) {
			setCommonFields();
		}
		
		// set preferred size so that updateSize will work and this EV can be
		// properly initialized
		setPreferredSize(new GDimensionW(ProbabilityCalculatorDockPanelW.DEFAULT_WIDTH, DEFAULT_HEIGHT));
		updateSize();

		
		
	}
	
	private void setCommonFields() {
		// set fields
				commonFields = new PlotPanelEuclidianViewCommon(
						false);	
				commonFields.setPlotSettings(new PlotSettings());

				setViewId(kernel);
				
				this.ec = this.getEuclidianController();
	}
	
	/*********** End Constructor **********************/

	/**
	 * Overrides EuclidianView setMode method so that no action is taken on a
	 * mode change.
	 */
	@Override
	public void setMode(int mode) {
		// .... do nothing
	}
	
	/** Returns viewID */
	@Override
	public int getViewID() {
		if (commonFields == null) {
			setCommonFields();
		}
		return commonFields.getViewID();
	}


	public void setViewId(Kernel kernel) {
	    // get viewID from GuiManager
		commonFields.setViewID(((GuiManagerW) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this));
    }

	public void setEVParams() {
	    commonFields.setEVParams(this);
    }

	public double getPixelOffset() {
		return (30 * getApplication().getFontSize()
				) / 12.0;
    }
	
	@Override
    public void updateSizeKeepDrawables() {
		super.updateSizeKeepDrawables();
	}
	
	

	public Widget getComponent() {
	    return EVPanel.getAbsolutePanel();
    }
	
	


}
