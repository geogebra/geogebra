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

package org.geogebra.common.gui.view.data;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Common code used by plotPanelEuclidianViewsW/D
 * @author gabor
 */
public class PlotPanelEuclidianViewCommon {
	/**
	 * Plot panel viewID. This is not a constant; it is assigned by GuiManager.
	 */
	private int viewID;
	/**
	 * Flag to determine if the mouse is over the drag region, a thin rectangle
	 * at the top of the panel
	 */
	private boolean overDragRegion;
	/** Settings to control EuclidianView features (e.g. axes visibility) */
	private PlotSettings plotSettings;
	public static final boolean SHOW_GRID = false;
	public static final boolean[] SHOW_AXES = { true, true };

	/**
	 * @param overDragRegion
	 * 
	 *            Constructor
	 */
	public PlotPanelEuclidianViewCommon(boolean overDragRegion) {
		this.overDragRegion = overDragRegion;
	}

	/**
	 * @return view ID
	 */
	public int getViewID() {
		return viewID;
	}

	public void setViewID(int viewID) {
		this.viewID = viewID;
	}

	public boolean isOverDragRegion() {
		return overDragRegion;
	}

	public void setOverDragRegion(boolean overDragRegion) {
		this.overDragRegion = overDragRegion;
	}

	public PlotSettings getPlotSettings() {
		return plotSettings;
	}

	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}

	/**
	 * Sets the plotSettings field and updates the panel accordingly.
	 * 
	 * @param plotPanelEuclidianView
	 *            TODO
	 * @param settings
	 *            settings
	 */
	public void updateSettings(
			PlotPanelEuclidianViewInterface plotPanelEuclidianView,
			PlotSettings settings) {
		setPlotSettings(settings);
		plotPanelEuclidianView.setEVParams();
	}

	/**
	 * Uses the values stored in the plotSettings field to update the features
	 * of this EuclidianView (e.g. axes visibility)
	 * 
	 * @param plotPanelEuclidianViewD
	 *            TODO
	 */
	public void setEVParams(
			PlotPanelEuclidianViewInterface plotPanelEuclidianViewD) {
		plotPanelEuclidianViewD.showGrid(getPlotSettings().showGrid);
		plotPanelEuclidianViewD.setShowAxis(EuclidianViewInterfaceCommon.AXIS_Y,
				getPlotSettings().showYAxis, false);

		plotPanelEuclidianViewD.setShowAxis(EuclidianViewInterfaceCommon.AXIS_X,
				getPlotSettings().showXAxis, false);

		plotPanelEuclidianViewD.setLogAxis(EuclidianViewInterfaceCommon.AXIS_X,
				getPlotSettings().logXAxis, false);
		plotPanelEuclidianViewD.setLogAxis(EuclidianViewInterfaceCommon.AXIS_Y,
				getPlotSettings().logYAxis, false);

		plotPanelEuclidianViewD
				.setAutomaticGridDistance(getPlotSettings().gridIntervalAuto);
		if (!getPlotSettings().gridIntervalAuto) {
			plotPanelEuclidianViewD
					.setGridDistances(getPlotSettings().gridInterval);
		}

		if (getPlotSettings().showArrows) {
			plotPanelEuclidianViewD.setAxesLineStyle(
					EuclidianStyleConstants.AXES_LINE_TYPE_ARROW);
		} else {
			plotPanelEuclidianViewD.setAxesLineStyle(
					EuclidianStyleConstants.AXES_LINE_TYPE_FULL);
		}

		plotPanelEuclidianViewD.setDrawBorderAxes(getPlotSettings().isEdgeAxis);
		if (!getPlotSettings().isEdgeAxis[0]) {
			plotPanelEuclidianViewD.setAxisCross(0, 0);
		}
		if (!getPlotSettings().isEdgeAxis[1]) {
			plotPanelEuclidianViewD.setAxisCross(1, 0);
		}

		plotPanelEuclidianViewD
				.setPositiveAxes(getPlotSettings().isPositiveOnly);

		if (getPlotSettings().forceXAxisBuffer) {
			// ensure that the axis labels are shown
			// by forcing a fixed pixel height below the x-axis
			double pixelOffset = plotPanelEuclidianViewD.getPixelOffset();
			double pixelHeight = plotPanelEuclidianViewD.getHeight();
			getPlotSettings().yMin = -pixelOffset * getPlotSettings().yMax
					/ (pixelHeight + pixelOffset);
		}

		plotPanelEuclidianViewD.setAxesCornerCoordsVisible(false);

		plotPanelEuclidianViewD.setAutomaticAxesNumberingDistance(
				getPlotSettings().xAxesIntervalAuto, 0);
		plotPanelEuclidianViewD.setAutomaticAxesNumberingDistance(
				getPlotSettings().yAxesIntervalAuto, 1);
		Construction cons = plotPanelEuclidianViewD.getApplication().getKernel()
				.getConstruction();
		if (!getPlotSettings().xAxesIntervalAuto) {
			plotPanelEuclidianViewD.setAxesNumberingDistance(
					new GeoNumeric(cons, getPlotSettings().xAxesInterval), 0);
		} else {
			getPlotSettings().xAxesInterval = plotPanelEuclidianViewD
					.getAxesNumberingDistances()[0];
		}
		if (!getPlotSettings().yAxesIntervalAuto) {
			plotPanelEuclidianViewD.setAxesNumberingDistance(
					new GeoNumeric(cons, getPlotSettings().yAxesInterval), 1);
		} else {
			getPlotSettings().yAxesInterval = plotPanelEuclidianViewD
					.getAxesNumberingDistances()[1];
		}

		plotPanelEuclidianViewD
				.setPointCapturing(getPlotSettings().pointCaptureStyle);

		// do this last ?

		plotPanelEuclidianViewD.setRealWorldCoordSystem(
				getPlotSettings().logXAxis && getPlotSettings().xMin <= 0 ? 0.1
						: getPlotSettings().xMin,
				getPlotSettings().xMax,
				getPlotSettings().logYAxis && getPlotSettings().yMin <= 0 ? 0.1
						: getPlotSettings().yMin,
				getPlotSettings().yMax);

		plotPanelEuclidianViewD.repaint();
	}

	/**
	 * Update plot size
	 * 
	 * @param plotPanelEView
	 *            plot panel view
	 */
	public void updateSize(PlotPanelEuclidianViewInterface plotPanelEView) {
		// record the old coord system
		double xminTemp = plotPanelEView.getXmin();
		double xmaxTemp = plotPanelEView.getXmax();
		double yminTemp = plotPanelEView.getYmin();
		double ymaxTemp = plotPanelEView.getYmax();

		plotPanelEView.updateSizeKeepDrawables();

		// now reset the coord system so that our view dimensions are restored
		// using the new scaling factors.
		plotPanelEView.setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp,
				ymaxTemp);

	}
}