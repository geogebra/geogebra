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
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.geogebra.common.gui.view.data.OneVarModel;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Extended JPanel that contains interactive sub-panels for performing one
 * variable inference with the current data set.
 * 
 * @author G. Sturr
 * 
 */
public class OneVarInferencePanelD extends JPanel
		implements ActionListener, FocusListener, StatPanelInterface {
	private static final long serialVersionUID = 1L;
	// ggb fields
	private AppD app;
	private Kernel kernel;
	private DataAnalysisViewD statDialog;
	private StatTable resultTable;

	// GUI
	private JLabel lblHypParameter, lblTailType, lblNull, lblConfLevel,
			lblSigma, lblResultHeader;
	private JButton btnCalculate;
	private MyTextFieldD fldNullHyp, fldConfLevel, fldSigma;
	private JRadioButton btnLeft, btnRight, btnTwo;
	private JComboBox cbAltHyp;
	private JPanel testPanel, intPanel, mainPanel, resultPanel;
	private Box sigmaPanel;
	private int fieldWidth = 6;

	// test type (tail)



	// statistics


	// flags
	private boolean isIniting;
	private boolean isTest = true;
	private boolean isZProcedure;

	private LocalizationD loc;
	private final OneVarModel model;

	/***************************************
	 * Construct a OneVarInference panel
	 */
	public OneVarInferencePanelD(AppD app, DataAnalysisViewD statDialog) {

		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.statDialog = statDialog;
		this.model = new OneVarModel();

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

		btnLeft = new JRadioButton(OneVarModel.tail_left);
		btnRight = new JRadioButton(OneVarModel.tail_right);
		btnTwo = new JRadioButton(OneVarModel.tail_two);
		ButtonGroup group = new ButtonGroup();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);
		btnLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnTwo.addActionListener(this);
		btnTwo.setSelected(true);

		cbAltHyp = new JComboBox();
		cbAltHyp.addActionListener(this);

		lblNull = new JLabel();
		lblHypParameter = new JLabel();
		lblTailType = new JLabel();

		fldNullHyp = new MyTextFieldD(app);
		fldNullHyp.setColumns(fieldWidth);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addActionListener(this);
		fldNullHyp.addFocusListener(this);

		lblConfLevel = new JLabel();
		fldConfLevel = new MyTextFieldD(app);
		fldConfLevel.setColumns(fieldWidth);
		fldConfLevel.addActionListener(this);
		fldConfLevel.addFocusListener(this);

		lblSigma = new JLabel();
		fldSigma = new MyTextFieldD(app);
		fldSigma.setColumns(fieldWidth);
		fldSigma.addActionListener(this);
		fldSigma.addFocusListener(this);

		btnCalculate = new JButton();
		lblResultHeader = new JLabel();

		sigmaPanel = hBox(lblSigma, fldSigma);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(4, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;

		GridBagConstraints tab = new GridBagConstraints();
		tab.gridx = 0;
		tab.gridy = c.gridy;
		tab.weightx = 1;
		tab.insets = new Insets(4, 20, 0, 0);
		tab.anchor = GridBagConstraints.WEST;

		// test panel
		testPanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		testPanel.add(lblNull, c);
		testPanel.add(flowPanel(lblHypParameter, fldNullHyp), tab);
		testPanel.add(lblTailType, c);
		testPanel.add(cbAltHyp, tab);

		// CI panel
		intPanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		intPanel.add(lblConfLevel, c);
		intPanel.add(fldConfLevel, tab);

		// result panel
		resultTable = new StatTable(app);
		setResultTable();

		resultPanel = new JPanel(new BorderLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		resultPanel.add(lblResultHeader, BorderLayout.NORTH);
		resultPanel.add(resultTable, BorderLayout.CENTER);
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		// resultPanel.add(resultTable, c);

		// main panel
		mainPanel = new JPanel(new GridBagLayout());
		this.add(mainPanel, BorderLayout.NORTH);
		// this.add(resultPanel, BorderLayout.CENTER);
	}

	private void updateMainPanel() {

		mainPanel.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(4, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;

		GridBagConstraints tab = new GridBagConstraints();
		tab.gridx = 0;
		tab.gridy = c.gridy;
		tab.weightx = 1;
		tab.insets = new Insets(4, 20, 0, 0);
		tab.anchor = GridBagConstraints.WEST;

		c.gridy = GridBagConstraints.RELATIVE;
		if (isZProcedure) {
			mainPanel.add(sigmaPanel, tab);
		}

		if (isTest) {
			mainPanel.add(testPanel, c);
		} else {
			mainPanel.add(intPanel, c);
		}

		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(resultPanel, c);


	}

	private void setResultTable() {

		ArrayList<String> nameList = model.getNameList(loc);

		String[] rowNames = new String[nameList.size()];
		nameList.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 1, null);

	}

	private void updateResultTable() {

		DefaultTableModel model1 = resultTable.getModel();

		evaluate();
		String cInt = statDialog.format(model.getMean()) + " \u00B1 "
				+ statDialog.format(model.getMe());

		switch (model.selectedPlot) {
		default:
			// do nothing
			break;
		case StatisticsModel.INFER_ZTEST:
			model1.setValueAt(statDialog.format(model.getP()), 0, 0);
			model1.setValueAt(statDialog.format(model.getTestStat()), 1, 0);
			model1.setValueAt("", 2, 0);
			model1.setValueAt(statDialog.format(model.getN()), 3, 0);
			model1.setValueAt(statDialog.format(model.getMean()), 4, 0);
			break;

		case StatisticsModel.INFER_TTEST:
			model1.setValueAt(statDialog.format(model.getP()), 0, 0);
			model1.setValueAt(statDialog.format(model.getTestStat()), 1, 0);
			model1.setValueAt(statDialog.format(model.getDf()), 2, 0);
			model1.setValueAt(statDialog.format(model.getSe()), 3, 0);
			model1.setValueAt("", 4, 0);
			model1.setValueAt(statDialog.format(model.getN()), 5, 0);
			model1.setValueAt(statDialog.format(model.getMean()), 6, 0);
			break;

		case StatisticsModel.INFER_ZINT:
			model1.setValueAt(cInt, 0, 0);
			model1.setValueAt(statDialog.format(model.getLower()), 1, 0);
			model1.setValueAt(statDialog.format(model.getUpper()), 2, 0);
			model1.setValueAt(statDialog.format(model.getMe()), 3, 0);
			model1.setValueAt("", 4, 0);
			model1.setValueAt(statDialog.format(model.getN()), 5, 0);
			model1.setValueAt(statDialog.format(model.getMean()), 6, 0);
			break;

		case StatisticsModel.INFER_TINT:
			model1.setValueAt(cInt, 0, 0);
			model1.setValueAt(statDialog.format(model.getLower()), 1, 0);
			model1.setValueAt(statDialog.format(model.getUpper()), 2, 0);
			model1.setValueAt(statDialog.format(model.getMe()), 3, 0);
			model1.setValueAt(statDialog.format(model.getDf()), 4, 0);
			model1.setValueAt(statDialog.format(model.getSe()), 5, 0);
			model1.setValueAt("", 6, 0);
			model1.setValueAt(statDialog.format(model.getN()), 7, 0);
			model1.setValueAt(statDialog.format(model.getMean()), 8, 0);
			break;
		}

	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	@Override
	public void updateFonts(Font font) {
		// not needed
		// ... font updates handled by recursive call in StatDialog
	}

	@Override
	public void setLabels() {

		lblHypParameter.setText(loc.getMenu("HypothesizedMean.short") + " = ");
		lblNull.setText(loc.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(loc.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(loc.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(loc.getMenu("Result") + ": ");
		lblSigma.setText(loc.getMenu("StandardDeviation.short") + " = ");
		btnCalculate.setText(loc.getMenu("Calculate"));
		repaint();
	}

	/** Helper method for updateGUI() */
	private void updateNumberField(JTextField fld, double n) {

		fld.removeActionListener(this);
		fld.setText(statDialog.format(n));
		// fld.setCaretPosition(0);
		fld.addActionListener(this);

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

	@SuppressWarnings("unchecked")
	private void updateCBAlternativeHyp() {

		cbAltHyp.removeActionListener(this);
		cbAltHyp.removeAllItems();
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_right + " "
				+ statDialog.format(model.hypMean));
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_left
				+ " " + statDialog.format(model.hypMean));
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ OneVarModel.tail_two
				+ " " + statDialog.format(model.hypMean));

		if (model.tail == OneVarModel.tail_right) {
			cbAltHyp.setSelectedIndex(0);
		} else if (model.tail == OneVarModel.tail_left) {
			cbAltHyp.setSelectedIndex(1);
		} else {
			cbAltHyp.setSelectedIndex(2);
		}

		cbAltHyp.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isIniting) {
			return;
		}
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == cbAltHyp) {

			if (cbAltHyp.getSelectedIndex() == 0) {
				model.tail = OneVarModel.tail_right;
			} else if (cbAltHyp.getSelectedIndex() == 1) {
				model.tail = OneVarModel.tail_left;
			} else {
				model.tail = OneVarModel.tail_two;
			}

			evaluate();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
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

	@Override
	public void focusGained(FocusEvent e) {
		// nothing to do
	}

	@Override
	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	public void setSelectedPlot(int selectedPlot) {
		model.selectedPlot = selectedPlot;
		updateGUI();
	}

	@Override
	public void updatePanel() {
		// evaluate();
		updateGUI();
		// updateResultTable();
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

	private static JPanel flowPanel(Component... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

	private static Box hBox(Component... comp) {
		Box b = Box.createHorizontalBox();
		for (int i = 0; i < comp.length; i++) {
			b.add(comp[i]);
		}
		return b;
	}

}
