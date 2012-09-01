package geogebra.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.gui.view.spreadsheet.statdialog.StatTable;
import geogebra.gui.view.spreadsheet.statdialog.StatisticsPanel;
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

	private JPanel inputPanel, testPanel, estimatePanel, controlPanel;

	JScrollPane resultScroller;

	private enum Procedure {
		ZMEAN, ZMEAN2, TMEAN, TMEAN2, ZPROP, ZPROP2
	}

	private Procedure selectedProcedure = Procedure.ZMEAN;

	private StatTable resultTable;

	private JComboBox cbProcedure;

	private String[] meanZSampleLabel;

	private String[] propSampleLabel;

	private String[] meanTSampleLabel;

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
		outputPanel.add(inputPanel);
		outputPanel.add(testPanel);
		outputPanel.add(estimatePanel);

		JPanel main = new JPanel(new BorderLayout());

		main.setLayout(new BorderLayout());
		main.add(outputPanel, BorderLayout.NORTH);
		main.add(resultScroller, BorderLayout.CENTER);
		main.add(controlPanel, BorderLayout.SOUTH);

		// JScrollPane mainScroller = new JScrollPane(main);

		this.setLayout(new BorderLayout());
		this.add(main, BorderLayout.CENTER);

		setLabels();
		updateGUI();

	}

	private void updateGUI() {
		// TODO Auto-generated method stub

	}

	private void createControlPanel() {

		controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(LayoutUtil.flowPanel(cbProcedure), BorderLayout.WEST);
		controlPanel
				.add(LayoutUtil.flowPanel(btnCalculate), BorderLayout.EAST);

	}

	private void createTestPanel() {

		testPanel = new JPanel();
		testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));

		testPanel.add(LayoutUtil.flowPanel(lblNull, fldNullHyp));
		testPanel.add(LayoutUtil.flowPanel(lblTailType, btnLeft, btnRight,
				btnTwo));

	}

	private void createEstimatePanel() {

		estimatePanel = new JPanel();
		estimatePanel.setLayout(new BoxLayout(estimatePanel, BoxLayout.Y_AXIS));

		estimatePanel.add(LayoutUtil.flowPanel(lblConfLevel, fldConfLevel));

	}

	private void createInputPanel() {

		JPanel p1 = new JPanel();

		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		p1.add(LayoutUtil.flowPanelRight(0, 0, 0, lblSampleHeader1));
		for (int i = 0; i < lblSampleStat1.length; i++) {
			p1.add(LayoutUtil.flowPanelRight(4, 0, 0, lblSampleStat1[i],
					fldSampleStat1[i]));
		}

		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

		inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		inputPanel.add(p1);

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

		inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
		testPanel.setBorder(BorderFactory.createTitledBorder("Test"));
		estimatePanel.setBorder(BorderFactory.createTitledBorder("Estimate"));

		lblHypParameter.setText(app.getMenu("HypothesizedMean.short") + " = ");

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(app.getMenu("Result") + ": ");
		lblSigma.setText(app.getMenu("StandardDeviation.short") + " = ");
		btnCalculate.setText(app.getMenu("Calculate"));

		lblSampleHeader1.setText(app.getMenu("Sample"));
		lblSampleHeader2.setText(app.getMenu("Sample"));
		
		setProcedureComboLabels();
		
		setSampleFieldLabels();

	}

	private void setProcedureComboLabels() {
		cbProcedure.removeAllItems();
		cbProcedure.addItem(app.getMenu("ZMeanProcedures"));
		cbProcedure.addItem(app.getMenu("ZMean2Procedures"));
		cbProcedure.addItem(app.getMenu("TMeanProcedures"));
		cbProcedure.addItem(app.getMenu("TMean2Procedures"));

		cbProcedure.addItem(app.getMenu("ZPropProcedures"));
		cbProcedure.addItem(app.getMenu("ZProp2Procedures"));

	}

	private void setSampleFieldLabels() {

		String sample1 = app.getMenu("Sample1");
		String sample2 = app.getMenu("Sample2");

		String sampleMean = app.getMenu("Mean");
		String sd = app.getMenu("SampleStandardDeviation.short");
		String sigma = app.getMenu("StandardDeviation.short");
		String successes = app.getMenu("Successes");
		String n = app.getMenu("N");

		switch (selectedProcedure) {
		case ZMEAN:
		case ZMEAN2:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(sigma);
			lblSampleStat1[2].setText(n);
			break;
		case TMEAN:
		case TMEAN2:
			lblSampleStat1[0].setText(sampleMean);
			lblSampleStat1[1].setText(sd);
			lblSampleStat1[2].setText(n);
			break;
		case ZPROP:
		case ZPROP2:
			lblSampleStat1[0].setText(successes);
			lblSampleStat1[2].setText(n);
			break;
		}
	}

	private void setResultTable() {

		ArrayList<String> resultLabelTest = new ArrayList<String>();

		switch (selectedProcedure) {

		case ZMEAN:
			resultLabelTest.add(app.getMenu("PValue"));
			resultLabelTest.add(app.getMenu("ZStatistic"));
			break;

		case TMEAN:
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

		case ZMEAN:
			resultLabelEstimate.add(app.getMenu("Interval"));
			resultLabelEstimate.add(app.getMenu("LowerLimit"));
			resultLabelEstimate.add(app.getMenu("UpperLimit"));
			break;

		case TMEAN:
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

	public void actionPerformed(ActionEvent e) {

	}

	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}

}
