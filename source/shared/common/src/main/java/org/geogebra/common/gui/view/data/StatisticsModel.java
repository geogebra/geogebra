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

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * 
 * Extended JPanel that displays: (1) summary statistics for the current data
 * set (2) interactive panels for performing statistical inference with the
 * current data set
 * 
 * @author G. Sturr
 * 
 */
public class StatisticsModel {
	// inference mode constants
	public static final int SUMMARY_STATISTICS = 0;
	// one var
	public static final int INFER_Z_TEST = 1;
	public static final int INFER_Z_INT = 2;
	public static final int INFER_T_TEST = 3;
	public static final int INFER_T_INT = 4;
	// two var
	public static final int INFER_T_TEST_2MEANS = 20;
	public static final int INFER_T_TEST_PAIRED = 21;
	public static final int INFER_T_INT_2MEANS = 22;
	public static final int INFER_T_INT_PAIRED = 23;
	// multi var
	public static final int INFER_ANOVA = 40;

	// inference mode selection
	private HashMap<Integer, String> labelMap;
	private HashMap<String, Integer> labelMapReverse;
	private int selectedMode = SUMMARY_STATISTICS;
	// ggb fields
	private DataAnalysisModel daModel;
	private IStatisticsModelListener listener;
	private Localization loc;

	/**
	 * UI delegate for this model.
	 */
	public interface IStatisticsModelListener {
		/**
		 * Ad an inference mode.
		 * @param item localized inference mode
		 */
		void addInferenceMode(String item);

		/**
		 * Select an inference mode.
		 * @param string localized inference mode
		 */
		void selectInferenceMode(String string);

		@MissingDoc
		String getSeparator();

		/**
		 * Update one variable inference.
		 * @param mode inference mode
		 */
		void updateOneVarInference(int mode);

		/**
		 * Update two variable inference.
		 * @param mode inference mode
		 */
		void updateTwoVarInference(int mode);

		@MissingDoc
		void updateAnovaTable();

	}

	/*************************************
	 * Constructor
	 * 
	 * @param app
	 *            application
	 * @param model
	 *            DA model
	 * @param listener
	 *            UI
	 */
	public StatisticsModel(App app, DataAnalysisModel model,
			IStatisticsModelListener listener) {
		this.loc = app.getLocalization();
		this.daModel = model;
		this.listener = listener;
		createLabelMap();
	}

	/**
	 * Build UI for inference modes
	 */
	public void fillInferenceModes() {

		switch (daModel.getMode()) {

		default:
		case DataAnalysisModel.MODE_ONEVAR:
			listener.addInferenceMode(labelMap.get(SUMMARY_STATISTICS));
			listener.addInferenceMode(labelMap.get(INFER_Z_TEST));
			listener.addInferenceMode(labelMap.get(INFER_T_TEST));
			listener.addInferenceMode(listener.getSeparator());
			listener.addInferenceMode(labelMap.get(INFER_Z_INT));
			listener.addInferenceMode(labelMap.get(INFER_T_INT));
			break;

		case DataAnalysisModel.MODE_REGRESSION:
			listener.addInferenceMode(labelMap.get(SUMMARY_STATISTICS));
			break;

		case DataAnalysisModel.MODE_MULTIVAR:
			listener.addInferenceMode(labelMap.get(SUMMARY_STATISTICS));
			listener.addInferenceMode(labelMap.get(INFER_ANOVA));
			listener.addInferenceMode(labelMap.get(INFER_T_TEST_2MEANS));
			listener.addInferenceMode(labelMap.get(INFER_T_TEST_PAIRED));
			listener.addInferenceMode(listener.getSeparator());
			listener.addInferenceMode(labelMap.get(INFER_T_INT_2MEANS));
			listener.addInferenceMode(labelMap.get(INFER_T_INT_PAIRED));
			break;
		}

		listener.selectInferenceMode(labelMap.get(getSelectedMode()));

	}

	/**
	 * Creates two hash maps for JComboBox selections, 1) plotMap: Key = integer
	 * mode, Value = JComboBox menu string 2) plotMapReverse: Key = JComboBox
	 * menu string, Value = integer mode
	 */
	private void createLabelMap() {
		if (labelMap == null) {
			labelMap = new HashMap<>();
		}

		labelMap.clear();
		labelMap.put(INFER_T_TEST, loc.getMenu("TMeanTest"));
		labelMap.put(INFER_T_INT, loc.getMenu("TMeanInterval"));
		labelMap.put(INFER_Z_TEST, loc.getMenu("ZMeanTest"));
		labelMap.put(INFER_Z_INT, loc.getMenu("ZMeanInterval"));

		labelMap.put(INFER_ANOVA, loc.getMenu("ANOVA"));
		labelMap.put(SUMMARY_STATISTICS, loc.getMenu("Statistics"));

		labelMap.put(INFER_T_TEST_2MEANS, loc.getMenu("TTestDifferenceOfMeans"));
		labelMap.put(INFER_T_TEST_PAIRED, loc.getMenu("TTestPairedDifferences"));
		labelMap.put(INFER_T_INT_2MEANS,
				loc.getMenu("TEstimateDifferenceOfMeans"));
		labelMap.put(INFER_T_INT_PAIRED,
				loc.getMenu("TEstimatePairedDifferences"));

		// REVERSE LABEL MAP
		labelMapReverse = new HashMap<>();
		for (Entry<Integer, String> entry : labelMap.entrySet()) {

			labelMapReverse.put(entry.getValue(), entry.getKey());
		}

	}

	/**
	 * Update the UI
	 */
	public void update() {
		switch (getSelectedMode()) {

		default:
		case INFER_Z_TEST:
		case INFER_T_TEST:
		case INFER_Z_INT:
		case INFER_T_INT:
			listener.updateOneVarInference(selectedMode);
			break;

		case INFER_T_TEST_2MEANS:
		case INFER_T_TEST_PAIRED:
		case INFER_T_INT_2MEANS:
		case INFER_T_INT_PAIRED:
			listener.updateTwoVarInference(selectedMode);
			break;

		case INFER_ANOVA:
			listener.updateAnovaTable();
			break;
		}

	}

	/**
	 * @param inferenceMode
	 *            selected inference mode
	 */
	public void selectInferenceMode(String inferenceMode) {
		if (inferenceMode.equals(listener.getSeparator())) {
			listener.selectInferenceMode(labelMap.get(getSelectedMode()));
		} else {
			selectedMode = labelMapReverse.get(inferenceMode);
		}
	}

	public int getSelectedMode() {
		return selectedMode;
	}

	public void setSelectedMode(int selectedMode) {
		this.selectedMode = selectedMode;
	}

}
