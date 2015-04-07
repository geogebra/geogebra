package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.panels.ProbabilityCalculatorDockPanelW;

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
				EVNO_GENERAL, null);

	
		
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
	
	@Override
	public boolean isPlotPanel() {
		return true;
	}


}
