package geogebra.common.gui.view.data;

import geogebra.common.gui.view.data.DataVariable.GroupType;
import geogebra.common.kernel.geos.GeoElement;
//import geogebra.common.kernel.geos.GeoList;
//import geogebra.common.kernel.geos.GeoNumeric;
//import geogebra.common.kernel.statistics.AlgoFrequencyTable;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

import java.util.ArrayList;

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
				"ResidualPlot"), MULTIBOXPLOT("StackedBoxPlots");

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
		 *            AppD
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
			scatterPlot, scatterPlotLine, residualPlot, nqPlot, boxPlot,
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


	/**
	 * Creates the JComboBox that selects display type
	 */
	private void createDisplayTypeComboBox() {


		switch (daModel.getMode()) {

		case DataAnalysisModel.MODE_ONEVAR:

			if (!daModel.isNumericData()) {
				listener.addDisplayTypeItem(PlotType.BARCHART);
			}

			else if (settings.groupType() == GroupType.RAWDATA) {
				listener.addDisplayTypeItem(PlotType.HISTOGRAM);
				listener.addDisplayTypeItem(PlotType.BARCHART);
				listener.addDisplayTypeItem(PlotType.BOXPLOT);
				listener.addDisplayTypeItem(PlotType.DOTPLOT);
				listener.addDisplayTypeItem(PlotType.STEMPLOT);
				listener.addDisplayTypeItem(PlotType.NORMALQUANTILE);
			}

			else if (settings.groupType() == GroupType.FREQUENCY) {
				listener.addDisplayTypeItem(PlotType.HISTOGRAM);
				listener.addDisplayTypeItem(PlotType.BARCHART);
				listener.addDisplayTypeItem(PlotType.BOXPLOT);

			} else if (settings.groupType() == GroupType.CLASS) {
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

	}

	/**
	 * Updates the plot panel. Adds/removes additional panels as needed for the
	 * current selected plot.
	 */
	private void updatePlotPanelLayout() {


		if (selectedPlot == PlotType.SCATTERPLOT) {
			listener.updateScatterPlot();
		}

		else if (selectedPlot == PlotType.HISTOGRAM
				|| selectedPlot == PlotType.BARCHART) {

			if (settings.showFrequencyTable) {
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
//	public void updatePlot(boolean doCreate) {
//
//		GeoList dataListSelected = daModel.getController().getDataSelected();
//
//		if (hasControlPanel)
//			((CardLayout) controlCards.getLayout()).show(controlCards,
//					"blankPanel");
//
//		if (doCreate) {
//			clearPlotGeoList();
//		}
//
//		btnOptions.setVisible(true);
//		updatePlotPanelLayout();
//
//		// if invalid data, show blank plot and exit
//		if (!daModel.getController().isValidData()) {
//			showInvalidDataDisplay();
//			return;
//		}
//
//		try {
//			switch (selectedPlot) {
//
//			case HISTOGRAM:
//
//				if (doCreate) {
//					if (histogram != null) {
//						histogram.remove();
//					}
//					histogram = statGeo.createHistogram(dataListSelected,
//							settings, false);
//					plotGeoList.add(histogram);
//
//					if (frequencyPolygon != null) {
//						frequencyPolygon.remove();
//					}
//					if (settings.hasOverlayPolygon) {
//						frequencyPolygon = statGeo.createHistogram(
//								dataListSelected, settings, true);
//						plotGeoList.add(frequencyPolygon);
//					}
//
//					if (normalCurve != null) {
//						normalCurve.remove();
//					}
//					if (settings.hasOverlayNormal) {
//						normalCurve = statGeo
//								.createNormalCurveOverlay(dataListSelected);
//						plotGeoList.add(normalCurve);
//					}
//
//					if (freqTableGeo != null) {
//						freqTableGeo.remove();
//					}
//					if (settings.showFrequencyTable) {
//						freqTableGeo = statGeo.createFrequencyTableGeo(
//								(GeoNumeric) histogram, selectedPlot);
//						plotGeoList.add(freqTableGeo);
//					}
//				}
//
//				// update the frequency table
//				if (settings.showFrequencyTable) {
//					frequencyTable.setTableFromGeoFrequencyTable(
//							(AlgoFrequencyTable) freqTableGeo
//									.getParentAlgorithm(), true);
//				}
//
//				// update settings
//				statGeo.getHistogramSettings(dataListSelected, histogram,
//						settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//
//				if (hasControlPanel
//						&& settings.getDataSource().getGroupType() != GroupType.CLASS)
//					if (settings.useManualClasses)
//						((CardLayout) controlCards.getLayout()).show(
//								controlCards, "manualClassesPanel");
//					else
//						((CardLayout) controlCards.getLayout()).show(
//								controlCards, "numClassesPanel");
//
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case BOXPLOT:
//				if (doCreate) {
//					if (boxPlot != null)
//						boxPlot.remove();
//					boxPlot = statGeo.createBoxPlot(dataListSelected, settings);
//					plotGeoList.add(boxPlot);
//				}
//				statGeo.getBoxPlotSettings(dataListSelected, settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case BARCHART:
//				if (doCreate) {
//					if (barChart != null) {
//						barChart.remove();
//					}
//					if (settings.isNumericData()) {
//						barChart = statGeo.createBarChartNumeric(
//								dataListSelected, settings);
//						plotGeoList.add(barChart);
//					} else {
//						barChart = statGeo.createBarChartText(dataListSelected,
//								settings);
//					}
//					plotGeoList.add(barChart);
//
//					if (freqTableGeo != null) {
//						freqTableGeo.remove();
//					}
//					if (settings.showFrequencyTable) {
//						freqTableGeo = statGeo.createFrequencyTableGeo(
//								(GeoNumeric) barChart, selectedPlot);
//						plotGeoList.add(freqTableGeo);
//					}
//				}
//
//				// update the frequency table
//				if (settings.showFrequencyTable) {
//					frequencyTable.setTableFromGeoFrequencyTable(
//							(AlgoFrequencyTable) freqTableGeo
//									.getParentAlgorithm(), false);
//				}
//
//				// update settings
//				statGeo.getBarChartSettings(dataListSelected, settings,
//						barChart);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case DOTPLOT:
//				if (doCreate) {
//					if (dotPlot != null)
//						dotPlot.remove();
//					dotPlot = statGeo.createDotPlot(dataListSelected);
//					plotGeoList.add(dotPlot);
//				}
//
//				statGeo.updateDotPlot(dataListSelected, dotPlot, settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case STEMPLOT:
//				String latex = statGeo.getStemPlotLatex(dataListSelected,
//						settings.stemAdjust);
//				imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, latex,
//						app.getPlainFont(), true, Color.BLACK, null));
//				btnOptions.setVisible(false);
//				if (hasControlPanel)
//					((CardLayout) controlCards.getLayout()).show(controlCards,
//							"stemAdjustPanel");
//
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "imagePanel");
//				break;
//
//			case NORMALQUANTILE:
//				if (doCreate) {
//					if (nqPlot != null)
//						nqPlot.remove();
//					nqPlot = statGeo.createNormalQuantilePlot(dataListSelected);
//					plotGeoList.add(nqPlot);
//				}
//				statGeo.updateNormalQuantilePlot(dataListSelected, settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case SCATTERPLOT:
//				if (doCreate) {
//					scatterPlot = statGeo.createScatterPlot(dataListSelected);
//					plotGeoList.add(scatterPlot);
//
//					if (daModel.getRegressionModel() != null
//							&& !daModel.getRegressionMode().equals(
//									Regression.NONE)) {
//						plotGeoList.add(daModel.getRegressionModel());
//					}
//
//					if (settings.showScatterplotLine) {
//						scatterPlotLine = statGeo
//								.createScatterPlotLine((GeoList) scatterPlot);
//						plotGeoList.add(scatterPlotLine);
//					}
//				}
//
//				// update xy title fields
//
//				if (settings.isPointList()) {
//					fldTitleX.setText(daModel.getDataTitles()[0]);
//					fldTitleY.setText(daModel.getDataTitles()[0]);
//				} else {
//					if (daModel.getDaCtrl().isLeftToRight()) {
//						fldTitleX.setText(daModel.getDataTitles()[0]);
//						fldTitleY.setText(daModel.getDataTitles()[1]);
//					} else {
//						fldTitleX.setText(daModel.getDataTitles()[1]);
//						fldTitleY.setText(daModel.getDataTitles()[0]);
//					}
//				}
//
//				// update settings
//				statGeo.getScatterPlotSettings(dataListSelected, settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//
//				break;
//
//			case RESIDUAL:
//				if (doCreate) {
//					if (!daModel.getRegressionMode().equals(Regression.NONE)) {
//						residualPlot = statGeo.createRegressionPlot(
//								dataListSelected, daModel.getRegressionMode(),
//								daModel.getRegressionOrder(), true);
//						plotGeoList.add(residualPlot);
//						statGeo.getResidualPlotSettings(dataListSelected,
//								residualPlot, settings);
//						plotPanel.commonFields.updateSettings(plotPanel, settings);
//					} else if (residualPlot != null) {
//						residualPlot.remove();
//						residualPlot = null;
//					}
//				}
//
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			case MULTIBOXPLOT:
//				if (doCreate) {
//					GeoElement[] boxPlots = statGeo.createMultipleBoxPlot(
//							dataListSelected, settings);
//					for (int i = 0; i < boxPlots.length; i++)
//						plotGeoList.add(boxPlots[i]);
//				}
//
//				statGeo.getMultipleBoxPlotSettings(dataListSelected, settings);
//				plotPanel.commonFields.updateSettings(plotPanel, settings);
//				boxPlotTitles = statGeo.createBoxPlotTitles(daModel, settings);
//				for (int i = 0; i < boxPlotTitles.length; i++)
//					plotGeoList.add(boxPlotTitles[i]);
//
//				((CardLayout) displayCardPanel.getLayout()).show(
//						displayCardPanel, "plotPanel");
//				break;
//
//			default:
//
//			}
//
//			// ==============================================
//			// Prepare Geos for plot panel view
//			// ==============================================
//
//			if (doCreate && statGeo.removeFromConstruction()) {
//				for (GeoElement listGeo : plotGeoList) {
//					// add the geo to our view and remove it from EV
//					listGeo.addView(plotPanel.getViewID());
//					plotPanel.add(listGeo);
//					listGeo.removeView(App.VIEW_EUCLIDIAN);
//					app.getEuclidianView1().remove(listGeo);
//				}
//			}
//
//			if (freqTableGeo != null) {
//				freqTableGeo.setEuclidianVisible(false);
//				freqTableGeo.updateRepaint();
//			}
//
//			if (histogram != null) {
//				histogram.setEuclidianVisible(settings.showHistogram);
//				histogram.updateRepaint();
//			}
//
//		} catch (Exception e) {
//			daModel.getDaCtrl().setValidData(false);
//			showInvalidDataDisplay();
//			e.printStackTrace();
//		}
//
//	}
//
//	// ============================================================
//	// Event Handlers
//	// ============================================================
//
//	public void actionPerformed(ActionEvent e) {
//
//		Object source = e.getSource();
//
//		if (source instanceof JTextField)
//			doTextFieldActionPerformed(source);
//
//		else if (source == minus || source == plus || source == none) {
//			minus.setSelected(source == minus);
//			none.setSelected(source == none);
//			plus.setSelected(source == plus);
//			if (source == minus)
//				settings.stemAdjust = -1;
//			if (source == none)
//				settings.stemAdjust = 0;
//			if (source == plus)
//				settings.stemAdjust = 1;
//			updatePlot(true);
//		}
//
//		else if (source == btnOptions) {
//			optionsPanel.setPanel(selectedPlot);
//			optionsPanel.setVisible(btnOptions.isSelected());
//		}
//
//		else if (source == btnExport) {
//			JPopupMenu menu = plotPanel.getContextMenu();
//			menu.show(btnExport,
//					-menu.getPreferredSize().width + btnExport.getWidth(),
//					btnExport.getHeight());
//		}
//
//		else if (source == cbDisplayType) {
//			if (cbDisplayType.getSelectedItem().equals(MyRenderer.SEPARATOR)) {
//				cbDisplayType.setSelectedItem(selectedPlot);
//			} else {
//				selectedPlot = (PlotType) cbDisplayType.getSelectedItem();
//				updatePlot(true);
//			}
//
//			if (optionsPanel.isVisible()) {
//				optionsPanel.setPanel(selectedPlot);
//			}
//		}
//
//	}


	public void clearPlotGeoList() {
		for (GeoElement geo : plotGeoList) {
			if (geo != null) {
				geo.remove();
				geo = null;
			}
		}
		plotGeoList.clear();
	}

	public void removeGeos() {
		clearPlotGeoList();
	}


	public void setMode(int mode) {
		daModel.setMode(mode);
	}

}
