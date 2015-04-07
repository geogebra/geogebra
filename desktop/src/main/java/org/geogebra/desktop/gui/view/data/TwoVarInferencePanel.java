package org.geogebra.desktop.gui.view.data;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geogebra.common.gui.view.data.TwoVarInferenceModel;
import org.geogebra.common.gui.view.data.TwoVarInferenceModel.TwoVarInferenceListener;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.gui.inputfield.MyTextField;
import org.geogebra.desktop.main.AppD;

public class TwoVarInferencePanel extends JPanel implements ActionListener,
		FocusListener, StatPanelInterface, TwoVarInferenceListener {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private DataAnalysisViewD daView;
	private StatTable resultTable;

	private JList dataSourceList;
	private DefaultListModel tblModel;

	private JComboBox cbTitle1, cbTitle2, cbAltHyp;
	private JLabel lblTitle1, lblTitle2, lblHypParameter, lblTailType, lblNull,
			lblCI, lblConfLevel, lblResultHeader;
	private JButton btnCalc;
	private MyTextField fldNullHyp;
	private JPanel cardProcedure, resultPanel;
	private JCheckBox ckEqualVariances;
	private MyTextField fldConfLevel;

	private boolean isIniting;
	private JPanel testPanel;
	private JPanel intPanel;
	private JPanel mainPanel;
	private JPanel samplePanel;
	private TwoVarStatPanel twoStatPanel;
	private TwoVarInferenceModel model;

	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanel(AppD app, DataAnalysisViewD view) {
		isIniting = true;
		this.app = app;
		this.daView = view;
		model = new TwoVarInferenceModel(app, this);
		// this.setMinimumSize(new Dimension(50,50));

		this.setLayout(new BorderLayout());
		this.createGUIElements();
		this.updateGUI();
		this.setLabels();

		isIniting = false;

	}

	// ============================================================
	// Create GUI
	// ============================================================

	private void createGUIElements() {

		// components
		cbTitle1 = new JComboBox();
		cbTitle2 = new JComboBox();
		cbTitle1.addActionListener(this);
		cbTitle2.addActionListener(this);

		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		ckEqualVariances = new JCheckBox();

		cbAltHyp = new JComboBox();
		cbAltHyp.addActionListener(this);

		lblNull = new JLabel();
		lblHypParameter = new JLabel();
		lblTailType = new JLabel();

		fldNullHyp = new MyTextField(app);
		fldNullHyp.setColumns(4);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addActionListener(this);
		fldNullHyp.addFocusListener(this);

		lblConfLevel = new JLabel();
		fldConfLevel = new MyTextField(app);
		fldConfLevel.setColumns(4);
		fldConfLevel.addActionListener(this);
		fldConfLevel.addFocusListener(this);

		lblResultHeader = new JLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(4, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;

		GridBagConstraints tab = new GridBagConstraints();
		tab.gridx = 0;
		tab.gridy = c.gridy;
		tab.weightx = 1;
		tab.insets = new Insets(4, 20, 0, 0);
		tab.anchor = GridBagConstraints.NORTHWEST;

		// test panel
		testPanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		testPanel.add(flowPanel(lblNull, lblHypParameter), tab);
		testPanel.add(flowPanel(lblTailType, cbAltHyp), tab);
		// testPanel.setBorder(BorderFactory.createEtchedBorder());

		// CI panel
		intPanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		intPanel.add(flowPanel(lblConfLevel, fldConfLevel), tab);

		// sample panel

		twoStatPanel = new TwoVarStatPanel(app, daView, model.isPairedData(),
				this);

		samplePanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;
		// samplePanel.add(flowPanel(lblTitle1, cbTitle1), c);
		// samplePanel.add(flowPanel(lblTitle2, cbTitle2), c);
		// samplePanel.add(ckEqualVariances, c);
		samplePanel.add(twoStatPanel, c);

		// Result panel
		resultTable = new StatTable(app);
		model.setResults();
		// resultTable.setBorder(BorderFactory.createEtchedBorder());

		resultPanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;
		// resultPanel.add(lblResultHeader, c);
		resultPanel.add(resultTable, c);

		// main panel
		mainPanel = new JPanel(new GridBagLayout());
		this.add(mainPanel, BorderLayout.NORTH);

	}

	private void updateMainPanel() {

		mainPanel.removeAll();

		// constraints
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 4, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints tab = new GridBagConstraints();
		tab.gridx = 0;
		tab.gridy = c.gridy;
		tab.weightx = 1;
		tab.insets = new Insets(4, 20, 0, 20);
		tab.fill = GridBagConstraints.HORIZONTAL;
		tab.anchor = GridBagConstraints.NORTHWEST;

		// layout
		if (model.isTest())
			mainPanel.add(testPanel, c);
		else
			mainPanel.add(intPanel, c);

		mainPanel.add(samplePanel, tab);
		// mainPanel.add(ckEqualVariances,c);
		mainPanel.add(resultPanel, tab);

		resultTable.getTable().setRowHeight(
				twoStatPanel.getTable().getRowHeight());

	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	private void updateGUI() {

		if (model.isTest()) {
			lblHypParameter.setText(model.getNullHypName() + " = 0");
		}

		ckEqualVariances.removeActionListener(this);
		// ckEqualVariances.setVisible(
		// selectedPlot == StatisticsModel.INFER_TINT_2MEANS
		// || selectedPlot == StatisticsModel.INFER_TTEST_2MEANS);
		ckEqualVariances.setSelected(model.isPooled());
		ckEqualVariances.addActionListener(this);

		updateNumberField(fldNullHyp, model.getHypMean());
		updateNumberField(fldConfLevel, model.getConfLevel());
		updateCBAlternativeHyp();

		// setResultTable();
		model.updateResults();

		updateMainPanel();

		twoStatPanel.updatePanel();

	}

	/** Helper method for updateGUI() */
	private void updateNumberField(JTextField fld, double n) {

		fld.removeActionListener(this);
		fld.setText(daView.format(n));
		// fld.setCaretPosition(0);
		fld.addActionListener(this);
	}

	private void setTitleComboBoxes() {

		cbTitle1.removeActionListener(this);
		cbTitle2.removeActionListener(this);

		cbTitle1.removeAllItems();
		cbTitle2.removeAllItems();
		String[] dataTitles = daView.getDataTitles();
		if (dataTitles != null) {
			for (int i = 0; i < dataTitles.length; i++) {
				cbTitle1.addItem(dataTitles[i]);
				cbTitle2.addItem(dataTitles[i]);
			}
		}
		cbTitle1.setSelectedIndex(0);
		cbTitle2.setSelectedIndex(1);

		cbTitle1.addActionListener(this);
		cbTitle2.addActionListener(this);

	}

	private void updateCBAlternativeHyp() {

		cbAltHyp.removeActionListener(this);
		cbAltHyp.removeAllItems();
		model.fillAlternateHyp();
		cbAltHyp.addActionListener(this);

	}

	public void setSelectedInference(int selectedPlot) {
		model.setSelectedInference(selectedPlot);
		if (!isIniting) {
			model.setResults();
			twoStatPanel.setTable(model.isPairedData());
		}
		updateGUI();
	}

	public void updateFonts(Font font) {
		twoStatPanel.updateFonts(font);

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

		updateGUI();
		model.updateResults();

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// handle update event from table
		if (e.getActionCommand().equals("updateTable")) {
			this.updatePanel();
		}

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == cbAltHyp) {
			model.applyTail(cbAltHyp.getSelectedIndex());
		}

		else if (source == cbTitle1 || source == cbTitle2) {
			model.updateResults();
		}

		else if (source == ckEqualVariances) {
			model.setPooled(ckEqualVariances.isSelected());
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
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

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	private Integer[] selectedDataIndex() {
		return twoStatPanel.getSelectedDataIndex();
	}

	// ============================================================
	// GUI Utilities
	// ============================================================

	private JPanel flowPanel(Component... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		return p;
	}

	public void setStatTable(int row, String[] rowNames, int length,
			String[] columnNames) {
		resultTable
				.setStatTable(row, rowNames, columnNames.length, columnNames);
	}

	public void setFormattedValueAt(double value, int row, int col) {
		resultTable.getModel().setValueAt(daView.format(value), row, col);
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
		cbAltHyp.addItem(name + " " + tail + " " + daView.format(value));
	}

	public void selectAltHyp(int idx) {
		cbAltHyp.setSelectedIndex(idx);
	}

}
