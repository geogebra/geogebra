/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewCommon;
import org.geogebra.common.gui.view.data.PlotPanelEuclidianViewInterface;
import org.geogebra.common.gui.view.data.PlotSettings;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.panels.ProbabilityCalculatorDockPanelW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.gwtproject.user.client.ui.Widget;

/**
 * @author gabor
 *
 *Plot panel for ProbabilityCalculator
 */
public class PlotPanelEuclidianViewW extends EuclidianViewW
		implements PlotPanelEuclidianViewInterface {
	
	/**
	 * default height of the PlotPanelEuclidianViewW
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
		return 30 * getApplication().getFontSize() / 12.0;
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
