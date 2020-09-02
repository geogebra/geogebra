package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW
		implements PlotPanelEuclidianViewInterface {
	
	/**
	 * default height ot PlotPanelEuclidianViewW
	 */
	public static final int DEFAULT_HEIGHT = 300;
	
	public PlotPanelEuclidianViewCommon commonFields;
	
	/*************************************************
	 * Construct the panel
	 */
	public PlotPanelEuclidianViewW(Kernel kernel) {
		super(new PlotPanelEuclidianControllerW(kernel), EVNO_GENERAL, null);
		
		if (commonFields == null) {
			setCommonFields();
		}
		
		// set preferred size so that updateSize will work and this EV can be
		// properly initialized
		setPreferredSize(new Dimension(
				ProbabilityCalculatorDockPanelW.DEFAULT_WIDTH, DEFAULT_HEIGHT));
		updateSize();
	}
	
	private void setCommonFields() {
		// set fields
		commonFields = new PlotPanelEuclidianViewCommon(false);
		commonFields.setPlotSettings(new PlotSettings());

		setViewId(kernel);
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

	@Override
	public void setViewId(Kernel kernel) {
	    // get viewID from GuiManager
		commonFields.setViewID(((GuiManagerW) kernel.getApplication().getGuiManager())
				.assignPlotPanelID(this));
    }

	@Override
	public void setEVParams() {
	    commonFields.setEVParams(this);
    }

	@Override
	public double getPixelOffset() {
		return (30 * getApplication().getFontSize()
				) / 12.0;
	}

	@Override
	public void updateSizeKeepDrawables() {
		super.updateSizeKeepDrawables();
	}
	
	/**
	 * @return panel wrapping the view
	 */
	public Widget getComponent() {
		return getAbsolutePanel();
	}

	@Override
	public boolean isPlotPanel() {
		return true;
	}

}
