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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.geogebra.common.gui.view.data.StatisticsModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.NumberValue;
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
public class OneVarInferencePanel extends JPanel implements ActionListener,
		FocusListener, StatPanelInterface {
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
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95, hypMean = 0, sigma = 1;

	// statistics
	double testStat, P, df, lower, upper, mean, se, me, N;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private NormalDistributionImpl normalDist;

	// flags
	private boolean isIniting;
	private boolean isTest = true;
	private boolean isZProcedure;

	private int selectedPlot = StatisticsModel.INFER_TINT;
	private LocalizationD loc;

	/***************************************
	 * Construct a OneVarInference panel
	 */
	public OneVarInferencePanel(AppD app, DataAnalysisViewD statDialog) {

		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.statDialog = statDialog;

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

		btnLeft = new JRadioButton(tail_left);
		btnRight = new JRadioButton(tail_right);
		btnTwo = new JRadioButton(tail_two);
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
		if (isZProcedure)
			mainPanel.add(sigmaPanel, tab);

		if (isTest)
			mainPanel.add(testPanel, c);
		else
			mainPanel.add(intPanel, c);

		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(resultPanel, c);

	}

	private void setResultTable() {

		ArrayList<String> nameList = new ArrayList<String>();

		switch (selectedPlot) {
		case StatisticsModel.INFER_ZTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("ZStatistic"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));

			break;

		case StatisticsModel.INFER_TTEST:
			nameList.add(loc.getMenu("PValue"));
			nameList.add(loc.getMenu("TStatistic"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_ZINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;

		case StatisticsModel.INFER_TINT:
			nameList.add(loc.getMenu("Interval"));
			nameList.add(loc.getMenu("LowerLimit"));
			nameList.add(loc.getMenu("UpperLimit"));
			nameList.add(loc.getMenu("MarginOfError"));
			nameList.add(loc.getMenu("DegreesOfFreedom.short"));
			nameList.add(loc.getMenu("StandardError.short"));
			nameList.add(loc.getMenu(""));
			nameList.add(loc.getMenu("Length.short"));
			nameList.add(loc.getMenu("Mean"));
			break;
		}

		String[] rowNames = new String[nameList.size()];
		nameList.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 1, null);

	}

	private void updateResultTable() {

		DefaultTableModel model = resultTable.getModel();

		evaluate();
		String cInt = statDialog.format(mean) + " \u00B1 "
				+ statDialog.format(me);

		switch (selectedPlot) {
		case StatisticsModel.INFER_ZTEST:
			model.setValueAt(statDialog.format(P), 0, 0);
			model.setValueAt(statDialog.format(testStat), 1, 0);
			model.setValueAt("", 2, 0);
			model.setValueAt(statDialog.format(N), 3, 0);
			model.setValueAt(statDialog.format(mean), 4, 0);
			break;

		case StatisticsModel.INFER_TTEST:
			model.setValueAt(statDialog.format(P), 0, 0);
			model.setValueAt(statDialog.format(testStat), 1, 0);
			model.setValueAt(statDialog.format(df), 2, 0);
			model.setValueAt(statDialog.format(se), 3, 0);
			model.setValueAt("", 4, 0);
			model.setValueAt(statDialog.format(N), 5, 0);
			model.setValueAt(statDialog.format(mean), 6, 0);
			break;

		case StatisticsModel.INFER_ZINT:
			model.setValueAt(cInt, 0, 0);
			model.setValueAt(statDialog.format(lower), 1, 0);
			model.setValueAt(statDialog.format(upper), 2, 0);
			model.setValueAt(statDialog.format(me), 3, 0);
			model.setValueAt("", 4, 0);
			model.setValueAt(statDialog.format(N), 5, 0);
			model.setValueAt(statDialog.format(mean), 6, 0);
			break;

		case StatisticsModel.INFER_TINT:
			model.setValueAt(cInt, 0, 0);
			model.setValueAt(statDialog.format(lower), 1, 0);
			model.setValueAt(statDialog.format(upper), 2, 0);
			model.setValueAt(statDialog.format(me), 3, 0);
			model.setValueAt(statDialog.format(df), 4, 0);
			model.setValueAt(statDialog.format(se), 5, 0);
			model.setValueAt("", 6, 0);
			model.setValueAt(statDialog.format(N), 7, 0);
			model.setValueAt(statDialog.format(mean), 8, 0);
			break;
		}

	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	public void updateFonts(Font font) {
		// not needed
		// ... font updates handled by recursive call in StatDialog
	}

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

		isTest = (selectedPlot == StatisticsModel.INFER_ZTEST || selectedPlot == StatisticsModel.INFER_TTEST);

		isZProcedure = selectedPlot == StatisticsModel.INFER_ZTEST
				|| selectedPlot == StatisticsModel.INFER_ZINT;

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateNumberField(fldSigma, sigma);
		updateCBAlternativeHyp();
		setResultTable();
		updateResultTable();
		updateMainPanel();
	}

	private void updateCBAlternativeHyp() {

		cbAltHyp.removeActionListener(this);
		cbAltHyp.removeAllItems();
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ tail_right + " " + statDialog.format(hypMean));
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " "
				+ tail_left + " " + statDialog.format(hypMean));
		cbAltHyp.addItem(loc.getMenu("HypothesizedMean.short") + " " + tail_two
				+ " " + statDialog.format(hypMean));

		if (tail == tail_right)
			cbAltHyp.setSelectedIndex(0);
		else if (tail == tail_left)
			cbAltHyp.setSelectedIndex(1);
		else
			cbAltHyp.setSelectedIndex(2);

		cbAltHyp.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (isIniting)
			return;
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		else if (source == cbAltHyp) {

			if (cbAltHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if (cbAltHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;

			evaluate();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if (isIniting)
			return;

		Double value = Double.parseDouble(source.getText().trim());

		if (source == fldConfLevel) {
			confLevel = value;
			evaluate();
			updateGUI();
		}

		else if (source == fldNullHyp) {
			hypMean = value;
			evaluate();
			updateGUI();
		}

		else if (source == fldSigma) {
			sigma = value;
			evaluate();
			updateGUI();
		}

	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	public void setSelectedPlot(int selectedPlot) {
		this.selectedPlot = selectedPlot;
		updateGUI();
	}

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

		mean = StatUtils.mean(sample);
		N = sample.length;

		try {
			switch (selectedPlot) {

			case StatisticsModel.INFER_ZTEST:
			case StatisticsModel.INFER_ZINT:
				normalDist = new NormalDistributionImpl(0, 1);
				se = sigma / Math.sqrt(N);
				testStat = (mean - hypMean) / se;
				P = 2.0 * normalDist.cumulativeProbability(-Math.abs(testStat));
				P = adjustedPValue(P, testStat, tail);

				double zCritical = normalDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = zCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;

			case StatisticsModel.INFER_TTEST:
			case StatisticsModel.INFER_TINT:
				if (tTestImpl == null)
					tTestImpl = new TTestImpl();
				se = Math.sqrt(StatUtils.variance(sample) / N);
				df = N - 1;
				testStat = tTestImpl.t(hypMean, sample);
				P = tTestImpl.tTest(hypMean, sample);
				P = adjustedPValue(P, testStat, tail);

				tDist = new TDistributionImpl(N - 1);
				double tCritical = tDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = tCritical * se;
				upper = mean + me;
				lower = mean - me;
				break;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

	private double adjustedPValue(double p, double testStatistic, String tail) {

		// two sided test
		if (tail.equals(tail_two))
			return p;

		// one sided test
		else if ((tail.equals(tail_right) && testStatistic > 0)
				|| (tail.equals(tail_left) && testStatistic < 0))
			return p / 2;
		else
			return 1 - p / 2;
	}

	protected double evaluateExpression(String expr) {

		NumberValue nv;

		try {
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}
		return nv.getDouble();
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

	private JPanel blPanel(Component center, Component north, Component south,
			Component west, Component east) {
		JPanel p = new JPanel(new BorderLayout());
		if (center != null)
			p.add(center, BorderLayout.CENTER);
		if (north != null)
			p.add(north, BorderLayout.NORTH);
		if (south != null)
			p.add(south, BorderLayout.SOUTH);
		if (west != null)
			p.add(west, loc.borderWest());
		if (east != null)
			p.add(east, loc.borderEast());

		// p.setBackground(Color.white);
		return p;
	}

}
