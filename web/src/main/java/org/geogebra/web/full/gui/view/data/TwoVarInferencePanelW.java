package org.geogebra.web.full.gui.view.data;

import org.geogebra.common.gui.view.data.TwoVarInferenceModel;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel.TwoVarInferenceListener;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class TwoVarInferencePanelW extends FlowPanel implements StatPanelInterfaceW,
		TwoVarInferenceListener {

	private AppW app;
	private DataAnalysisViewW daView;
	private StatTableW resultTable;

	private ListBox lbTitle1;
	private ListBox lbTitle2;
	private ListBox lbAltHyp;
	private Label lblTitle1;
	private Label lblTitle2;
	private Label lblHypParameter;
	private Label lblTailType;
	private Label lblNull;
	private Label lblConfLevel;
	private Label lblResultHeader;
	private AutoCompleteTextFieldW fldNullHyp;
	private FlowPanel resultPanel;
	private CheckBox ckEqualVariances;
	private AutoCompleteTextFieldW fldConfLevel;

	private boolean isIniting;
	private FlowPanel testPanel;
	private FlowPanel intPanel;
	private FlowPanel mainPanel;
	private FlowPanel samplePanel;
	private TwoVarStatPanelW twoStatPanel;
	private TwoVarInferenceModel model;
	private boolean enablePooled;
	private CheckBox ckPooled;
	private Localization loc;

	/**
	 * Construct a TwoVarInference panel
	 * 
	 * @param app
	 *            application
	 * @param view
	 *            analysis view
	 */
	public TwoVarInferencePanelW(AppW app, DataAnalysisViewW view) {
		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.daView = view;
		model = new TwoVarInferenceModel(app, this);

		this.createGUIElements();
		this.updateGUI();
		this.setLabels();
		setStyleName("daTwoVarInference");
		isIniting = false;
		this.enablePooled = false;
	}

	// ============================================================
	// Create GUI
	// ============================================================

	private void createGUIElements() {

		// components
		lbTitle1 = new ListBox();
		lbTitle2 = new ListBox();
		lbTitle1.addChangeHandler(event -> actionPerformed(lbTitle1));

		lbTitle2.addChangeHandler(event -> actionPerformed(lbTitle2));
		lblTitle1 = new Label();
		lblTitle2 = new Label();

		ckPooled = new CheckBox();
		ckPooled.addStyleName("ckPooled");
		ckPooled.setValue(false);
		ckPooled.addValueChangeHandler(event -> model.setPooled(ckPooled.getValue()));
		ckEqualVariances = new CheckBox();

		lbAltHyp = new ListBox();

		lbAltHyp.addChangeHandler(event -> actionPerformed(lbAltHyp));

		lblNull = new Label();
		lblHypParameter = new Label();
		lblTailType = new Label();

		fldNullHyp = new AutoCompleteTextFieldW(4, app);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldNullHyp);
			}
		});

		fldNullHyp.addBlurHandler(event -> doTextFieldActionPerformed(fldNullHyp));

		lblConfLevel = new Label();
		fldConfLevel = new AutoCompleteTextFieldW(4, app);
		fldConfLevel.setWidthInEm(4);
		fldConfLevel.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldConfLevel);
			}
		});

		fldConfLevel.addBlurHandler(event -> doTextFieldActionPerformed(fldConfLevel));

		lblResultHeader = new Label();

		// test panel
		testPanel = new FlowPanel();

		testPanel.add(LayoutUtilW.panelRow(lblNull, lblHypParameter));
		testPanel.add(LayoutUtilW.panelRow(lblTailType, lbAltHyp));

		intPanel = new FlowPanel();
		intPanel.add(LayoutUtilW.panelRow(lblConfLevel, fldConfLevel));

		twoStatPanel = new TwoVarStatPanelW(app, daView, model.isPairedData(), this);

		samplePanel = new FlowPanel();

		samplePanel.add(twoStatPanel);

		// Result panel
		resultTable = new StatTableW();
		model.setResults();

		resultPanel = new FlowPanel();

		resultPanel.add(resultTable);

		// main panel
		mainPanel = new FlowPanel();
		add(ckPooled);
		add(mainPanel);
	}

	private void updateMainPanel() {

		mainPanel.clear();

		// layout
		if (model.isTest()) {
			mainPanel.add(testPanel);
		} else {
			mainPanel.add(intPanel);
		}

		mainPanel.add(samplePanel);
		// mainPanel.add(ckEqualVariances,c);
		mainPanel.add(resultPanel);
	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	private void updateGUI() {

		if (model.isTest()) {
			lblHypParameter.setText(model.getNullHypName() + " = 0");
		}

		// ckEqualVariances.setVisible(
		// selectedPlot == StatisticsModel.INFER_TINT_2MEANS
		// || selectedPlot == StatisticsModel.INFER_TTEST_2MEANS);
		ckEqualVariances.setValue(model.isPooled());

		updateNumberField(fldNullHyp, model.getHypMean());
		updateNumberField(fldConfLevel, model.getConfLevel());
		updateCBAlternativeHyp();

		model.setResults();
		model.updateResults();

		updateMainPanel();
		twoStatPanel.updatePanel();
	}

	/** Helper method for updateGUI() */
	private void updateNumberField(AutoCompleteTextFieldW fld, double n) {
		fld.setText(daView.format(n));
	}

	private void updateCBAlternativeHyp() {
		lbAltHyp.clear();
		model.fillAlternateHyp();
	}

	/**
	 * @param selectedPlot
	 *            selected plot index
	 */
	public void setSelectedInference(int selectedPlot) {
		model.setSelectedInference(selectedPlot);
		if (!isIniting) {
			model.setResults();
			this.twoStatPanel.setTable(model.isPairedData());
		}
		updateGUI();
	}

	@Override
	public void setLabels() {

		lblResultHeader.setText(loc.getMenu("Result") + ": ");

		lblTitle1.setText(loc.getMenu("Sample1") + ": ");
		lblTitle2.setText(loc.getMenu("Sample2") + ": ");

		lblNull.setText(loc.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(loc.getMenu("AlternativeHypothesis") + ": ");

		// lblCI.setText("Interval Estimate");
		lblConfLevel.setText(loc.getMenu("ConfidenceLevel") + ": ");

		// btnCalc.setText(loc.getMenu("Calculate"));

		ckEqualVariances.setText(loc.getMenu("EqualVariance"));
		ckPooled.setText(loc.getMenu("Pooled"));
	}

	@Override
	public void updatePanel() {
		model.updateResults();
		updateGUI();
	}

	/**
	 * Handle input event.
	 * 
	 * @param source
	 *            input field
	 */
	public void actionPerformed(Object source) {
		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == lbAltHyp) {
			model.applyTail(lbAltHyp.getSelectedIndex());
		}

		else if (source == lbTitle1 || source == lbTitle2) {
			model.updateResults();
		}

		else if (source == ckEqualVariances) {
			model.setPooled(ckEqualVariances.getValue());
		}
	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		if (isIniting) {
			return;
		}

		Double value = Double.parseDouble(source.getText().trim());

		if (source == fldConfLevel) {
			model.setConfLevel(value);
			updateGUI();
		}

		if (source == fldNullHyp) {
			model.setHypMean(value);
			updateGUI();
		}
	}

	private Integer[] selectedDataIndex() {
		return twoStatPanel.getSelectedDataIndex();
	}

	@Override
	public void setStatTable(int row, String[] rowNames, int length,
            String[] columnNames) {
		resultTable.setStatTable(1, null, columnNames.length, columnNames);
	}

	@Override
	public void setFormattedValueAt(double value, int row, int col) {
	    resultTable.setValueAt(daView.format(value), row, col);
    }

	@Override
	public GeoList getDataSelected() {
	    return daView.getController().getDataSelected();
    }

	@Override
	public int getSelectedDataIndex(int idx) {
	    return selectedDataIndex()[idx];
    }

	@Override
	public double[] getValueArray(GeoList list) {
	    return daView.getController().getValueArray(list);
    }

	@Override
	public void addAltHypItem(String name, String tail, double value) {
	    lbAltHyp.addItem(name + " " + tail + " " + daView.format(value));
    }

	@Override
	public void selectAltHyp(int idx) {
	   lbAltHyp.setSelectedIndex(idx);
    }

	/**
	 * @return whether "pooled" checkbox is visible
	 */
	public boolean isEnablePooled() {
		return enablePooled;
	}

	/**
	 * @param enablePooled
	 *            whether "pooled" checkbox should be visible
	 */
	public void setEnablePooled(boolean enablePooled) {
		this.enablePooled = enablePooled;
		ckPooled.setVisible(enablePooled);
	}

}
