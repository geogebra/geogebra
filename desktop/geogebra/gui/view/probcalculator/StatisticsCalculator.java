package geogebra.gui.view.probcalculator;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.gui.view.data.StatTable;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.gui.util.ListSeparatorRenderer;
import geogebra.gui.view.data.StatTable;
import geogebra.gui.view.data.StatisticsPanel;
import geogebra.main.AppD;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatisticsCalculator extends JPanel implements ActionListener,
		FocusListener, SetLabels {

	private AppD app;

	// GUI elements
	private MyTextField[] fldSampleStat1, fldSampleStat2;
	private JTextArea taResultTest, taResultEstimate;
	private JLabel[] lblSampleStat1, lblSampleStat2;
	private JRadioButton btnLeft, btnRight, btnTwo;

	private JLabel lblHypParameter, lblTailType, lblNull, lblConfLevel,
			lblSigma, lblResultHeader, lblSampleHeader1, lblSampleHeader2;
	private JButton btnCalculate;
	private MyTextField fldNullHyp, fldConfLevel, fldSigma;
	private JTextArea taResultLog;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;
	private String tail = tail_two;

	private JComboBox cpProcedure;

	private int fieldWidth = 6;
	private double confLevel = .95, hypMean = 0;

	private JPanel panelInput, panelTest, panelEstimate, panelControl;

	JScrollPane resultScroller;

	private enum Procedure {
		ZMEAN_TEST, ZMEAN2_TEST, TMEAN_TEST, TMEAN2_TEST, ZPROP_TEST, ZPROP2_TEST,

		ZMEAN_CI, ZMEAN2_CI, TMEAN_CI, TMEAN2_CI, ZPROP_CI, ZPROP2_CI, GOF_TEST, CHISQ_TEST
	}

	private Procedure selectedProcedure = Procedure.ZMEAN_TEST;

	private StatTable resultTable;

	private JComboBox cbProcedure;

	private String[] meanZSampleLabel;

	private String[] propSampleLabel;

	private String[] meanTSampleLabel;

	private HashMap<String, Procedure> pMap;

	/******************************************************************
	 * @param app
	 */
	public StatisticsCalculator(AppD app) {
		this.app = app;

		createGUI();
	}

	private void createGUI() {

		createGUIElements();
		createControlPanel();
		createInputPanel();
		createTestPanel();
		createEstimatePanel();
		createResultPanel();

		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
		outputPanel.add(panelInput);
		outputPanel.add(panelTest);
		outputPanel.add(panelEstimate);
		outputPanel.add(panelControl);
		
		JPanel main = new JPanel(new BorderLayout());

		main.setLayout(new BorderLayout());
		main.add(outputPanel, BorderLayout.SOUTH);
		main.add(resultScroller, BorderLayout.CENTER);
		
		
		//main.add(panelControl, BorderLayout.SOUTH);

		// JScrollPane mainScroller = new JScrollPane(main);

		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);

		setLabels();
		updateGUI();

	}

	private void updateGUI() {

		setSampleFieldLabels();
		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			fldSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			lblSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
			fldSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
		}

		lblSampleHeader1.setVisible((lblSampleStat2[0].getText() != null));
		lblSampleHeader2.setVisible((lblSampleStat2[0].getText() != null));

		setPanelLayout();
		this.revalidate();

	}

	private void createControlPanel() {


		panelControl = new JPanel(new BorderLayout());
		panelControl.add(LayoutUtil.flowPanel(cbProcedure), app.borderWest());
		panelControl.add(LayoutUtil.flowPanel(btnCalculate), app.borderEast());


	}

	private void createTestPanel() {

		panelTest = new JPanel();
		panelTest.setLayout(new BoxLayout(panelTest, BoxLayout.Y_AXIS));

		panelTest.add(LayoutUtil.flowPanel(lblNull, fldNullHyp));
		panelTest.add(LayoutUtil.flowPanel(lblTailType, btnLeft, btnRight,
				btnTwo));

	}

	private void createEstimatePanel() {

		panelEstimate = new JPanel();
		panelEstimate.setLayout(new BoxLayout(panelEstimate, BoxLayout.Y_AXIS));

		panelEstimate.add(LayoutUtil.flowPanel(lblConfLevel, fldConfLevel));

	}

	private void createInputPanel() {

		JPanel p1 = new JPanel();

		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		p1.add(LayoutUtil.flowPanel(0, 4, 0, lblSampleHeader1));
		for (int i = 0; i < lblSampleStat1.length; i++) {
			p1.add(LayoutUtil.flowPanelRight(4, 2, 0, lblSampleStat1[i],
					fldSampleStat1[i]));
		}

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		p2.add(LayoutUtil.flowPanel(0, 4, 0, lblSampleHeader2));
		for (int i = 0; i < lblSampleStat2.length; i++) {
			p2.add(LayoutUtil.flowPanelRight(4, 2, 0, lblSampleStat2[i],
					fldSampleStat2[i]));
		}

		panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		panelInput.add(p1);
		panelInput.add(p2);

	}

	private void createResultPanel() {

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(taResultLog);
		resultScroller = new JScrollPane(p);

	}

	private void createGUIElements() {

		lblSampleHeader1 = new JLabel();
		lblSampleHeader2 = new JLabel();

		taResultLog = new JTextArea();
		taResultLog.setMinimumSize(new Dimension(50, 50));

		cbProcedure = new JComboBox();
		cbProcedure.setRenderer(new ListSeparatorRenderer());

		cbProcedure.addActionListener(this);
		btnCalculate = new JButton();

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

		lblNull = new JLabel();
		lblHypParameter = new JLabel();
		lblTailType = new JLabel();

		fldNullHyp = new MyTextField(app);
		fldNullHyp.setColumns(fieldWidth);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addActionListener(this);
		fldNullHyp.addFocusListener(this);

		lblConfLevel = new JLabel();
		fldConfLevel = new MyTextField(app);
		fldConfLevel.setColumns(fieldWidth);
		fldConfLevel.addActionListener(this);
		fldConfLevel.addFocusListener(this);

		lblSigma = new JLabel();
		fldSigma = new MyTextField(app);
		fldSigma.setColumns(fieldWidth);
		fldSigma.addActionListener(this);
		fldSigma.addFocusListener(this);

		lblResultHeader = new JLabel();

		lblSampleStat1 = new JLabel[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new JLabel();
		}

		fldSampleStat1 = new MyTextField[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = new MyTextField(app);
			fldSampleStat1[i].setColumns(fieldWidth);
			fldSampleStat1[i].setText("" + 0);
			fldSampleStat1[i].addActionListener(this);
			fldSampleStat1[i].addFocusListener(this);
		}

		lblSampleStat2 = new JLabel[3];
		for (int i = 0; i < lblSampleStat2.length; i++) {
			lblSampleStat2[i] = new JLabel();
		}

		fldSampleStat2 = new MyTextField[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = new MyTextField(app);
			fldSampleStat2[i].setColumns(fieldWidth);
			fldSampleStat2[i].setText("" + 0);
			fldSampleStat2[i].addActionListener(this);
			fldSampleStat2[i].addFocusListener(this);
		}
	}

	public void setLabels() {

		panelInput.setBorder(BorderFactory.createTitledBorder("Input"));
		panelTest.setBorder(BorderFactory.createTitledBorder("Test"));
		panelEstimate.setBorder(BorderFactory.createTitledBorder("Estimate"));

		lblHypParameter.setText(app.getMenu("HypothesizedMean.short") + " = ");

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(app.getMenu("Result") + ": ");
		lblSigma.setText(app.getMenu("StandardDeviation.short") + " = ");
		btnCalculate.setText(app.getMenu("Calculate"));

		lblSampleHeader1.setText(app.getMenu("Sample1"));
		lblSampleHeader2.setText(app.getMenu("SampleProportion"));

		setProcedureComboLabels();

		setSampleFieldLabels();

	}

	private void setProcedureComboLabels() {

		pMap = new HashMap<String, Procedure>();
		pMap.put(app.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		pMap.put(app.getMenu("TMeanTest"), Procedure.TMEAN_TEST);
		pMap.put(app.getMenu("ZMeanInterval"), Procedure.ZMEAN_CI);
		pMap.put(app.getMenu("TMeanInterval"), Procedure.TMEAN_CI);
		pMap.put(app.getMenu("ZTestDifferenceOfMeans"), Procedure.ZMEAN2_TEST);
		pMap.put(app.getMenu("TTestDifferenceOfMeans"), Procedure.TMEAN2_TEST);
		pMap.put(app.getMenu("ZEstimateDifferenceOfMeans"), Procedure.ZMEAN2_CI);
		pMap.put(app.getMenu("TEstimateDifferenceOfMeans"), Procedure.TMEAN2_CI);
		pMap.put(app.getMenu("ZProportionTest"), Procedure.ZPROP_TEST);
		pMap.put(app.getMenu("ZProportionInterval"), Procedure.ZPROP_CI);
		pMap.put(app.getMenu("ZTestDifferenceOfProportions"),
				Procedure.ZPROP2_TEST);
		pMap.put(app.getMenu("ZEstimateDifferenceOfProportions"),
				Procedure.ZPROP_CI);
		pMap.put(app.getMenu("GooodnessOfFitTest"), Procedure.GOF_TEST);
		pMap.put(app.getMenu("ChiSquaredTest"), Procedure.CHISQ_TEST);

		cbProcedure.removeAllItems();
		cbProcedure.addItem(app.getMenu("ZMeanTest"));
		cbProcedure.addItem(app.getMenu("TMeanTest"));
		cbProcedure.addItem(app.getMenu("ZMeanInterval"));
		// cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);
		cbProcedure.addItem(app.getMenu("TMeanInterval"));
		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);

		cbProcedure.addItem(app.getMenu("ZTestDifferenceOfMeans"));
		cbProcedure.addItem(app.getMenu("TTestDifferenceOfMeans"));

		cbProcedure.addItem(app.getMenu("ZEstimateDifferenceOfMeans"));
		// cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);

		cbProcedure.addItem(app.getMenu("TEstimateDifferenceOfMeans"));
		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);

		cbProcedure.addItem(app.getMenu("ZProportionInterval"));
		cbProcedure.addItem(app.getMenu("ZProportionTest"));
		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);

		cbProcedure.addItem(app.getMenu("ZTestDifferenceOfProportions"));
		cbProcedure.addItem(app.getMenu("ZEstimateDifferenceOfProportions"));
		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);

		cbProcedure.addItem(app.getMenu("GooodnessOfFitTest"));
		cbProcedure.addItem(app.getMenu("ChiSquaredTest"));

		cbProcedure.setMaximumRowCount(17);

	}

	private void setSampleFieldLabels() {

		String sample1 = app.getMenu("Sample1");
		String sample2 = app.getMenu("Sample2");

		String sampleMean = app.getMenu("Mean");
		String sd = app.getMenu("SampleStandardDeviation.short");
		String sigma = app.getMenu("StandardDeviation.short");
		String successes = app.getMenu("Successes");
		String n = app.getMenu("N");

		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setText(null);
			lblSampleStat2[i].setText(null);
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(n);
			break;

		case TMEAN_TEST:
		case TMEAN_CI:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(sd);
			lblSampleStat1[2].setText(n);
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(n);
			lblSampleStat2[0].setText(sampleMean);
			lblSampleStat2[1].setText(n);
			break;

		case TMEAN2_TEST:
		case TMEAN2_CI:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(sd);
			lblSampleStat1[2].setText(n);
			lblSampleStat2[0].setText(sampleMean);
			lblSampleStat2[1].setText(sd);
			lblSampleStat2[2].setText(n);
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			lblSampleStat1[0].setText(successes);
			lblSampleStat1[2].setText(n);
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleStat1[0].setText(successes);
			lblSampleStat1[2].setText(n);
			lblSampleStat2[0].setText(successes);
			lblSampleStat2[2].setText(n);
			break;

		}
	}

	private void setResultTable() {

		ArrayList<String> resultLabelTest = new ArrayList<String>();

		switch (selectedProcedure) {

		case ZMEAN_TEST:
			resultLabelTest.add(app.getMenu("PValue"));
			resultLabelTest.add(app.getMenu("ZStatistic"));
			break;

		case TMEAN_TEST:
			resultLabelTest.add(app.getMenu("PValue"));
			resultLabelTest.add(app.getMenu("TStatistic"));
			resultLabelTest.add(app.getMenu(""));
			resultLabelTest.add(app.getMenu("DegreesOfFreedom.short"));
			resultLabelTest.add(app.getMenu("StandardError.short"));
			break;

		}

		String[] rowNames = new String[resultLabelTest.size()];
		resultLabelTest.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 1, null);

	}

	private void setResultTable2() {

		ArrayList<String> resultLabelEstimate = new ArrayList<String>();

		switch (selectedProcedure) {

		case ZMEAN_TEST:
			resultLabelEstimate.add(app.getMenu("Interval"));
			resultLabelEstimate.add(app.getMenu("LowerLimit"));
			resultLabelEstimate.add(app.getMenu("UpperLimit"));
			break;

		case TMEAN_TEST:
			resultLabelEstimate.add(app.getMenu("Interval"));
			resultLabelEstimate.add(app.getMenu("LowerLimit"));
			resultLabelEstimate.add(app.getMenu("UpperLimit"));
			resultLabelEstimate.add(app.getMenu(""));
			resultLabelEstimate.add(app.getMenu("DegreesOfFreedom.short"));
			resultLabelEstimate.add(app.getMenu("StandardError.short"));
			break;
		}

		String[] rowNames = new String[resultLabelEstimate.size()];
		resultLabelEstimate.toArray(rowNames);
		resultTable.setStatTable(rowNames.length, rowNames, 1, null);

	}

	private void setPanelLayout() {

		panelEstimate.setVisible(false);
		panelTest.setVisible(false);

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN2_TEST:
		case TMEAN_TEST:
		case TMEAN2_TEST:
		case ZPROP_TEST:
		case ZPROP2_TEST:
			panelTest.setVisible(true);
			break;

		case ZMEAN_CI:
		case ZMEAN2_CI:
		case TMEAN_CI:
		case TMEAN2_CI:
		case ZPROP_CI:
		case ZPROP2_CI:
			panelEstimate.setVisible(true);
			break;
		}

	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source == cbProcedure) {
			selectedProcedure = pMap.get(cbProcedure.getSelectedItem());
			updateGUI();
		}

	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

}
