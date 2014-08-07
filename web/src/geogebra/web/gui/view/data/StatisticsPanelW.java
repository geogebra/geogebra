package geogebra.web.gui.view.data;

import geogebra.common.gui.view.data.DataAnalysisModel;
import geogebra.web.main.AppW;

import java.util.HashMap;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * Extended JPanel that displays: (1) summary statistics for the current data
 * set (2) interactive panels for performing statistical inference with the
 * current data set
 * 
 * @author G. Sturr
 * 
 */
public class StatisticsPanelW extends FlowPanel implements StatPanelInterfaceW {
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
	private ListBox lbInferenceMode;
	private HashMap<Integer, String> labelMap;
	private HashMap<String, Integer> labelMapReverse;
	private int selectedMode = SUMMARY_STATISTICS;

	// panels
	private BasicStatTableW statTable;
	private OneVarInferencePanelW oneVarInferencePanel;
//	private LinearRegressionPanel regressionPanel;
//	private TwoVarInferencePanel twoVarInferencePanel;
//	private ANOVATable anovaTable;
//	private MultiVarStatPanel minMVStatPanel;
	private FlowPanel selectionPanel;
	private FlowPanel inferencePanel;

	// ggb fields
	private DataAnalysisViewW statDialog;
	private AppW app;
	private DataAnalysisModel daModel;

	/*************************************
	 * Constructor
	 * 
	 * @param app
	 * @param statDialog
	 */
	public StatisticsPanelW(AppW app, DataAnalysisViewW statDialog) {

		this.app = app;
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		// create the sub-panels
		createSelectionPanel();
		daModel.setMode(DataAnalysisModel.MODE_ONEVAR);
		
		createStatTable();
		if (statTable != null) {
	//		statTable.setBorder(BorderFactory.createEmptyBorder());
			inferencePanel = new FlowPanel();
			inferencePanel.add(statTable);
			//add(selectionPanel);
			add(inferencePanel);

			setLabels();
		}
	}

