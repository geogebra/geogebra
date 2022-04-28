package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.DataAnalysisModel;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.gui.view.data.StatisticsModel.IStatisticsModelListener;
import org.geogebra.web.html5.main.AppW;

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
public class StatisticsPanelW extends FlowPanel implements StatPanelInterfaceW,
		 IStatisticsModelListener {
	private static final String SEPARATOR = "-------------------";

	private StatisticsModel model;
	// inference mode selection
	private ListBox lbInferenceMode;

	// panels
	private BasicStatTableW statTable;
	private OneVarInferencePanelW oneVarInferencePanel;
	private TwoVarInferencePanelW twoVarInferencePanel;
	private ANOVATableW anovaTable;
	private MultiVarStatPanelW minMVStatPanel;
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
	 *            application
	 * @param statDialog
	 *            data analysis view
	 */
	public StatisticsPanelW(AppW app, DataAnalysisViewW statDialog) {
		this.app = app;
		this.statDialog = statDialog;
		this.daModel = statDialog.getModel();
		model = new StatisticsModel(app, daModel, this);
		// create the sub-panels
		createSelectionPanel();
		createStatTable();
		if (statTable != null) {
			inferencePanel = new FlowPanel();
			inferencePanel.add(statTable);

			add(selectionPanel);
			add(inferencePanel);

			setLabels();
		}
	}

	/**
	 * Creates a table to display summary statistics for the current data set(s)
	 */
	private void createStatTable() {
		// create a statTable according to dialog type
		if (daModel.getMode() == DataAnalysisModel.MODE_ONEVAR) {
			statTable = new BasicStatTableW(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_REGRESSION) {
			statTable = new BasicStatTableW(app, statDialog);
		} else if (daModel.getMode() == DataAnalysisModel.MODE_MULTIVAR) {
			statTable = new MultiVarStatPanelW(app, statDialog);
		}
	}

	/**
	 * Reconfigures the panel layout according to the current selected inference
	 * mode
	 */
	private void setInferencePanel() {
		if (inferencePanel == null) {
			return;
		}

		inferencePanel.clear();

		switch (model.getSelectedMode()) {

		case StatisticsModel.INFER_TTEST:
		case StatisticsModel.INFER_ZTEST:
		case StatisticsModel.INFER_ZINT:
		case StatisticsModel.INFER_TINT:
			inferencePanel.add(getOneVarInferencePanel());
			break;

		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TINT_2MEANS:
			inferencePanel.add(getTwoVarInferencePanel(true));
			break;

		case StatisticsModel.INFER_TTEST_PAIRED:
		case StatisticsModel.INFER_TINT_PAIRED:
			inferencePanel.add(getTwoVarInferencePanel(false));
			break;

		case StatisticsModel.INFER_ANOVA:
			inferencePanel.add(getAnovaTable());
			inferencePanel.add(getMinMVStatPanel());
			break;

		default:
			inferencePanel.add(statTable);
		}

		statDialog.updateStatDataPanelVisibility();
	}

	private void createSelectionPanel() {
		createInferenceTypeComboBox();

		selectionPanel = new FlowPanel();
		selectionPanel.add(lbInferenceMode);
	}

	private ANOVATableW getAnovaTable() {
		if (anovaTable == null) {
			anovaTable = new ANOVATableW(app, statDialog);
		}
		return anovaTable;
	}

	private OneVarInferencePanelW getOneVarInferencePanel() {
		if (oneVarInferencePanel == null) {
			oneVarInferencePanel = new OneVarInferencePanelW(app, statDialog);
		}
		return oneVarInferencePanel;
	}

	private TwoVarInferencePanelW getTwoVarInferencePanel() {
		if (twoVarInferencePanel == null) {
			twoVarInferencePanel = new TwoVarInferencePanelW(app, statDialog);
		}
		return twoVarInferencePanel;
	}

	private TwoVarInferencePanelW getTwoVarInferencePanel(boolean pooled) {
		TwoVarInferencePanelW p = getTwoVarInferencePanel();
		p.setEnablePooled(pooled);
		return p;
	}

	private MultiVarStatPanelW getMinMVStatPanel() {
		if (minMVStatPanel == null) {
			minMVStatPanel = new MultiVarStatPanelW(app, statDialog);
		}
		minMVStatPanel.setMinimalTable(true);
		return minMVStatPanel;
	}

	/**
	 * Creates the JComboBox that selects inference mode
	 */
	private void createInferenceTypeComboBox() {
		if (lbInferenceMode == null) {
			lbInferenceMode = new ListBox();
			lbInferenceMode.addChangeHandler(event -> actionPerformed(lbInferenceMode));
		} else {
			lbInferenceMode.clear();
		}

		model.fillInferenceModes();
	}

	@Override
	public void setLabels() {
		statTable.setLabels();
	}

	@Override
	public void updatePanel() {
		if (statTable == null) {
			return;
		}

		statTable.updatePanel();
		model.update();
		statDialog.updateStatDataPanelVisibility();
	}

	/**
	 * Handle input event.
	 * 
	 * @param source
	 *            input element
	 */
	public void actionPerformed(Object source) {
		int idx = lbInferenceMode.getSelectedIndex();
		if (source == lbInferenceMode
				&& idx != -1) {

			model.selectInferenceMode(lbInferenceMode.getValue(idx));
			setInferencePanel();
			updatePanel();
		}
	}

	@Override
	public void addInferenceMode(String item) {
		lbInferenceMode.addItem(item);
	}

	@Override
	public void selectInferenceMode(String item) {
		for (int idx = 0; idx < lbInferenceMode.getItemCount(); idx++) {
			String s = lbInferenceMode.getItemText(idx);
			if (s.equals(item)) {
				lbInferenceMode.setSelectedIndex(idx);
				return;
			}
		}
		lbInferenceMode.setSelectedIndex(0);
	}

	@Override
	public String getSeparator() {
		return SEPARATOR;
	}

	@Override
	public void updateOneVarInference(int mode) {
		getOneVarInferencePanel().setSelectedPlot(mode);
		getOneVarInferencePanel().updatePanel();
	}

	@Override
	public void updateTwoVarInference(int mode) {
		getTwoVarInferencePanel().setSelectedInference(mode);
		getTwoVarInferencePanel().updatePanel();
	}

	@Override
	public void updateAnovaTable() {
		getAnovaTable().updatePanel();
		getMinMVStatPanel().updatePanel();

	}

	/**
	 * @param rows
	 *            ANOVA / goodnes rows
	 * @return estimated height
	 */
	public double estimateHeight(int rows) {
		switch (model.getSelectedMode()) {

		case StatisticsModel.INFER_TTEST:
		case StatisticsModel.INFER_ZTEST:
		case StatisticsModel.INFER_ZINT:
		case StatisticsModel.INFER_TINT:
			// inferencePanel.add(getOneVarInferencePanel());
			return 150;

		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TINT_2MEANS:
			// /inferencePanel.add(getTwoVarInferencePanel(true));
			return 320;

		case StatisticsModel.INFER_TTEST_PAIRED:
		case StatisticsModel.INFER_TINT_PAIRED:
			// inferencePanel.add(getTwoVarInferencePanel(false));
			return 320;

		case StatisticsModel.INFER_ANOVA:
			return 200 + rows * 20;

		default:
			return 70 + rows * 20;
		}
	}

}
