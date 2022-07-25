package org.geogebra.web.full.gui.view.data;

import java.util.ArrayList;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.OneVarModel;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.LocalizationW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Extended JPanel that contains interactive sub-panels for performing one
 * variable inference with the current data set.
 * 
 * @author G. Sturr
 * 
 */
public class OneVarInferencePanelW extends FlowPanel
		implements ClickHandler, BlurHandler, StatPanelInterfaceW {
	// ggb fields
	private AppW app;
	private Kernel kernel;
	private DataAnalysisViewW statDialog;
	private StatTableW resultTable;

	// GUI
	private Label lblHypParameter;
	private Label lblTailType;
	private Label lblNull;
	private Label lblConfLevel;
	private Label lblSigma;
	private Label lblResultHeader;
	private AutoCompleteTextFieldW fldNullHyp;
	private AutoCompleteTextFieldW fldConfLevel;
	private AutoCompleteTextFieldW fldSigma;
	private RadioButton btnLeft;
	private RadioButton btnRight;
	private RadioButton btnTwo;
	private ListBox lbAltHyp;
	private FlowPanel testPanel;
	private FlowPanel intPanel;
	private FlowPanel mainPanel;
	private FlowPanel resultPanel;
	private FlowPanel sigmaPanel;
	private int fieldWidth = 6;

	// statistics

	// flags
	private boolean isIniting;
	private boolean isTest = true;
	private boolean isZProcedure;

	private LocalizationW loc;
	private boolean enablePooled;
	private final OneVarModel model;

	private class ParamKeyHandler implements KeyHandler {
		private Object source;

		public ParamKeyHandler(Object source) {
			this.source = source;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.isEnterKey()) {
				actionPerformed(source);
			}
		}
	}

	private class ParamBlurHandler implements BlurHandler {
		private Object source;

		public ParamBlurHandler(Object source) {
			this.source = source;
		}

		@Override
		public void onBlur(BlurEvent event) {
			actionPerformed(source);

		}

	}

	/***************************************
	 * Construct a OneVarInference panel
	 * 
	 * @param app
	 *            application
	 * @param statDialog
	 *            stat dialog
	 */
	public OneVarInferencePanelW(AppW app, DataAnalysisViewW statDialog) {

		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.model = new OneVarModel();
		this.statDialog = statDialog;
		this.statDialog.getController().loadDataLists(true);
		this.createGUIElements();

		this.updateGUI();
		this.setLabels();

		isIniting = false;
	}

	// ============================================================
	// Create GUI
	// ============================================================

	private void createGUIElements() {

		btnLeft = new RadioButton(OneVarModel.tail_left);
		btnRight = new RadioButton(OneVarModel.tail_right);
		btnTwo = new RadioButton(OneVarModel.tail_two);
		FlowPanel group = new FlowPanel();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);
		btnLeft.addClickHandler(this);
		btnRight.addClickHandler(this);
		btnTwo.addClickHandler(this);
		btnTwo.setValue(true);

		lbAltHyp = new ListBox();
		lbAltHyp.addChangeHandler(event -> actionPerformed(lbAltHyp));

		lblNull = new Label();
		lblNull.setStyleName("panelTitle");
		lblHypParameter = new Label();
		lblTailType = new Label();
		lblTailType.setStyleName("panelTitle");

		fldNullHyp = (new InputPanelW(app, -1, false)).getTextComponent();
		fldNullHyp.setWidthInEm(fieldWidth);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addKeyHandler(new ParamKeyHandler(fldNullHyp));
		fldNullHyp.addBlurHandler(new ParamBlurHandler(fldNullHyp));
		lblConfLevel = new Label();
		lblConfLevel.setStyleName("panelTitle");
		fldConfLevel = (new InputPanelW(app, -1, false)).getTextComponent();
		fldConfLevel.setWidthInEm(fieldWidth);
		fldConfLevel.addKeyHandler(new ParamKeyHandler(fldConfLevel));
		fldConfLevel.addBlurHandler(new ParamBlurHandler(fldConfLevel));

		lblSigma = new Label();
		fldSigma = (new InputPanelW(app, -1, false)).getTextComponent();
		fldSigma.setWidthInEm(fieldWidth);
		fldSigma.addKeyHandler(new ParamKeyHandler(fldSigma));
		fldSigma.addBlurHandler(new ParamBlurHandler(fldSigma));

		lblResultHeader = new Label();
		lblResultHeader.setStyleName("panelTitle");

		sigmaPanel = new FlowPanel();
		sigmaPanel.add(LayoutUtilW.panelRowIndent(lblSigma, fldSigma));
		// // test panel
		testPanel = new FlowPanel();
		testPanel.add(lblNull);
		testPanel.add(LayoutUtilW.panelRowIndent(lblHypParameter, fldNullHyp));
		testPanel.add(lblTailType);
		testPanel.add(LayoutUtilW.panelRowIndent(lbAltHyp));
		// // CI panel
		intPanel = new FlowPanel();
		intPanel.add(lblConfLevel);
		intPanel.add(fldConfLevel);
		//
		// // result panel
		resultTable = new StatTableW();
		resultTable.setStyleName("daStatistics");
		setResultTable();

		resultPanel = new FlowPanel();
		resultPanel.add(lblResultHeader);
		resultPanel.add(resultTable);
		//
		//
		//
		// main panel
		mainPanel = new FlowPanel();
		add(mainPanel);
		add(resultPanel);
	}

	private void updateMainPanel() {
		mainPanel.clear();

		if (isZProcedure) {
			mainPanel.add(sigmaPanel);
		}

		if (isTest) {
			mainPanel.add(testPanel);
		} else {
			mainPanel.add(intPanel);
		}

		mainPanel.add(resultPanel);

	}

	private void setResultTable() {

		ArrayList<String> nameList = model.getNameList(loc);

		String[] rowNames = new String[nameList.size()];
		nameList.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 2, null);

	}

	private void updateResultTable() {

		evaluate();
		String cInt = statDialog.format(model.getMean()) + " \u00B1 "
				+ statDialog.format(model.getMe());

		switch (model.selectedPlot) {
		default:
			// do nothing
			break;
		case StatisticsModel.INFER_ZTEST:
			resultTable.setValueAt(statDialog.format(model.getP()), 0, 1);
			resultTable.setValueAt(statDialog.format(model.getTestStat()), 1,
					1);
			resultTable.setValueAt("", 2, 1);
			resultTable.setValueAt(statDialog.format(model.getN()), 3, 1);
			resultTable.setValueAt(statDialog.format(model.getMean()), 4, 1);
			break;

		case StatisticsModel.INFER_TTEST:
			resultTable.setValueAt(statDialog.format(model.getP()), 0, 1);
			resultTable.setValueAt(statDialog.format(model.getTestStat()), 1,
					1);
			resultTable.setValueAt(statDialog.format(model.getDf()), 2, 1);
			resultTable.setValueAt(statDialog.format(model.getSe()), 3, 1);
			resultTable.setValueAt("", 4, 1);
			resultTable.setValueAt(statDialog.format(model.getN()), 5, 1);
			resultTable.setValueAt(statDialog.format(model.getMean()), 6, 1);
			break;

		case StatisticsModel.INFER_ZINT:
			resultTable.setValueAt(cInt, 0, 1);
			resultTable.setValueAt(statDialog.format(model.getLower()), 1, 1);
			resultTable.setValueAt(statDialog.format(model.getUpper()), 2, 1);
			resultTable.setValueAt(statDialog.format(model.getMe()), 3, 1);
			resultTable.setValueAt("", 4, 1);
			resultTable.setValueAt(statDialog.format(model.getN()), 5, 1);
			resultTable.setValueAt(statDialog.format(model.getMean()), 6, 1);
			break;

		case StatisticsModel.INFER_TINT:
			resultTable.setValueAt(cInt, 0, 1);
			resultTable.setValueAt(statDialog.format(model.getLower()), 1, 1);
			resultTable.setValueAt(statDialog.format(model.getUpper()), 2, 1);
			resultTable.setValueAt(statDialog.format(model.getMe()), 3, 1);
			resultTable.setValueAt(statDialog.format(model.getDf()), 4, 1);
			resultTable.setValueAt(statDialog.format(model.getSe()), 5, 1);
			resultTable.setValueAt("", 6, 1);
			resultTable.setValueAt(statDialog.format(model.getN()), 7, 1);
			resultTable.setValueAt(statDialog.format(model.getMean()), 8, 1);
			break;
		}

	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	@Override
	public void setLabels() {

		lblHypParameter.setText(loc.getMenu("HypothesizedMean.short") + " = ");
		lblNull.setText(loc.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(loc.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(loc.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(loc.getMenu("Result") + ": ");
		lblSigma.setText(loc.getMenu("StandardDeviation.short") + " = ");
	}

	/** Helper method for updateGUI() */
	private void updateNumberField(AutoCompleteTextFieldW fld, double n) {
		fld.setText(statDialog.format(n));
		// fld.setCaretPosition(0);
	}

	private void updateGUI() {

		isTest = (model.selectedPlot == StatisticsModel.INFER_ZTEST
				|| model.selectedPlot == StatisticsModel.INFER_TTEST);

		isZProcedure = model.selectedPlot == StatisticsModel.INFER_ZTEST
				|| model.selectedPlot == StatisticsModel.INFER_ZINT;

		updateNumberField(fldNullHyp, model.hypMean);
		updateNumberField(fldConfLevel, model.confLevel);
		updateNumberField(fldSigma, model.sigma);
		updateCBAlternativeHyp();
		setResultTable();
		updateResultTable();
		updateMainPanel();
	}

	private void updateCBAlternativeHyp() {
		lbAltHyp.clear();
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_right + " "
				+ statDialog.format(model.hypMean));
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_left + " "
				+ statDialog.format(model.hypMean));
		lbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_two + " "
				+ statDialog.format(model.hypMean));

		if (OneVarModel.tail_right.equals(model.tail)) {
			lbAltHyp.setSelectedIndex(0);
		} else if (OneVarModel.tail_left.equals(model.tail)) {
			lbAltHyp.setSelectedIndex(1);
		} else {
			lbAltHyp.setSelectedIndex(2);
		}

	}

	/**
	 * Handle text input
	 * 
	 * @param source
	 *            event source
	 */
	public void actionPerformed(Object source) {
		if (isIniting) {
			return;
		}

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == lbAltHyp) {

			if (lbAltHyp.getSelectedIndex() == 0) {
				model.tail = OneVarModel.tail_right;
			} else if (lbAltHyp.getSelectedIndex() == 1) {
				model.tail = OneVarModel.tail_left;
			} else {
				model.tail = OneVarModel.tail_two;
			}

			evaluate();
			updateResultTable();
		}
	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isIniting) {
			return;
		}

		double value = model.evaluateExpression(kernel,
				source.getText().trim());

		if (source == fldConfLevel) {
			model.confLevel = value;
			evaluate();
			updateGUI();
		}

		else if (source == fldNullHyp) {
			model.hypMean = value;
			evaluate();
			updateGUI();
		}

		else if (source == fldSigma) {
			model.sigma = value;
			evaluate();
			updateGUI();
		}
	}

	/**
	 * @param selectedPlot
	 *            plot type
	 */
	public void setSelectedPlot(int selectedPlot) {
		model.selectedPlot = selectedPlot;
		updateGUI();
	}

	@Override
	public void updatePanel() {
		evaluate();
		updateGUI();
		updateResultTable();
	}

	// ============================================================
	// Computation
	// ============================================================

	private void evaluate() {
		GeoList dataList = statDialog.getController().getDataSelected();
		double[] sample = statDialog.getController().getValueArray(dataList);

		model.evaluate(sample);
	}

	// ============================================================
	// GUI Utilities
	// ============================================================

	@Override
	public void onClick(ClickEvent event) {
		actionPerformed(event.getSource());
	}

	@Override
	public void onBlur(BlurEvent event) {
		// TODO Auto-generated method stub
		doTextFieldActionPerformed(
				(AutoCompleteTextFieldW) (event.getSource()));

	}

	public boolean isEnablePooled() {
		return enablePooled;
	}

	public void setEnablePooled(boolean enablePooled) {
		this.enablePooled = enablePooled;
	}

}