	/**
	 * Creates a table to display summary statistics for the current data set(s)
	 */
	private void createStatTable() {
//		 create a statTable according to dialog type
		if (daModel.getMode() == DataAnalysisModel.MODE_ONEVAR) {
			statTable = new BasicStatTableW(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_REGRESSION) {
			statTable = new BasicStatTableW(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_MULTIVAR) {
		//	statTable = new MultiVarStatPanelW(app, statDialog);
		}
	}

	/**
	 * Reconfigures the panel layout according to the current selected inference
	 * mode
	 */
	private void setInferencePanel() {

		if (inferencePanel == null)
			return;

		inferencePanel.clear();
		switch (selectedMode) {

		case INFER_ZTEST:
		case INFER_TTEST:
		case INFER_ZINT:
		case INFER_TINT:
			inferencePanel.add(getOneVarInferencePanel());
			break;

		case INFER_TTEST_2MEANS:
		case INFER_TTEST_PAIRED:
		case INFER_TINT_2MEANS:
		case INFER_TINT_PAIRED:
			//inferencePanel.add(getTwoVarInferencePanel(), BorderLayout.NORTH);
			// inferencePanel.add(statTable, BorderLayout.CENTER);
			break;

		case INFER_ANOVA:

//			GridBagConstraints tab = new GridBagConstraints();
//			tab.gridx = 0;
//			tab.gridy = GridBagConstraints.RELATIVE;
//			tab.weightx = 1;
//			tab.insets = new Insets(4, 20, 0, 20);
//			tab.fill = GridBagConstraints.HORIZONTAL;
//			tab.anchor = GridBagConstraints.NORTHWEST;
//
//			JPanel p = new JPanel(new GridBagLayout());
//			p.add(getAnovaTable(), tab);
//			p.add(getMinMVStatPanel(), tab);
//			inferencePanel.add(p, BorderLayout.CENTER);
//
			break;

		default:
			inferencePanel.add(statTable);
		}

		statDialog.updateStatDataPanelVisibility();
	}

	private void createSelectionPanel() {
		createLabelMap();
		createInferenceTypeComboBox();

//		selectionPanel = new JPanel(new BorderLayout());
//		selectionPanel.add(cbInferenceMode, app.getLocalization().borderWest());
	}

//	private ANOVATable getAnovaTable() {
//		if (anovaTable == null)
//			anovaTable = new ANOVATable(app, statDialog);
//		return anovaTable;
//	}
//
	private OneVarInferencePanelW getOneVarInferencePanel() {
		if (oneVarInferencePanel == null)
			oneVarInferencePanel = new OneVarInferencePanelW(app, statDialog);
		return oneVarInferencePanel;
	}

//	private TwoVarInferencePanel getTwoVarInferencePanel() {
//		if (twoVarInferencePanel == null)
//			twoVarInferencePanel = new TwoVarInferencePanel(app, statDialog);
//		return twoVarInferencePanel;
//	}
//
//	private MultiVarStatPanel getMinMVStatPanel() {
//		if (minMVStatPanel == null)
//			minMVStatPanel = new MultiVarStatPanel(app, statDialog);
//		minMVStatPanel.setMinimalTable(true);
//		return minMVStatPanel;
//	}

	/**
	 * Creates the JComboBox that selects inference mode
	 */
	private void createInferenceTypeComboBox() {
//
//		if (cbInferenceMode == null) {
//			cbInferenceMode = new JComboBox();
//			cbInferenceMode.setFocusable(false);
//			cbInferenceMode.setRenderer(new MyRenderer());
//
//		} else {
//			cbInferenceMode.removeActionListener(this);
//			cbInferenceMode.removeAllItems();
//		}
//
//		switch (daModel.getMode()) {
//
//		case DataAnalysisModel.MODE_ONEVAR:
//			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
//			cbInferenceMode.addItem(labelMap.get(INFER_ZTEST));
//			cbInferenceMode.addItem(labelMap.get(INFER_TTEST));
//			cbInferenceMode.addItem(MyRenderer.SEPARATOR);
//			cbInferenceMode.addItem(labelMap.get(INFER_ZINT));
//			cbInferenceMode.addItem(labelMap.get(INFER_TINT));
//			break;
//
//		case DataAnalysisModel.MODE_REGRESSION:
//			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
//			break;
//
//		case DataAnalysisModel.MODE_MULTIVAR:
//			cbInferenceMode.addItem(labelMap.get(SUMMARY_STATISTICS));
//			cbInferenceMode.addItem(labelMap.get(INFER_ANOVA));
//			cbInferenceMode.addItem(labelMap.get(INFER_TTEST_2MEANS));
//			cbInferenceMode.addItem(labelMap.get(INFER_TTEST_PAIRED));
//			cbInferenceMode.addItem(MyRenderer.SEPARATOR);
//			cbInferenceMode.addItem(labelMap.get(INFER_TINT_2MEANS));
//			cbInferenceMode.addItem(labelMap.get(INFER_TINT_PAIRED));
//			break;
//		}
//
//		cbInferenceMode.setSelectedItem(labelMap.get(selectedMode));
//		cbInferenceMode.addActionListener(this);
//		cbInferenceMode.setMaximumRowCount(cbInferenceMode.getItemCount());
//		cbInferenceMode.addActionListener(this);
//
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

	public void setLabels() {
		statTable.setLabels();
		
	}

	public void updatePanel() {
		// System.out.println("============= update stat panel");
		if (statTable == null) {
			return;
		}

		statTable.updatePanel();

//		switch (selectedMode) {
//
//		case INFER_ZTEST:
//		case INFER_TTEST:
//		case INFER_ZINT:
//		case INFER_TINT:
//			getOneVarInferencePanel().setSelectedPlot(selectedMode);
//			getOneVarInferencePanel().updatePanel();
//			break;
//
//		case INFER_TTEST_2MEANS:
//		case INFER_TTEST_PAIRED:
//		case INFER_TINT_2MEANS:
//		case INFER_TINT_PAIRED:
//			getTwoVarInferencePanel().setSelectedInference(selectedMode);
//			getTwoVarInferencePanel().updatePanel();
//			break;
//
//		case INFER_ANOVA:
//			getAnovaTable().updatePanel();
//			getMinMVStatPanel().updatePanel();
//			break;
//		}
//
//		this.setMinimumSize(this.getPreferredSize());
//		// statDialog.updateStatDataPanelVisibility();

	}
}
