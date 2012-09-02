package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.geos.GeoList;
import geogebra.gui.inputfield.MyTextField;
import geogebra.main.AppD;

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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;

public class TwoVarInferencePanel extends JPanel implements ActionListener,
		FocusListener, StatPanelInterface {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private DataAnalysisViewD daView;
	private StatTable resultTable;

	private JList dataSourceList;
	private DefaultListModel model;

	private JComboBox cbTitle1, cbTitle2, cbAltHyp;
	private JLabel lblTitle1, lblTitle2, lblHypParameter, lblTailType, lblNull,
			lblCI, lblConfLevel, lblResultHeader;
	private JButton btnCalc;
	private MyTextField fldNullHyp;
	private JPanel cardProcedure, resultPanel;
	private JCheckBox ckEqualVariances;
	private MyTextField fldConfLevel;

	private int selectedInference = StatisticsPanel.INFER_TINT_2MEANS;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95, hypMean = 0;

	// statistics
	double t, P, df, lower, upper, mean, se, me, n1, n2, diffMeans, mean1,
			mean2;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private boolean pooled = false;

	private boolean isIniting;
	private JPanel testPanel;
	private JPanel intPanel;
	private JPanel mainPanel;
	private boolean isTest;
	private JPanel samplePanel;
	private TwoVarStatPanel twoStatPanel;
	private double meanDifference;

	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanel(AppD app, DataAnalysisViewD view) {
		isIniting = true;
		this.app = app;
		this.daView = view;

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

		twoStatPanel = new TwoVarStatPanel(app, daView, isPairedData(), this);

		samplePanel = new JPanel(new GridBagLayout());
		c.gridy = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;
		// samplePanel.add(flowPanel(lblTitle1, cbTitle1), c);
		// samplePanel.add(flowPanel(lblTitle2, cbTitle2), c);
		// samplePanel.add(ckEqualVariances, c);
		samplePanel.add(twoStatPanel, c);

		// Result panel
		resultTable = new StatTable(app);
		setResultTable();
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
		if (isTest)
			mainPanel.add(testPanel, c);
		else
			mainPanel.add(intPanel, c);

		mainPanel.add(samplePanel, tab);
		// mainPanel.add(ckEqualVariances,c);
		mainPanel.add(resultPanel, tab);

		resultTable.getTable().setRowHeight(
				twoStatPanel.getTable().getRowHeight());

	}

	private boolean isPairedData() {
		return (selectedInference == StatisticsPanel.INFER_TINT_PAIRED || selectedInference == StatisticsPanel.INFER_TTEST_PAIRED);
	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	private void updateGUI() {

		isTest = (selectedInference == StatisticsPanel.INFER_TTEST_2MEANS || selectedInference == StatisticsPanel.INFER_TTEST_PAIRED);

		if (isTest)
			lblHypParameter.setText(getNullHypName() + " = 0");

		ckEqualVariances.removeActionListener(this);
		// ckEqualVariances.setVisible(
		// selectedPlot == StatisticsPanel.INFER_TINT_2MEANS
		// || selectedPlot == StatisticsPanel.INFER_TTEST_2MEANS);
		ckEqualVariances.setSelected(pooled);
		ckEqualVariances.addActionListener(this);

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateCBAlternativeHyp();

		// setResultTable();
		updateResultTable();

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
		cbAltHyp.addItem(getNullHypName() + " " + tail_right + " "
				+ daView.format(hypMean));
		cbAltHyp.addItem(getNullHypName() + " " + tail_left + " "
				+ daView.format(hypMean));
		cbAltHyp.addItem(getNullHypName() + " " + tail_two + " "
				+ daView.format(hypMean));
		if (tail == tail_right)
			cbAltHyp.setSelectedIndex(0);
		else if (tail == tail_left)
			cbAltHyp.setSelectedIndex(1);
		else
			cbAltHyp.setSelectedIndex(2);
		cbAltHyp.addActionListener(this);

	}

	public void setSelectedInference(int selectedPlot) {
		this.selectedInference = selectedPlot;
		if (!isIniting) {
			this.setResultTable();
			this.twoStatPanel.setTable(isPairedData());
		}
		updateGUI();
	}

	public void updateFonts(Font font) {
		twoStatPanel.updateFonts(font);

	}

	private String getNullHypName() {

		if (selectedInference == StatisticsPanel.INFER_TTEST_2MEANS)
			return app.getMenu("DifferenceOfMeans.short");
		else if (selectedInference == StatisticsPanel.INFER_TTEST_PAIRED)
			return app.getMenu("MeanDifference");
		else
			return "";

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
		updateResultTable();

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
			if (cbAltHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if (cbAltHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;
			updateResultTable();
		}

		else if (source == cbTitle1 || source == cbTitle2) {
			updateResultTable();
		}

		else if (source == ckEqualVariances) {
			pooled = ckEqualVariances.isSelected();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if (isIniting)
			return;

		Double value = Double.parseDouble(source.getText().trim());

		if (source == fldConfLevel) {
			confLevel = value;
			updateGUI();
		}

		if (source == fldNullHyp) {
			hypMean = value;
			updateGUI();
		}

	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField) (e.getSource()));
	}

	// =======================================
	// Result Table
	// =======================================

	private void setResultTable() {

		ArrayList<String> list = new ArrayList<String>();

		switch (selectedInference) {
		case StatisticsPanel.INFER_TTEST_2MEANS:
		case StatisticsPanel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsPanel.INFER_TTEST_PAIRED)
				list.add(app.getMenu("MeanDifference"));
			else
				list.add(app.getPlain("fncInspector.Difference"));

			list.add(app.getMenu("PValue"));
			list.add(app.getMenu("TStatistic"));
			list.add(app.getMenu("StandardError.short"));
			list.add(app.getMenu("DegreesOfFreedom.short"));
			break;

		case StatisticsPanel.INFER_TINT_2MEANS:
		case StatisticsPanel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsPanel.INFER_TINT_PAIRED)
				list.add(app.getMenu("MeanDifference"));
			else
				list.add(app.getPlain("fncInspector.Difference"));

			list.add(app.getMenu("MarginOfError.short"));
			list.add(app.getMenu("LowerLimit"));
			list.add(app.getMenu("UpperLimit"));
			list.add(app.getMenu("StandardError.short"));
			list.add(app.getMenu("DegreesOfFreedom.short"));
			break;
		}

		String[] columnNames = new String[list.size()];
		list.toArray(columnNames);
		resultTable.setStatTable(1, null, columnNames.length, columnNames);

	}

	private void updateResultTable() {

		DefaultTableModel model = resultTable.getModel();

		boolean ok = evaluate();

		if (!ok) {
			// resultTable.clear();
			return;
		}

		switch (selectedInference) {
		case StatisticsPanel.INFER_TTEST_2MEANS:
		case StatisticsPanel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsPanel.INFER_TTEST_PAIRED)
				model.setValueAt(daView.format(meanDifference), 0, 0);
			else
				model.setValueAt(daView.format(diffMeans), 0, 0);

			model.setValueAt(daView.format(P), 0, 1);
			model.setValueAt(daView.format(t), 0, 2);
			model.setValueAt(daView.format(se), 0, 3);
			model.setValueAt(daView.format(df), 0, 4);
			break;

		case StatisticsPanel.INFER_TINT_2MEANS:
		case StatisticsPanel.INFER_TINT_PAIRED:

			// String cInt = statDialog.format(mean) + " \u00B1 " +
			// statDialog.format(me);
			// model.setValueAt(cInt,0,0);

			if (selectedInference == StatisticsPanel.INFER_TINT_PAIRED)
				model.setValueAt(daView.format(meanDifference), 0, 0);
			else
				model.setValueAt(daView.format(diffMeans), 0, 0);

			model.setValueAt(daView.format(me), 0, 1);
			model.setValueAt(daView.format(lower), 0, 2);
			model.setValueAt(daView.format(upper), 0, 3);
			model.setValueAt(daView.format(se), 0, 4);
			model.setValueAt(daView.format(df), 0, 5);

			break;
		}

	}

	private Integer[] selectedDataIndex() {
		return twoStatPanel.getSelectedDataIndex();
	}

	// ============================================================
	// Evaluate
	// ============================================================

	private boolean evaluate() {

		// get the sample data

		GeoList dataCollection = daView.getController().getDataSelected();

		GeoList dataList1 = (GeoList) dataCollection
				.get(selectedDataIndex()[0]);
		double[] sample1 = daView.getController().getValueArray(dataList1);
		SummaryStatistics stats1 = new SummaryStatistics();
		for (int i = 0; i < sample1.length; i++) {
			stats1.addValue(sample1[i]);
		}

		GeoList dataList2 = (GeoList) dataCollection
				.get(selectedDataIndex()[1]);
		double[] sample2 = daView.getController().getValueArray(dataList2);
		SummaryStatistics stats2 = new SummaryStatistics();
		for (int i = 0; i < sample2.length; i++) {
			stats2.addValue(sample2[i]);
		}

		// exit if paired data is expected and sample sizes are unequal
		if (isPairedData() && stats1.getN() != stats2.getN())
			return false;

		if (tTestImpl == null)
			tTestImpl = new TTestImpl();
		double tCritical;

		try {

			switch (selectedInference) {
			case StatisticsPanel.INFER_TTEST_2MEANS:
			case StatisticsPanel.INFER_TINT_2MEANS:

				// get statistics
				mean1 = StatUtils.mean(sample1);
				mean2 = StatUtils.mean(sample2);
				diffMeans = mean1 - mean2;
				n1 = stats1.getN();
				n2 = stats2.getN();
				double v1 = stats1.getVariance();
				double v2 = stats2.getVariance();
				df = getDegreeOfFreedom(v1, v2, n1, n2, pooled);

				if (pooled) {
					double pooledVariance = ((n1 - 1) * v1 + (n2 - 1) * v2)
							/ (n1 + n2 - 2);
					se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
				} else
					se = Math.sqrt((v1 / n1) + (v2 / n2));

				// get confidence interval
				tDist = new TDistributionImpl(df);
				tCritical = tDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = tCritical * se;
				upper = diffMeans + me;
				lower = diffMeans - me;

				// get test results
				if (pooled) {
					t = tTestImpl.homoscedasticT(sample1, sample2);
					P = tTestImpl.homoscedasticTTest(sample1, sample2);
				} else {
					t = tTestImpl.t(sample1, sample2);
					P = tTestImpl.tTest(sample1, sample2);
				}
				P = adjustedPValue(P, t, tail);

				break;

			case StatisticsPanel.INFER_TTEST_PAIRED:
			case StatisticsPanel.INFER_TINT_PAIRED:

				// get statistics
				n1 = sample1.length;
				meanDifference = StatUtils.meanDifference(sample1, sample2);
				se = Math.sqrt(StatUtils.varianceDifference(sample1, sample2,
						meanDifference) / n1);
				df = n1 - 1;

				tDist = new TDistributionImpl(df);
				tCritical = tDist
						.inverseCumulativeProbability((confLevel + 1d) / 2);
				me = tCritical * se;
				upper = meanDifference + me;
				lower = meanDifference - me;

				// get test results
				t = meanDifference / se;
				P = 2.0 * tDist.cumulativeProbability(-Math.abs(t));
				P = adjustedPValue(P, t, tail);

				break;
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (MathException e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	// TODO: Validate !!!!!!!!!!!

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

	/**
	 * Computes approximate degrees of freedom for 2-sample t-estimate. (code
	 * from Apache commons, TTestImpl class)
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param n1
	 *            first sample n
	 * @param n2
	 *            second sample n
	 * @return approximate degrees of freedom
	 */
	private double getDegreeOfFreedom(double v1, double v2, double n1,
			double n2, boolean pooled) {

		if (pooled)
			return n1 + n2 - 2;

		else
			return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2)))
					/ ((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2)
							/ (n2 * n2 * (n2 - 1d)));
	}

	/**
	 * Computes margin of error for 2-sample t-estimate; this is the half-width
	 * of the confidence interval
	 * 
	 * @param v1
	 *            first sample variance
	 * @param v2
	 *            second sample variance
	 * @param n1
	 *            first sample n
	 * @param n2
	 *            second sample n
	 * @param confLevel
	 *            confidence level
	 * @return margin of error for 2 mean interval estimate
	 * @throws MathException
	 */
	private double getMarginOfError(double v1, double n1, double v2, double n2,
			double confLevel, boolean pooled) throws MathException {

		if (pooled) {

			double pooledVariance = ((n1 - 1) * v1 + (n2 - 1) * v2)
					/ (n1 + n2 - 2);
			double se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2,
					pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
			return a * se;

		} else {

			double se = Math.sqrt((v1 / n1) + (v2 / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2,
					pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d) / 2);
			return a * se;
		}

	}

	// ============================================================
	// GUI Utilities
	// ============================================================

	private JPanel flowPanel(Component... comp) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < comp.length; i++) {
			p.add(comp[i]);
		}
		// p.setBackground(Color.white);
		return p;
	}

}
