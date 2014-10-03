package geogebra.web.gui.view.data;

import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.data.StatisticsModel;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.geos.GeoList;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.html5.main.AppW;

import java.util.ArrayList;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class TwoVarInferencePanelW extends FlowPanel implements StatPanelInterfaceW {
	private static final long serialVersionUID = 1L;
	private AppW app;
	private DataAnalysisViewW daView;
	private StatTableW resultTable;

//	private JList dataSourceList;
//	private DefaultListModel model;

	private ListBox lbTitle1, lbTitle2, lbAltHyp;
	private Label lblTitle1, lblTitle2, lblHypParameter, lblTailType, lblNull,
			lblCI, lblConfLevel, lblResultHeader;
	private Button btnCalc;
	private AutoCompleteTextFieldW fldNullHyp;
	private FlowPanel cardProcedure, resultPanel;
	private CheckBox ckEqualVariances;
	private AutoCompleteTextFieldW fldConfLevel;

	private int selectedInference = StatisticsModel.INFER_TINT_2MEANS;

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
	private FlowPanel testPanel;
	private FlowPanel intPanel;
	private FlowPanel mainPanel;
	private boolean isTest;
	private FlowPanel samplePanel;
	private TwoVarStatPanelW twoStatPanel;
	private double meanDifference;

	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanelW(AppW app, DataAnalysisViewW view) {
		isIniting = true;
		this.app = app;
		this.daView = view;

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

		twoStatPanel = new TwoVarStatPanelW(app, daView, isPairedData());

		samplePanel = new FlowPanel();

		samplePanel.add(twoStatPanel);

		// Result panel
		resultTable = new StatTableW(app);
		setResultTable();
		// resultTable.setBorder(BorderFactory.createEtchedBorder());

		resultPanel = new FlowPanel();
		// resultPanel.add(lblResultHeader, c);
		resultPanel.add(resultTable);

		// main panel
		mainPanel = new FlowPanel();
		add(mainPanel);

	}

	private void updateMainPanel() {

		mainPanel.clear();;

		// layout
		if (isTest)
			mainPanel.add(testPanel);
		else
			mainPanel.add(intPanel);

		mainPanel.add(samplePanel);
		// mainPanel.add(ckEqualVariances,c);
		mainPanel.add(resultPanel);

//		resultTable.getTable().setRowHeight(
//				twoStatPanel.getTable().getRowHeight());

	}

	private boolean isPairedData() {
		return (selectedInference == StatisticsModel.INFER_TINT_PAIRED || selectedInference == StatisticsModel.INFER_TTEST_PAIRED);
	}

	// ============================================================
	// Updates and Event Handlers
	// ============================================================

	private void updateGUI() {

		isTest = (selectedInference == StatisticsModel.INFER_TTEST_2MEANS || selectedInference == StatisticsModel.INFER_TTEST_PAIRED);

		if (isTest) {
			lblHypParameter.setText(getNullHypName() + " = 0");
		}

		// ckEqualVariances.setVisible(
		// selectedPlot == StatisticsModel.INFER_TINT_2MEANS
		// || selectedPlot == StatisticsModel.INFER_TTEST_2MEANS);
		ckEqualVariances.setValue(pooled);

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateCBAlternativeHyp();

		// setResultTable();
		updateResultTable();

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
		lbAltHyp.addItem(getNullHypName() + " " + tail_right + " "
				+ daView.format(hypMean));
		lbAltHyp.addItem(getNullHypName() + " " + tail_left + " "
				+ daView.format(hypMean));
		lbAltHyp.addItem(getNullHypName() + " " + tail_two + " "
				+ daView.format(hypMean));
		if (tail == tail_right)
			lbAltHyp.setSelectedIndex(0);
		else if (tail == tail_left)
			lbAltHyp.setSelectedIndex(1);
		else
			lbAltHyp.setSelectedIndex(2);

	}

	public void setSelectedInference(int selectedPlot) {
		this.selectedInference = selectedPlot;
		if (!isIniting) {
			this.setResultTable();
			this.twoStatPanel.setTable(isPairedData());
		}
		updateGUI();
	}


	private String getNullHypName() {

		if (selectedInference == StatisticsModel.INFER_TTEST_2MEANS)
			return app.getMenu("DifferenceOfMeans.short");
		else if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED)
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

	public void actionPerformed(Object source) {
//		// handle update event from table
//		if (e.getActionCommand().equals("updateTable")) {
//			this.updatePanel();
//		}

		if (source instanceof AutoCompleteTextFieldW) {
			doTextFieldActionPerformed((AutoCompleteTextFieldW) source);
		}

		else if (source == lbAltHyp) {
			if (lbAltHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if (lbAltHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;
			updateResultTable();
		}

		else if (source == lbTitle1 || source == lbTitle2) {
			updateResultTable();
		}

		else if (source == ckEqualVariances) {
			pooled = ckEqualVariances.getValue();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
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

	// =======================================
	// Result Table
	// =======================================

	private void setResultTable() {

		ArrayList<String> list = new ArrayList<String>();

		switch (selectedInference) {
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED)
				list.add(app.getMenu("MeanDifference"));
			else
				list.add(app.getPlain("fncInspector.Difference"));

			list.add(app.getMenu("PValue"));
			list.add(app.getMenu("TStatistic"));
			list.add(app.getMenu("StandardError.short"));
			list.add(app.getMenu("DegreesOfFreedom.short"));
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED)
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

		boolean ok = evaluate();

		if (!ok) {
			// resultTable.clear();
			return;
		}

		switch (selectedInference) {
		case StatisticsModel.INFER_TTEST_2MEANS:
		case StatisticsModel.INFER_TTEST_PAIRED:

			if (selectedInference == StatisticsModel.INFER_TTEST_PAIRED)
				resultTable.setValueAt(daView.format(meanDifference), 0, 0);
			else
				resultTable.setValueAt(daView.format(diffMeans), 0, 0);

			resultTable.setValueAt(daView.format(P), 0, 1);
			resultTable.setValueAt(daView.format(t), 0, 2);
			resultTable.setValueAt(daView.format(se), 0, 3);
			resultTable.setValueAt(daView.format(df), 0, 4);
			break;

		case StatisticsModel.INFER_TINT_2MEANS:
		case StatisticsModel.INFER_TINT_PAIRED:

			// String cInt = statDialog.format(mean) + " \u00B1 " +
			// statDialog.format(me);
			// resultTable.setValueAt(cInt,0,0);

			if (selectedInference == StatisticsModel.INFER_TINT_PAIRED)
				resultTable.setValueAt(daView.format(meanDifference), 0, 0);
			else
				resultTable.setValueAt(daView.format(diffMeans), 0, 0);

			resultTable.setValueAt(daView.format(me), 0, 1);
			resultTable.setValueAt(daView.format(lower), 0, 2);
			resultTable.setValueAt(daView.format(upper), 0, 3);
			resultTable.setValueAt(daView.format(se), 0, 4);
			resultTable.setValueAt(daView.format(df), 0, 5);

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
			case StatisticsModel.INFER_TTEST_2MEANS:
			case StatisticsModel.INFER_TINT_2MEANS:

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

			case StatisticsModel.INFER_TTEST_PAIRED:
			case StatisticsModel.INFER_TINT_PAIRED:

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


}
