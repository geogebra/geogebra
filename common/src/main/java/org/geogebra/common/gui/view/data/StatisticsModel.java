package org.geogebra.common.gui.view.data;

import java.util.HashMap;

import org.geogebra.common.main.App;

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
	public interface IStatisticsModelListener {
		void addInferenceMode(String item);

		void selectInferenceMode(String string);

		String getSeparator();

		void updateOneVarInference(int mode);

		void updateTwoVarInference(int mode);

		void updateAnovaTable();

	}
	
	private static final long serialVersionUID = 1L;
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
	private App app;
	private DataAnalysisModel daModel;
	private IStatisticsModelListener listener;
	/*************************************
	 * Constructor
	 * 
	 * @param app
	 * @param statDialog
	 */
	public StatisticsModel(App app, DataAnalysisModel model, IStatisticsModelListener listener) {

		this.app = app;
		this.daModel = model;
		this.listener = listener;
		createLabelMap();
	}


	public void fillInferenceModes() {

		switch (daModel.getMode()) {

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
		if (labelMap == null)
			labelMap = new HashMap<Integer, String>();

		labelMap.clear();
		labelMap.put(INFER_TTEST, app.getMenu("TMeanTest"));
		labelMap.put(INFER_TINT, app.getMenu("TMeanInterval"));
		labelMap.put(INFER_ZTEST, app.getMenu("ZMeanTest"));
		labelMap.put(INFER_ZINT, app.getMenu("ZMeanInterval"));

		labelMap.put(INFER_ANOVA, app.getMenu("ANOVA"));
		labelMap.put(SUMMARY_STATISTICS, app.getMenu("Statistics"));

		labelMap.put(INFER_TTEST_2MEANS, app.getMenu("TTestDifferenceOfMeans"));
		labelMap.put(INFER_TTEST_PAIRED, app.getMenu("TTestPairedDifferences"));
		labelMap.put(INFER_TINT_2MEANS,
				app.getMenu("TEstimateDifferenceOfMeans"));
		labelMap.put(INFER_TINT_PAIRED,
				app.getMenu("TEstimatePairedDifferences"));

		// REVERSE LABEL MAP
		labelMapReverse = new HashMap<String, Integer>();
		for (Integer key : labelMap.keySet()) {
			labelMapReverse.put(labelMap.get(key), key);
		}

	}

	public void update() {
		switch (getSelectedMode()) {

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


	public void selectInferenceMode(String item) {
		if (item.equals(listener.getSeparator())) {
			listener.selectInferenceMode(labelMap.get(getSelectedMode()));
		} else {
			selectedMode = labelMapReverse.get(item);
		}
	}


	public int getSelectedMode() {
		return selectedMode;
	}


	public void setSelectedMode(int selectedMode) {
		this.selectedMode = selectedMode;
	}

}
