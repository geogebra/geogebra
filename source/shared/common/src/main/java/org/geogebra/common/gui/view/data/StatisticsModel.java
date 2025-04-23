package org.geogebra.common.gui.view.data;

import java.util.HashMap;
import java.util.Map.Entry;

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
	public static final int INFER_ZTEST = 1;
	public static final int INFER_ZINT = 2;
	public static final int INFER_TTEST = 3;
	public static final int INFER_TINT = 4;
	// two var
	public static final int INFER_TTEST_2MEANS = 20;
	public static final int INFER_TTEST_PAIRED = 21;
	public static final int INFER_TINT_2MEANS = 22;
	public static final int INFER_TINT_PAIRED = 23;
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
		void addInferenceMode(String item);

		void selectInferenceMode(String string);

		String getSeparator();

		void updateOneVarInference(int mode);

		void updateTwoVarInference(int mode);

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
			listener.addInferenceMode(labelMap.get(INFER_ZTEST));
			listener.addInferenceMode(labelMap.get(INFER_TTEST));
			listener.addInferenceMode(listener.getSeparator());
			listener.addInferenceMode(labelMap.get(INFER_ZINT));
			listener.addInferenceMode(labelMap.get(INFER_TINT));
			break;

		case DataAnalysisModel.MODE_REGRESSION:
			listener.addInferenceMode(labelMap.get(SUMMARY_STATISTICS));
			break;

		case DataAnalysisModel.MODE_MULTIVAR:
			listener.addInferenceMode(labelMap.get(SUMMARY_STATISTICS));
			listener.addInferenceMode(labelMap.get(INFER_ANOVA));
			listener.addInferenceMode(labelMap.get(INFER_TTEST_2MEANS));
			listener.addInferenceMode(labelMap.get(INFER_TTEST_PAIRED));
			listener.addInferenceMode(listener.getSeparator());
			listener.addInferenceMode(labelMap.get(INFER_TINT_2MEANS));
			listener.addInferenceMode(labelMap.get(INFER_TINT_PAIRED));
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
		labelMap.put(INFER_TTEST, loc.getMenu("TMeanTest"));
		labelMap.put(INFER_TINT, loc.getMenu("TMeanInterval"));
		labelMap.put(INFER_ZTEST, loc.getMenu("ZMeanTest"));
		labelMap.put(INFER_ZINT, loc.getMenu("ZMeanInterval"));

		labelMap.put(INFER_ANOVA, loc.getMenu("ANOVA"));
		labelMap.put(SUMMARY_STATISTICS, loc.getMenu("Statistics"));

		labelMap.put(INFER_TTEST_2MEANS, loc.getMenu("TTestDifferenceOfMeans"));
		labelMap.put(INFER_TTEST_PAIRED, loc.getMenu("TTestPairedDifferences"));
		labelMap.put(INFER_TINT_2MEANS,
				loc.getMenu("TEstimateDifferenceOfMeans"));
		labelMap.put(INFER_TINT_PAIRED,
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
		case INFER_ZTEST:
		case INFER_TTEST:
		case INFER_ZINT:
		case INFER_TINT:
			listener.updateOneVarInference(selectedMode);
			break;

		case INFER_TTEST_2MEANS:
		case INFER_TTEST_PAIRED:
		case INFER_TINT_2MEANS:
		case INFER_TINT_PAIRED:
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
