package org.geogebra.common.gui.view.data;

import java.util.ArrayList;
//import geogebra.common.kernel.geos.GeoList;
//import geogebra.common.kernel.geos.GeoNumeric;
//import geogebra.common.kernel.statistics.AlgoFrequencyTable;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.data.DataAnalysisModel.Regression;
import org.geogebra.common.gui.view.data.DataVariable.GroupType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.statistics.AlgoFrequencyTable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * DataAnalysisView.
 * 
 * @author G.Sturr
 * 
 */
public class DataDisplayModel {
	public interface IDataDisplayListener {

		void addDisplayTypeItem(PlotType histogram);

		void updateScatterPlot();

		void updateFrequencyTable();

		void setSelectedType(PlotType type);

		void showControlPanel();

		void setOptionsButtonVisible();

		void showInvalidDataDisplay();

		void setTableFromGeoFrequencyTable(AlgoFrequencyTable parentAlgorithm,
				boolean b);

		void updatePlotPanelSettings();

		void showManualClassesPanel();

		void showNumClassesPanel();

		void showPlotPanel();

		void updateStemPlot(String latex);

		void updateXYTitles(boolean isPointList, boolean isLeftToRight);

		void geoToPlotPanel(GeoElement listGeo);

		void removeFrequencyTable();

		void resize();

//		void updatePlot(boolean b);

	};

	private static final long serialVersionUID = 1L;
	//
	//	// ggb fields
	private App app;
	private Localization loc;
	private DataAnalysisModel daModel;
	private StatGeo statGeo;
	//
	//	// data view mode
	//	private int mode;
	//
	@SuppressWarnings("javadoc")
	// keys for these need to be in "menu" category of ggbtrans
	public enum PlotType {
		HISTOGRAM("Histogram"), BOXPLOT("Boxplot"), DOTPLOT("DotPlot"), NORMALQUANTILE(
				"NormalQuantilePlot"), STEMPLOT("StemPlot"), BARCHART(
						"BarChart"), SCATTERPLOT("Scatterplot"), RESIDUAL(
				"ResidualPlot"), MULTIBOXPLOT(
				"StackedBoxPlots");

		/**
		 * the associated key from menu.properties app.getMenu(key) gives the
		 * translation (for the menu) in the current locale
		 */
		public String key;

		PlotType(String key) {
			this.key = key;

		}

		/**
		 * @param app
		 *            App
		 * @return translated key for the current locale eg "StemPlot" ->
		 *         "Stem and Leaf Diagram" in en_GB
		 */
		public String getTranslatedKey(App app) {
			return app.getMenu(key);
		}

	}

	// currently selected plot type
	private PlotType selectedPlot;

	private StatPanelSettings settings;

	private ArrayList<GeoElement> plotGeoList;

	private GeoElement[] boxPlotTitles;
	private GeoElement histogram, dotPlot, frequencyPolygon, normalCurve,
			scatterPlot, scatterPlotLine, residualPlot, logarithmicPlot,
			nqPlot, boxPlot,
	barChart, freqTableGeo;

	private boolean hasControlPanel = true;
	private IDataDisplayListener listener;

	/*****************************************
	 * Constructs a ComboStatPanel
	 * 
	 * @param daModel
	 *            daModel
	 */
	public DataDisplayModel(DataAnalysisModel daModel, IDataDisplayListener listener) {

		this.daModel = daModel;
		this.app = daModel.getApp();
		this.loc = app.getLocalization();
		this.statGeo = daModel.getStatGeo();
		this.listener = listener;
		plotGeoList = new ArrayList<GeoElement>();

		// create settings
		settings = new StatPanelSettings();
		settings.setDataSource(daModel.getDataSource());

	}

	public void updatePlot(PlotType index, int mode) {

		setMode(mode);
		this.setSelectedPlot(index);
		getSettings().setDataSource(daModel.getDataSource());
	}

