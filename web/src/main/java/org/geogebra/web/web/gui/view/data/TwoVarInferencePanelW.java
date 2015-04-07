package org.geogebra.web.web.gui.view.data;

import org.geogebra.common.euclidian.event.KeyEvent;
import org.geogebra.common.euclidian.event.KeyHandler;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel.TwoVarInferenceListener;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel.UpdatePanel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.LayoutUtil;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class TwoVarInferencePanelW extends FlowPanel implements StatPanelInterfaceW,
		TwoVarInferenceListener, UpdatePanel {
	
	private AppW app;
	private DataAnalysisViewW daView;
	private StatTableW resultTable;

	private ListBox lbTitle1, lbTitle2, lbAltHyp;
	private Label lblTitle1, lblTitle2, lblHypParameter, lblTailType, lblNull,
			lblCI, lblConfLevel, lblResultHeader;
	private Button btnCalc;
	private AutoCompleteTextFieldW fldNullHyp;
	private FlowPanel cardProcedure, resultPanel;
	private CheckBox ckEqualVariances;
	private AutoCompleteTextFieldW fldConfLevel;

	private boolean isIniting;
	private FlowPanel testPanel;
	private FlowPanel intPanel;
	private FlowPanel mainPanel;
	private FlowPanel samplePanel;
	private TwoVarStatPanelW twoStatPanel;
	private TwoVarInferenceModel model;
	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanelW(AppW app, DataAnalysisViewW view) {
		
		isIniting = true;
		this.app = app;
		this.daView = view;
		model = new TwoVarInferenceModel(app, this);

		this.createGUIElements();
		this.updateGUI();
		this.setLabels();
		setStyleName("daTwoVarInference");
		isIniting = false;

	}

	// ============================================================
	// Create GUI
	// ============================================================

	private void createGUIElements() {

		// components
		lbTitle1 = new ListBox();
		lbTitle2 = new ListBox();
		lbTitle1.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				actionPerformed(lbTitle1);
			}
		});
		
		lbTitle2.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				actionPerformed(lbTitle2);
			}
		});
		lblTitle1 = new Label();
		lblTitle2 = new Label();

		ckEqualVariances = new CheckBox();

		lbAltHyp = new ListBox();

		lbAltHyp.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				actionPerformed(lbAltHyp);
			}
		});

		lblNull = new Label();
		lblHypParameter = new Label();
		lblTailType = new Label();

		fldNullHyp = new AutoCompleteTextFieldW(4, app);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
	            if (e.isEnterKey()) {
	            	doTextFieldActionPerformed(fldNullHyp);
	            }
            }});
		
		fldNullHyp.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldNullHyp);
			}
		});

		lblConfLevel = new Label();
		fldConfLevel = new AutoCompleteTextFieldW(4, app);
		fldConfLevel.setColumns(4);
		fldConfLevel.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
	            if (e.isEnterKey()) {
	            	doTextFieldActionPerformed(fldConfLevel);
	            }
            }});
		
		fldConfLevel.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldConfLevel);
			}
		});
		

		lblResultHeader = new Label();


		// test panel
		testPanel = new FlowPanel();
		testPanel.add(LayoutUtil.panelRow(lblNull, lblHypParameter));
		testPanel.add(LayoutUtil.panelRow(lblTailType, lbAltHyp));

		intPanel = new FlowPanel();
		intPanel.add(LayoutUtil.panelRow(lblConfLevel, fldConfLevel));

		twoStatPanel = new TwoVarStatPanelW(app, daView, model.isPairedData(), this);

		samplePanel = new FlowPanel();

		samplePanel.add(twoStatPanel);

		// Result panel
		resultTable = new StatTableW(app);
		model.setResults();

		resultPanel = new FlowPanel();

		resultPanel.add(resultTable);

		// main panel
		mainPanel = new FlowPanel();
		add(mainPanel);

	}

	private void updateMainPanel() {

		mainPanel.clear();

		// layout
		if (model.isTest())
			mainPanel.add(testPanel);
		else
			mainPanel.add(intPanel);

		mainPanel.add(samplePanel);
		// mainPanel.add(ckEqualVariances,c);
		mainPanel.add(resultPanel);

//		resultTable.getTable().setRowHeight(
//				twoStatPanel.getTable().getRowHeight());

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

	private void setTitleComboBoxes() {

		lbTitle1.clear();
		lbTitle2.clear();
		String[] dataTitles = daView.getDataTitles();
		if (dataTitles != null) {
			for (int i = 0; i < dataTitles.length; i++) {
				lbTitle1.addItem(dataTitles[i]);
				lbTitle2.addItem(dataTitles[i]);
			}
		}
		lbTitle1.setSelectedIndex(0);
		lbTitle2.setSelectedIndex(1);


	}

	private void updateCBAlternativeHyp() {

		lbAltHyp.clear();
		model.fillAlternateHyp();
	}

	public void setSelectedInference(int selectedPlot) {
		model.setSelectedInference(selectedPlot);
		if (!isIniting) {
			model.setResults();
			this.twoStatPanel.setTable(model.isPairedData());
		}
		updateGUI();
	}



	public void setLabels() {

		lblResultHeader.setText(app.getMenu("Result") + ": ");

		lblTitle1.setText(app.getMenu("Sample1") + ": ");
		lblTitle2.setText(app.getMenu("Sample2") + ": ");

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");

		// lblCI.setText("Interval Estimate");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");

		// btnCalc.setText(app.getMenu("Calculate"));

		ckEqualVariances.setText(app.getMenu("EqualVariance"));

	}

	public void updatePanel() {


		model.updateResults();
		updateGUI();

	}

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
		if (isIniting)
			return;

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


	public void setStatTable(int row, String[] rowNames, int length,
            String[] columnNames) {
		resultTable.setStatTable(1, null, columnNames.length, columnNames);
	  }

	public void setFormattedValueAt(double value, int row, int col) {
	    resultTable.setValueAt(daView.format(value), row, col);
    }

	public GeoList getDataSelected() {
	    return daView.getController().getDataSelected();
    }

	public int getSelectedDataIndex(int idx) {
	    return selectedDataIndex()[idx];
    }

	public double[] getValueArray(GeoList list) {
	    return daView.getController().getValueArray(list);
    }

	public void addAltHypItem(String name, String tail, double value) {
	    lbAltHyp.addItem(name + " " + tail + " " + daView.format(value));
    }

	public void selectAltHyp(int idx) {
	   lbAltHyp.setSelectedIndex(idx);
    }


}
