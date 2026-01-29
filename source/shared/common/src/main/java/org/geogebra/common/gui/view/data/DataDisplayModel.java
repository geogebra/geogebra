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

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.common.kernel.statistics.Regression;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayModel {
	private App app;
	private DataAnalysisModel daModel;
	private StatGeo statGeo;
	// currently selected plot type
	//private PlotType selectedPlot;

	private StatPanelSettings settings;

	private ArrayList<GeoElementND> plotGeoList;

	private GeoElement[] boxPlotTitles;
	private GeoElementND frequencyPolygon;
	private GeoElementND histogram;
	private GeoElementND barChart;
	private GeoElementND scatterPlotLine;
	private GeoElement dotPlot;
	private GeoElement normalCurve;
	private GeoElement scatterPlot;
	private GeoElement residualPlot;
	private GeoElement nqPlot;
	private GeoElement boxPlot;
	private GeoElement freqTableGeo;

	private boolean hasControlPanel = true;
	private IDataDisplayListener listener;

	// 0 or 1
	// can have two open at once
	private int id;

	/**
	 * UI delegate for the model.
	 */
	public interface IDataDisplayListener {

		/**
		 * Add display type item.
		 * @param plotType plot type
		 */
		void addDisplayTypeItem(PlotType plotType);

		/**
		 * Update the scatter plot.
		 */
		void updateScatterPlot();

		/**
		 * Update the frequency table.
		 */
		void updateFrequencyTable();

		/**
		 * @param type plot type
		 */
		void setSelectedType(PlotType type);

		/**
		 * Show the control panel.
		 */
		void showControlPanel();

		/**
		 * Show the options button.
		 */
		void setOptionsButtonVisible();

		/**
		 * Show invalid data fallback.
		 */
		void showInvalidDataDisplay();

		/**
		 * Set table from frequency table
		 * @param frequencyTable frequency table
		 * @param useClasses whether to use classes
		 */
		void setTableFromGeoFrequencyTable(AlgoFrequencyTable frequencyTable,
				boolean useClasses);

		@MissingDoc
		void updatePlotPanelSettings();

		@MissingDoc
		void showManualClassesPanel();

		@MissingDoc
		void showNumClassesPanel();

		@MissingDoc
		void showPlotPanel();

		/**
		 * Update the stem plot.
		 * @param latex LaTeX content of the stem plot
		 */
		void updateStemPlot(String latex);

		/**
		 * Update XY tiles
		 * @param isPointList whether to use point list
		 * @param isLeftToRight whether it is left to right
		 */
		void updateXYTitles(boolean isPointList, boolean isLeftToRight);

		/**
		 * Add element to plot panel
		 * @param listGeo element to add
		 */
		void geoToPlotPanel(GeoElement listGeo);

		/**
		 * Remove frequency table.
		 */
		void removeFrequencyTable();

		/**
		 * Resize content to fit.
		 */
		void resize();

	}

	/**
	 * Plot types, keys for these need to be in "menu" category of ggbtrans
	 */
	public enum PlotType {
		HISTOGRAM("Histogram"),

		BOXPLOT("Boxplot"),

		DOTPLOT("DotPlot"),

		NORMALQUANTILE("NormalQuantilePlot"),

		STEMPLOT("StemPlot"),

		BARCHART("BarChart"),

		SCATTERPLOT("Scatterplot"),

		RESIDUAL("ResidualPlot"),

		MULTIBOXPLOT("StackedBoxPlots");

		/**
		 * the associated key from menu.properties app.getMenu(key) gives the
		 * translation (for the menu) in the current locale
		 */
		final private String key;

		PlotType(String key) {
			this.key = key;

		}

		final public String getKey() {
			return key;
		}

		/**
		 * @param loc
		 *            localization
		 * @return translated key for the current locale eg "StemPlot" -&gt;
		 *         "Stem and Leaf Diagram" in en_GB
		 */
		public String getTranslatedKey(Localization loc) {
			return loc.getMenu(key);
		}

	}

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daModel
	 *            daModel
	 * @param listener
	 *            change listener
	 * @param id
	 *            id 0 or 1 (within the DA view)
	 */
	public DataDisplayModel(DataAnalysisModel daModel,
			IDataDisplayListener listener, int id) {

		this.daModel = daModel;
		this.app = daModel.getApp();
		this.statGeo = daModel.getStatGeo();
		this.listener = listener;
		this.id = id;
		plotGeoList = new ArrayList<>();

		// create settings
		settings = new StatPanelSettings();
		settings.setDataSource(daModel.getDataSource());

	}

	/**
	 * @param index
	 *            plot type
	 * @param mode
	 *            app mode
	 */
	public void updatePlot(PlotType index, int mode) {
		daModel.setMode(mode);
		setSelectedPlot(index);
		getSettings().setDataSource(daModel.getDataSource());
	}

	/**
	 * Fill display types
	 */
	public void fillDisplayTypes() {
		switch (daModel.getMode()) {
		default:
		case DataAnalysisModel.MODE_ONEVAR:

			if (!daModel.isNumericData()) {
				listener.addDisplayTypeItem(PlotType.BARCHART);
			}

			else if (getSettings().groupType() == GroupType.RAWDATA) {
				listener.addDisplayTypeItem(PlotType.HISTOGRAM);
				listener.addDisplayTypeItem(PlotType.BARCHART);
				listener.addDisplayTypeItem(PlotType.BOXPLOT);
				listener.addDisplayTypeItem(PlotType.DOTPLOT);
				listener.addDisplayTypeItem(PlotType.STEMPLOT);
				listener.addDisplayTypeItem(PlotType.NORMALQUANTILE);

			}

			else if (getSettings().groupType() == GroupType.FREQUENCY) {
				listener.addDisplayTypeItem(PlotType.HISTOGRAM);
				listener.addDisplayTypeItem(PlotType.BARCHART);
				listener.addDisplayTypeItem(PlotType.BOXPLOT);

			} else if (getSettings().groupType() == GroupType.CLASS) {
				listener.addDisplayTypeItem(PlotType.HISTOGRAM);
			}

			break;

		case DataAnalysisModel.MODE_REGRESSION:
			listener.addDisplayTypeItem(PlotType.SCATTERPLOT);
			listener.addDisplayTypeItem(PlotType.RESIDUAL);
			break;

		case DataAnalysisModel.MODE_MULTIVAR:
			listener.addDisplayTypeItem(PlotType.MULTIBOXPLOT);
			break;
		}
		listener.setSelectedType(getSelectedPlot());
	}

	/**
	 * Updates the plot panel. Adds/removes additional panels as needed for the
	 * current selected plot.
	 */
	public void updatePlotPanelLayout() {

		if (getSelectedPlot() == PlotType.SCATTERPLOT) {
			listener.updateScatterPlot();
		}

		else if (getSelectedPlot() == PlotType.HISTOGRAM
				|| getSelectedPlot() == PlotType.BARCHART) {

			if (getSettings().isShowFrequencyTable()) {
				listener.updateFrequencyTable();
			}
		}

	}

	// ==============================================
	// DISPLAY UPDATE
	// ==============================================

	/**
	 * Update the plot.
	 * 
	 * @param doCreate
	 *            if true then the plot GeoElements are redefined
	 */
	public void updatePlot(boolean doCreate) {

		GeoList dataListSelected = daModel.getController().getDataSelected();
		if (dataListSelected == null) {
			Log.debug("[DDMODEL] dataListSelected is null!");
			return;
		}

		if (hasControlPanel) {
			listener.showControlPanel();
		}

		if (doCreate) {
			clearPlotGeoList();
		}

		listener.setOptionsButtonVisible();

		updatePlotPanelLayout();

		// if invalid data, show blank plot and exit
		if (!daModel.getController().isValidData()) {
			listener.showInvalidDataDisplay();
			return;
		}

		try {
			switch (getSelectedPlot()) {

			case HISTOGRAM:

				if (doCreate) {
					if (histogram != null) {
						histogram.remove();
					}

					histogram = statGeo.createHistogram(dataListSelected,
							settings, false);
					plotGeoList.add(histogram);

					if (frequencyPolygon != null) {
						frequencyPolygon.remove();
					}
					if (settings.isHasOverlayPolygon()) {
						frequencyPolygon = statGeo.createHistogram(
								dataListSelected, settings, true);
						plotGeoList.add(frequencyPolygon);
					}

					if (normalCurve != null) {
						normalCurve.remove();
					}
					if (settings.isHasOverlayNormal() && settings.isOverlayEnabled()) {
						normalCurve = statGeo
								.createNormalCurveOverlay(dataListSelected);
						plotGeoList.add(normalCurve);
					}

					if (freqTableGeo != null) {
						freqTableGeo.remove();
					}
					if (settings.isShowFrequencyTable()) {
						freqTableGeo = statGeo.createFrequencyTableGeo(
								(GeoNumeric) histogram, getSelectedPlot());
						plotGeoList.add(freqTableGeo);
					}
				}

				// update the frequency table
				if (settings.isShowFrequencyTable()) {
					listener.setTableFromGeoFrequencyTable(
							(AlgoFrequencyTable) freqTableGeo
									.getParentAlgorithm(),
							true);
				} else {
					listener.removeFrequencyTable();
				}
				// update settings
				if (histogram != null) {
					statGeo.getHistogramSettings(dataListSelected, histogram,
							settings);
				}

				listener.updatePlotPanelSettings();

				if (hasControlPanel && settings.getDataSource()
						.getGroupType() != GroupType.CLASS) {
					if (settings.isUseManualClasses()) {
						listener.showManualClassesPanel();
					} else {
						listener.showNumClassesPanel();

					}
				}

				listener.showPlotPanel();
				break;

			case BOXPLOT:
				if (doCreate) {
					if (boxPlot != null) {
						boxPlot.remove();
					}
					boxPlot = statGeo.createBoxPlot(dataListSelected, settings);
					plotGeoList.add(boxPlot);
				}
				statGeo.getBoxPlotSettings(dataListSelected, settings);
				listener.updatePlotPanelSettings();
				listener.showPlotPanel();
				break;

			case BARCHART:
				if (doCreate) {
					if (barChart != null) {
						barChart.remove();
					}
					if (settings.isNumericData()) {
						barChart = statGeo.createBarChartNumeric(
								dataListSelected, settings);
						plotGeoList.add(barChart);
					} else {
						barChart = statGeo.createBarChartText(dataListSelected,
								settings);
					}
					plotGeoList.add(barChart);

					if (freqTableGeo != null) {
						freqTableGeo.remove();
					}
					if (settings.isShowFrequencyTable()) {
						freqTableGeo = statGeo.createFrequencyTableGeo(
								(GeoNumeric) barChart, getSelectedPlot());
						plotGeoList.add(freqTableGeo);
					}
					listener.resize();
				}

				// update the frequency table
				if (settings.isShowFrequencyTable()) {
					listener.setTableFromGeoFrequencyTable(
							(AlgoFrequencyTable) freqTableGeo
									.getParentAlgorithm(),
							false);
				}

				// update settings
				statGeo.getBarChartSettings(dataListSelected, settings,
						barChart);
				listener.updatePlotPanelSettings();
				listener.showPlotPanel();
				break;

			case DOTPLOT:
				if (doCreate) {
					if (dotPlot != null) {
						dotPlot.remove();
					}
					dotPlot = statGeo.createDotPlot(dataListSelected);
					plotGeoList.add(dotPlot);
				}

				statGeo.updateDotPlot(dataListSelected, dotPlot, settings);
				listener.updatePlotPanelSettings();
				listener.showPlotPanel();
				break;

			case STEMPLOT:
				String latex = statGeo.getStemPlotLatex(dataListSelected,
						settings.getStemAdjust());
				listener.updateStemPlot(latex);
				break;

			case NORMALQUANTILE:
				if (doCreate) {
					if (nqPlot != null) {
						nqPlot.remove();
					}
					nqPlot = statGeo.createNormalQuantilePlot(dataListSelected);
					plotGeoList.add(nqPlot);
				}
				statGeo.updateNormalQuantilePlot(dataListSelected, settings);
				listener.updatePlotPanelSettings();
				listener.showPlotPanel();
				break;

			case SCATTERPLOT:

				if (doCreate) {
					Log.debug("[DDMODEL]  UPDATE SCATTERPLOT");
					scatterPlot = statGeo.createScatterPlot(dataListSelected);
					plotGeoList.add(scatterPlot);

					if (daModel.getRegressionModel() != null && !daModel
							.getRegressionMode().equals(Regression.NONE)) {
						plotGeoList.add(daModel.getRegressionModel());
					}

					if (settings.isShowScatterplotLine()) {
						scatterPlotLine = statGeo
								.createScatterPlotLine((GeoList) scatterPlot);
						plotGeoList.add(scatterPlotLine);
					}
				}

				// update xy title fields
				listener.updateXYTitles(settings.isPointList(),
						daModel.getDaCtrl().isLeftToRight());

				// update settings
				statGeo.getScatterPlotSettings(dataListSelected, settings);
				listener.updatePlotPanelSettings();
				listener.showPlotPanel();

				break;

			case RESIDUAL:
				if (doCreate) {
					if (!daModel.getRegressionMode().equals(Regression.NONE)) {
						residualPlot = statGeo.createRegressionPlot(
								dataListSelected, daModel.getRegressionMode(),
								daModel.getRegressionOrder(), true);
						plotGeoList.add(residualPlot);
						statGeo.getResidualPlotSettings(dataListSelected,
								residualPlot, settings);
						listener.updatePlotPanelSettings();
					} else if (residualPlot != null) {
						residualPlot.remove();
						residualPlot = null;
					}
				}
				listener.showPlotPanel();
				break;

			case MULTIBOXPLOT:
				if (doCreate) {
					GeoElement[] boxPlots = statGeo
							.createMultipleBoxPlot(dataListSelected, settings);
					plotGeoList.addAll(Arrays.asList(boxPlots));
				}

				statGeo.getMultipleBoxPlotSettings(dataListSelected, settings);
				listener.updatePlotPanelSettings();
				boxPlotTitles = statGeo.createBoxPlotTitles(daModel, settings);
				plotGeoList.addAll(Arrays.asList(boxPlotTitles));

				listener.showPlotPanel();
				break;

			default:

			}

			// ==============================================
			// Prepare Geos for plot panel view
			// ==============================================

			if (doCreate && statGeo.removeFromConstruction()) {
				for (GeoElementND listGeo : plotGeoList) {
					// add the geo to our view and remove it from EV
					listener.geoToPlotPanel(listGeo.toGeoElement());

				}
			}

			if (freqTableGeo != null) {
				freqTableGeo.setEuclidianVisible(false);
				freqTableGeo.updateRepaint();
			}

			if (histogram != null) {
				histogram.setEuclidianVisible(settings.isShowHistogram()
						&& getSelectedPlot() == PlotType.HISTOGRAM);
				histogram.updateRepaint();
			}

		} catch (RuntimeException | StatException e) {
			daModel.getDaCtrl().setValidData(false);
			listener.showInvalidDataDisplay();
			Log.debug(e);
		}

	}

	/**
	 * Remove plot geos from construction and this model
	 */
	public void clearPlotGeoList() {
		for (GeoElementND geo : plotGeoList) {
			if (geo != null) {
				geo.remove();
				geo = null;
			}
		}
		plotGeoList.clear();
	}

	/**
	 * Exports all GeoElements that are currently displayed in this panel to a
	 * target EuclidianView.
	 * 
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	public void exportGeosToEV(int euclidianViewID) {

		// TODO:
		// in multivar mode create dynamic boxplots linked to separate lists

		app.setWaitCursor();
		EuclidianView targetEV = (EuclidianView) app.getView(euclidianViewID);

		try {

			// =================================================================
			// Step 1:
			// Update the plot geos with the removeFromConstruction
			// flag set to false. This ensures that the display geos have been
			// put in the construction list and will be saved to xml.
			// =================================================================
			daModel.getController().loadDataLists(false); // load actual data
															// lists
			daModel.getStatGeo().setRemoveFromConstruction(false);
			updatePlot(true);

			// =================================================================
			// Step 2:
			// Prepare the geos for display in the currently active EV
			// (set labels, make visible, etc).
			// =================================================================

			// remove the histogram from the plot geo list if it is not showing
			if (histogram != null && !settings.isShowHistogram()) {
				plotGeoList.remove(histogram);
				histogram.remove();
				histogram = null;
			}

			// prepare all display geos to appear in the EV
			for (GeoElementND geo : plotGeoList) {
				prepareGeoForEV(geo, euclidianViewID);
			}

			// the regression geo is maintained by the da view, so we create a
			// copy and prepare this for the EV
			if (daModel.isRegressionMode()
					&& !daModel.getRegressionMode().equals(Regression.NONE)) {

				GeoElement regressionCopy = statGeo.createRegressionPlot(
						(GeoList) scatterPlot, daModel.getRegressionMode(),
						daModel.getRegressionOrder(), false);
				prepareGeoForEV(regressionCopy, euclidianViewID);
			}

			// =================================================================
			// Step 3:
			// Adjust the target EV window to match the plotPanel dimensions
			// =================================================================

			targetEV.setRealWorldCoordSystem(settings.xMin, settings.xMax,
					settings.yMin, settings.yMax);
			targetEV.setAutomaticAxesNumberingDistance(
					settings.xAxesIntervalAuto, 0);
			targetEV.setAutomaticAxesNumberingDistance(
					settings.yAxesIntervalAuto, 1);
			Construction cons = app.getKernel().getConstruction();
			if (!settings.xAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(
						new GeoNumeric(cons, settings.xAxesInterval), 0);
			}
			if (!settings.yAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(
						new GeoNumeric(cons, settings.yAxesInterval), 1);
			}
			targetEV.updateBackground();

			// =================================================================
			// Step 4:
			// Dereference the geos from fields in this class and the
			// DataAnalysisView
			// =================================================================

			// null the display geos
			boxPlotTitles = null;
			histogram = null;
			dotPlot = null;
			frequencyPolygon = null;
			normalCurve = null;
			scatterPlotLine = null;
			scatterPlot = null;
			nqPlot = null;
			boxPlot = null;
			barChart = null;
			freqTableGeo = null;

			daModel.getController().removeRegressionGeo();
			daModel.getController().disposeDataListSelected();
			plotGeoList.clear();

			// =================================================================
			// Step 5:
			// Reload the data and create new display geos that are not
			// in the construction list.
			// =================================================================

			daModel.getController().loadDataLists(true);
			statGeo.setRemoveFromConstruction(true);
			daModel.getController().setRegressionGeo();
			daModel.getController().updateRegressionPanel();
			updatePlot(true);

		} catch (RuntimeException e) {
			Log.debug(e);
			app.setDefaultCursor();
		}

		app.setDefaultCursor();
		app.storeUndoInfo();
	}

	/**
	 * Prepares the specified GeoElement for visibility in a target
	 * EuclidianView.
	 * 
	 * @param geo
	 *            plot element
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	private static void prepareGeoForEV(GeoElementND geo, int euclidianViewID) {
		geo.setEuclidianVisible(true);
		geo.setAuxiliaryObject(false);
		if (euclidianViewID == App.VIEW_EUCLIDIAN) {
			geo.addView(App.VIEW_EUCLIDIAN);
			geo.removeView(App.VIEW_EUCLIDIAN2);
		}
		if (euclidianViewID == App.VIEW_EUCLIDIAN2) {
			geo.addView(App.VIEW_EUCLIDIAN2);
			geo.removeView(App.VIEW_EUCLIDIAN);
		}
		if (geo.getParentAlgorithm() != null) {
			geo.getConstruction().addToConstructionList(geo.getParentAlgorithm(), true);
		}
		if (geo.isLabelSet()) {
			geo.update();
		} else {
			geo.setLabel(null);
		}
	}

	public StatPanelSettings getSettings() {
		return settings;
	}

	/**
	 * @return selected plot
	 */
	public PlotType getSelectedPlot() {
		return app.getSettings().getDataAnalysis().getPlotType(id,
				id == 0 ? PlotType.BARCHART : PlotType.BOXPLOT);
	}

	/**
	 * Change selected plot type.
	 * @param selectedPlot plot type
	 */
	public void setSelectedPlot(PlotType selectedPlot) {
		app.getSettings().getDataAnalysis().setPlotType(id, selectedPlot);
	}

}