	public void fillDisplayTypes() {
		switch (daModel.getMode()) {
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
			if(dataListSelected == null){
				App.debug("[DDMODEL] dataListSelected is null!");
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
				switch (selectedPlot) {
	
				case HISTOGRAM:
	
					if (doCreate) {
						if (histogram != null) {
							histogram.remove();
						}
						if (dataListSelected != null) {
						histogram = statGeo.createHistogram(dataListSelected,
								settings, false);
						plotGeoList.add(histogram);}
	
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
						if (settings.isHasOverlayNormal()) {
							normalCurve = statGeo
									.createNormalCurveOverlay(dataListSelected);
							plotGeoList.add(normalCurve);
						}
	
						if (freqTableGeo != null) {
							freqTableGeo.remove();
						}
						if (settings.isShowFrequencyTable()) {
							freqTableGeo = statGeo.createFrequencyTableGeo(
									(GeoNumeric) histogram, selectedPlot);
							plotGeoList.add(freqTableGeo);
						}
					}
					
					// update the frequency table
					if (settings.isShowFrequencyTable()) {
						listener.setTableFromGeoFrequencyTable(
								(AlgoFrequencyTable) freqTableGeo
										.getParentAlgorithm(), true);
					} else {
						listener.removeFrequencyTable();
					}
	
					// update settings
					if (dataListSelected != null) {
						statGeo.getHistogramSettings(dataListSelected, histogram, settings);

					}
							
					listener.updatePlotPanelSettings();
				
					if (hasControlPanel
							&& settings.getDataSource().getGroupType() != GroupType.CLASS)
						if (settings.isUseManualClasses()) {
							listener.showManualClassesPanel();
						} else {
							listener.showNumClassesPanel();
							
						}
					
					listener.showPlotPanel();
					break;
	
				case BOXPLOT:
					if (doCreate) {
						if (boxPlot != null)
							boxPlot.remove();
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
									(GeoNumeric) barChart, selectedPlot);
							plotGeoList.add(freqTableGeo);
						}
						listener.resize();
					}
	
					// update the frequency table
					if (settings.isShowFrequencyTable()) {
						listener.setTableFromGeoFrequencyTable(
								(AlgoFrequencyTable) freqTableGeo
										.getParentAlgorithm(), false);
					}
	
					// update settings
					statGeo.getBarChartSettings(dataListSelected, settings,
							barChart);
					listener.updatePlotPanelSettings();
					listener.showPlotPanel();
					break;
	
				case DOTPLOT:
					if (doCreate) {
						if (dotPlot != null)
							dotPlot.remove();
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
						if (nqPlot != null)
							nqPlot.remove();
						nqPlot = statGeo.createNormalQuantilePlot(dataListSelected);
						plotGeoList.add(nqPlot);
					}
					statGeo.updateNormalQuantilePlot(dataListSelected, settings);
					listener.updatePlotPanelSettings();
					listener.showPlotPanel();
					break;
	
				case SCATTERPLOT:

					if (doCreate) {
						App.debug("[DDMODEL]  UPDATE SCATTERPLOT");
					scatterPlot = statGeo.createScatterPlot(dataListSelected);
						plotGeoList.add(scatterPlot);
	
						if (daModel.getRegressionModel() != null
								&& !daModel.getRegressionMode().equals(
										Regression.NONE)) {
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
						GeoElement[] boxPlots = statGeo.createMultipleBoxPlot(
								dataListSelected, settings);
						for (int i = 0; i < boxPlots.length; i++)
							plotGeoList.add(boxPlots[i]);
					}
	
					statGeo.getMultipleBoxPlotSettings(dataListSelected, settings);
					listener.updatePlotPanelSettings();
					boxPlotTitles = statGeo.createBoxPlotTitles(daModel, settings);
					for (int i = 0; i < boxPlotTitles.length; i++)
						plotGeoList.add(boxPlotTitles[i]);
	
					listener.showPlotPanel();
					break;
	
				default:
	
				}
	
				// ==============================================
				// Prepare Geos for plot panel view
				// ==============================================
	
				if (doCreate && statGeo.removeFromConstruction()) {
					for (GeoElement listGeo : plotGeoList) {
						// add the geo to our view and remove it from EV
						listener.geoToPlotPanel(listGeo);
					
					}
				}
	
				if (freqTableGeo != null) {
					freqTableGeo.setEuclidianVisible(false);
					freqTableGeo.updateRepaint();
				}
	
				if (histogram != null) {
					histogram.setEuclidianVisible(settings.isShowHistogram() && selectedPlot == PlotType.HISTOGRAM);
					histogram.updateRepaint();
				}
	
			} catch (Exception e) {
				daModel.getDaCtrl().setValidData(false);
				listener.showInvalidDataDisplay();
				e.printStackTrace();
			}
	
		}
	

	public void clearPlotGeoList() {
		for (GeoElement geo : plotGeoList) {
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
		// app.storeUndoInfo();
		GeoElement regressionCopy = null;
		EuclidianView targetEV = (EuclidianView) app.getView(euclidianViewID);

		try {

			// =================================================================
			// Step 1:
			// Update the plot geos with the reomoveFromConstruction
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
			if (histogram != null && settings.isShowHistogram() == false) {
				plotGeoList.remove(histogram);
				histogram.remove();
				histogram = null;
			}

			// prepare all display geos to appear in the EV
			for (GeoElement geo : plotGeoList) {
				prepareGeoForEV(geo, euclidianViewID);
			}

			// the regression geo is maintained by the da view, so we create a
			// copy and prepare this for the EV
			if (daModel.isRegressionMode()
					&& !daModel.getRegressionMode().equals(Regression.NONE)) {

				regressionCopy = statGeo.createRegressionPlot(
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
			if (!settings.xAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(settings.xAxesInterval, 0);
			}
			if (!settings.yAxesIntervalAuto) {
				targetEV.setAxesNumberingDistance(settings.yAxesInterval, 1);
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
			updatePlot(true);

		} catch (Exception e) {
			e.printStackTrace();
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
	 * @param euclidianViewID
	 *            viewID of the target EuclidianView
	 */
	private static void prepareGeoForEV(GeoElement geo, int euclidianViewID) {

		geo.setLabel(null);
		geo.setEuclidianVisible(true);
		geo.setAuxiliaryObject(false);
		if (euclidianViewID == App.VIEW_EUCLIDIAN) {
			geo.addView(App.VIEW_EUCLIDIAN);
			geo.removeView(App.VIEW_EUCLIDIAN2);
			geo.update();
		}
		if (euclidianViewID == App.VIEW_EUCLIDIAN2) {
			geo.addView(App.VIEW_EUCLIDIAN2);
			geo.removeView(App.VIEW_EUCLIDIAN);
			geo.update();
		}

	}


	public void removeGeos() {
		clearPlotGeoList();
	}


	public void setMode(int mode) {
		daModel.setMode(mode);
	}

	public StatPanelSettings getSettings() {
		return settings;
	}

	public PlotType getSelectedPlot() {
		return selectedPlot;
	}

	
	public void setSelectedPlot(PlotType selectedPlot) {
		this.selectedPlot = selectedPlot;
	}
}
