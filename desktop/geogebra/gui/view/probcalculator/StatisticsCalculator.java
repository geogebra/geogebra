package geogebra.gui.view.probcalculator;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.gui.util.ListSeparatorRenderer;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCalculator extends JPanel implements ActionListener,
		FocusListener, SetLabels {

	private static final long serialVersionUID = 1L;

	// =========================================
	// support classes
	// =========================================

	private AppD app;
	private Construction cons;
	private Kernel kernel;
	private StatisticsCollection sc;
	private StatisticsCalculatorProcessor statProcessor;
	private StatisticsCalculatorHTML statHTML;

	// =========================================
	// GUI components
	// =========================================

	// text fields
	private MyTextField[] fldSampleStat1, fldSampleStat2;
	private MyTextField fldNullHyp, fldConfLevel, fldSigma;
	private int fieldWidth = 6;

	// labels
	private JLabel[] lblSampleStat1, lblSampleStat2;
	private JLabel lblResult, lblHypParameter, lblTailType, lblNull,
			lblConfLevel, lblSigma, lblSampleHeader, lblSampleHeader1,
			lblSampleHeader2;

	// buttons and combo boxes
	private JRadioButton btnLeft, btnRight, btnTwo;
	private JButton btnCalculate;
	private JComboBox cbProcedure;
	private JCheckBox ckPooled;

	// panels
	private JPanel panelBasicProcedures, panelControl, panelSample1,
			panelSample2, panelTestAndCI;
	private ChiSquarePanel panelChiSquare;
	private JEditorPane resultPane;
	private JScrollPane scroller;

	// =========================================
	// Procedures
	// =========================================

	/***/
	public enum Procedure {
		ZMEAN_TEST, ZMEAN2_TEST, TMEAN_TEST, TMEAN2_TEST, ZPROP_TEST, ZPROP2_TEST, ZMEAN_CI, ZMEAN2_CI, TMEAN_CI, TMEAN2_CI, ZPROP_CI, ZPROP2_CI, GOF_TEST, CHISQ_TEST
	}

	private Procedure selectedProcedure;

	private HashMap<String, Procedure> mapNameToProcedure;
	private HashMap<Procedure, String> mapProcedureToName;

	// =========================================
	// Misc
	// =========================================

	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNodeConstants.strNOT_EQUAL;

	private StringBuilder bodyText;

	private String strMean, strSD, strSigma, strSuccesses, strN, strPooled;

	private double[] s1, s2;

	/******************************************************************
	 * 
	 * Construct StatisticsCalculator
	 * 
	 * @param app
	 */
	public StatisticsCalculator(AppD app) {
		this.app = app;
		cons = app.getKernel().getConstruction();
		kernel = cons.getKernel();
		sc = new StatisticsCollection();
		statProcessor = new StatisticsCalculatorProcessor(app, this, sc);
		statHTML = new StatisticsCalculatorHTML(app, this, sc);

		selectedProcedure = Procedure.ZMEAN_TEST;
		createGUI();
	}

	// =========================================
	// Getters/Setters
	// =========================================

	public Procedure getSelectedProcedure() {
		return selectedProcedure;
	}

	public HashMap<Procedure, String> getMapProcedureToName() {
		return mapProcedureToName;
	}

	public StatisticsCalculatorProcessor getStatProcessor() {
		return statProcessor;
	}

	public StatisticsCollection getStatististicsCollection() {
		return sc;
	}

	// =========================================
	// GUI
	// =========================================

	private void createGUI() {

		createGUIElements();
		createControlPanel();
		setInputPanelLayout();
		panelChiSquare = new ChiSquarePanel(app, this);

		// prepare result panel
		resultPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		JPanel resultPanel = new JPanel(new BorderLayout());
		resultPanel.add(lblResult, BorderLayout.NORTH);
		resultPanel.add(resultPane, BorderLayout.CENTER);

		// procedure panel (procedure input fields + result panel)
		JPanel procedurePanel = new JPanel();
		procedurePanel
				.setLayout(new BoxLayout(procedurePanel, BoxLayout.Y_AXIS));
		procedurePanel.add(panelBasicProcedures);
		procedurePanel.add(panelChiSquare);
		procedurePanel.add(Box.createVerticalStrut(20));
		procedurePanel.add(resultPanel);
		procedurePanel.setAlignmentY(TOP_ALIGNMENT);

		// wrapper for procedure panel
		JPanel procedureWrapper = new JPanel(new BorderLayout());
		procedureWrapper.add(procedurePanel, BorderLayout.NORTH);
		procedureWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 10,
				20));
		scroller = new JScrollPane(procedureWrapper);
		scroller.getVerticalScrollBar().setUnitIncrement(30);

		// main content panel
		JPanel main = new JPanel(new BorderLayout());
		main.add(scroller, BorderLayout.CENTER);
		main.add(panelControl, BorderLayout.NORTH);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);

		setLabels();
		updateGUI();

	}

	private void createControlPanel() {

		panelControl = new JPanel(new BorderLayout());
		panelControl.add(LayoutUtil.flowPanel(cbProcedure), app.borderWest());
		// panelControl.add(LayoutUtil.flowPanel(btnCalculate),
		// BorderLayout.CENTER);
		panelControl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

	}

	private void setInputPanelLayout() {

		// ---- prepare panels

		if (panelBasicProcedures == null) {
			panelBasicProcedures = new JPanel();
			panelBasicProcedures.setLayout(new GridBagLayout());
			panelBasicProcedures.setAlignmentY(TOP_ALIGNMENT);
		}

		if (panelSample1 == null) {
			panelSample1 = new JPanel();
			panelSample1
					.setLayout(new BoxLayout(panelSample1, BoxLayout.Y_AXIS));
			panelSample1.setAlignmentY(TOP_ALIGNMENT);
		}
		if (panelSample2 == null) {
			panelSample2 = new JPanel();
			panelSample2
					.setLayout(new BoxLayout(panelSample2, BoxLayout.Y_AXIS));
			panelSample2.setAlignmentY(TOP_ALIGNMENT);
		}
		if (panelTestAndCI == null) {
			panelTestAndCI = new JPanel();
			panelTestAndCI.setLayout(new BoxLayout(panelTestAndCI,
					BoxLayout.Y_AXIS));
			panelTestAndCI.setAlignmentY(TOP_ALIGNMENT);
		}

		panelBasicProcedures.removeAll();
		panelSample1.removeAll();
		panelSample2.removeAll();
		panelTestAndCI.removeAll();

		// ---- add components

		panelSample1.add(LayoutUtil.flowPanelRight(4, 2, 0, lblSampleHeader1));
		for (int i = 0; i < lblSampleStat1.length; i++) {
			panelSample1.add(LayoutUtil.flowPanelRight(4, 2, 0,
					lblSampleStat1[i], fldSampleStat1[i]));
		}

		panelSample2.add(LayoutUtil.flowPanelRight(4, 2, 0, new JLabel(" "),
				lblSampleHeader2));
		for (int i = 0; i < lblSampleStat2.length; i++) {
			panelSample2.add(LayoutUtil.flowPanelRight(4, 2, 0,
					lblSampleStat2[i], fldSampleStat2[i]));
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN2_TEST:
		case TMEAN_TEST:
		case TMEAN2_TEST:
		case ZPROP_TEST:
		case ZPROP2_TEST:

			panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, lblNull,
					Box.createHorizontalStrut(5), lblHypParameter, fldNullHyp));
			panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, lblTailType,
					btnLeft, btnRight, btnTwo));
			panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, ckPooled));
			break;

		case ZMEAN_CI:
		case ZMEAN2_CI:
		case TMEAN_CI:
		case TMEAN2_CI:
		case ZPROP_CI:
		case ZPROP2_CI:

			panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, lblConfLevel,
					fldConfLevel));
			break;
		}

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 0, 0, 0);
		panelBasicProcedures.add(panelSample1, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 30, 0, 0);
		panelBasicProcedures.add(panelSample2, c);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 2;
		c.insets = new Insets(10, 0, 0, 0);
		c.anchor = GridBagConstraints.FIRST_LINE_START;

		panelBasicProcedures.add(panelTestAndCI, c);

	}

	private void createGUIElements() {

		// text pane to hold result HTML output
		resultPane = new JEditorPane();
		HTMLEditorKit kit = new HTMLEditorKit();
		resultPane.setEditorKit(kit);
		setStyleSheets(kit);
		resultPane.setEditable(false);

		s1 = new double[3];
		s2 = new double[3];

		lblResult = new JLabel();

		lblSampleHeader1 = new JLabel();
		lblSampleHeader2 = new JLabel();

		bodyText = new StringBuilder();

		ckPooled = new JCheckBox();
		ckPooled.setSelected(false);
		ckPooled.addActionListener(this);

		cbProcedure = new JComboBox();
		cbProcedure.setRenderer(new ListSeparatorRenderer());
		cbProcedure.addActionListener(this);

		btnCalculate = new JButton();
		btnCalculate.addActionListener(this);

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

		lblSampleStat1 = new JLabel[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new JLabel();
		}

		fldSampleStat1 = new MyTextField[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = new MyTextField(app);
			fldSampleStat1[i].setColumns(fieldWidth);
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
			fldSampleStat2[i].addActionListener(this);
			fldSampleStat2[i].addFocusListener(this);
		}
	}

	public void setLabels() {

		lblResult.setText(app.getMenu("Result"));
		lblNull.setText(app.getMenu("NullHypothesis"));
		lblTailType.setText(app.getMenu("AlternativeHypothesis"));
		lblConfLevel.setText(app.getMenu("ConfidenceLevel"));
		lblSigma.setText(app.getMenu("StandardDeviation.short"));
		btnCalculate.setText(app.getMenu("Calculate"));

		switch (selectedProcedure) {

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_CI:
		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleHeader1.setText(app.getMenu("Sample1"));
			break;

		default:
			lblSampleHeader1.setText(app.getMenu("Sample"));
		}

		lblSampleHeader2.setText(app.getMenu("Sample2"));

		ckPooled.setText(app.getMenu("Pooled"));

		setHypParameterLabel();
		setLabelStrings();
		setProcedureComboLabels();
		setSampleFieldLabels();

		panelChiSquare.setLabels();

		// reset the text in the result panel
		updateResult();

	}

	private void setHypParameterLabel() {
		switch (selectedProcedure) {

		case ZMEAN_TEST:
		case TMEAN_TEST:
			lblHypParameter.setText(app.getMenu("HypothesizedMean.short") + " = ");
			break;

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
			lblHypParameter.setText(app.getMenu("DifferenceOfMeans.short")+ " = ");
			break;

		case ZPROP_TEST:
			lblHypParameter
					.setText(app.getMenu("HypothesizedProportion.short")+ " = ");
			break;

		case ZPROP2_TEST:
			lblHypParameter.setText(app
					.getMenu("DifferenceOfProportions.short")+ " = ");
			break;

		default:
			lblHypParameter.setText(app.getMenu(""));
		}
	}

	private void setProcedureComboLabels() {

		if (mapNameToProcedure == null) {
			mapNameToProcedure = new HashMap<String, Procedure>();
		}
		if (mapProcedureToName == null) {
			mapProcedureToName = new HashMap<Procedure, String>();
		}

		mapNameToProcedure.clear();
		mapProcedureToName.clear();

		mapNameToProcedure.put(app.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(app.getMenu("ZMeanTest"), Procedure.ZMEAN_TEST);
		mapNameToProcedure.put(app.getMenu("TMeanTest"), Procedure.TMEAN_TEST);
		mapNameToProcedure
				.put(app.getMenu("ZMeanInterval"), Procedure.ZMEAN_CI);
		mapNameToProcedure
				.put(app.getMenu("TMeanInterval"), Procedure.TMEAN_CI);
		mapNameToProcedure.put(app.getMenu("ZTestDifferenceOfMeans"),
				Procedure.ZMEAN2_TEST);
		mapNameToProcedure.put(app.getMenu("TTestDifferenceOfMeans"),
				Procedure.TMEAN2_TEST);
		mapNameToProcedure.put(app.getMenu("ZEstimateDifferenceOfMeans"),
				Procedure.ZMEAN2_CI);
		mapNameToProcedure.put(app.getMenu("TEstimateDifferenceOfMeans"),
				Procedure.TMEAN2_CI);
		mapNameToProcedure.put(app.getMenu("ZProportionTest"),
				Procedure.ZPROP_TEST);
		mapNameToProcedure.put(app.getMenu("ZProportionInterval"),
				Procedure.ZPROP_CI);
		mapNameToProcedure.put(app.getMenu("ZTestDifferenceOfProportions"),
				Procedure.ZPROP2_TEST);
		mapNameToProcedure.put(app.getMenu("ZEstimateDifferenceOfProportions"),
				Procedure.ZPROP2_CI);
		mapNameToProcedure.put(app.getMenu("GoodnessOfFitTest"),
				Procedure.GOF_TEST);
		mapNameToProcedure.put(app.getMenu("ChiSquaredTest"),
				Procedure.CHISQ_TEST);

		for (String s : mapNameToProcedure.keySet()) {
			this.mapProcedureToName.put(mapNameToProcedure.get(s), s);
		}

		cbProcedure.removeAllItems();
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_TEST));

		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_CI));

		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);
		cbProcedure.addItem(mapProcedureToName.get(Procedure.GOF_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.CHISQ_TEST));

		cbProcedure.setMaximumRowCount(cbProcedure.getItemCount());

		// TODO for testing only, remove later
		// cbProcedure.setSelectedItem(mapProcedureToName
		// .get(Procedure.CHISQ_TEST));

	}

	private void setLabelStrings() {

		strMean = app.getMenu("Mean");
		strSD = app.getMenu("SampleStandardDeviation.short");
		strSigma = app.getMenu("StandardDeviation.short");
		strSuccesses = app.getMenu("Successes");
		strN = app.getMenu("N");
		strPooled = app.getMenu("Pooled");
	}

	private void setSampleFieldLabels() {

		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setText(null);
			lblSampleStat2[i].setText(null);
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSigma);
			lblSampleStat1[2].setText(strN);
			break;

		case TMEAN_TEST:
		case TMEAN_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSD);
			lblSampleStat1[2].setText(strN);
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSigma);
			lblSampleStat1[2].setText(strN);
			lblSampleStat2[0].setText(strMean);
			lblSampleStat2[1].setText(strSigma);
			lblSampleStat2[2].setText(strN);
			break;

		case TMEAN2_TEST:
		case TMEAN2_CI:
			lblSampleStat1[0].setText(strMean);
			lblSampleStat1[1].setText(strSD);
			lblSampleStat1[2].setText(strN);
			lblSampleStat2[0].setText(strMean);
			lblSampleStat2[1].setText(strSD);
			lblSampleStat2[2].setText(strN);
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			lblSampleStat1[0].setText(strSuccesses);
			lblSampleStat1[1].setText(strN);
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleStat1[0].setText(strSuccesses);
			lblSampleStat1[1].setText(strN);
			lblSampleStat2[0].setText(strSuccesses);
			lblSampleStat2[1].setText(strN);
			break;

		}
	}

	private void setSampleFieldText() {

		for (int i = 0; i < 3; i++) {
			fldSampleStat1[i].removeActionListener(this);
			fldSampleStat2[i].removeActionListener(this);
			fldSampleStat1[i].setText(null);
			fldSampleStat2[i].setText(null);
		}

		switch (selectedProcedure) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
		case TMEAN_TEST:
		case TMEAN_CI:
			fldSampleStat1[0].setText(format(sc.mean));
			fldSampleStat1[1].setText(format(sc.sd));
			fldSampleStat1[2].setText(format(sc.n));
			break;

		case ZMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_TEST:
		case TMEAN2_CI:
			fldSampleStat1[0].setText(format(sc.mean));
			fldSampleStat1[1].setText(format(sc.sd));
			fldSampleStat1[2].setText(format(sc.n));
			fldSampleStat2[0].setText(format(sc.mean2));
			fldSampleStat2[1].setText(format(sc.sd2));
			fldSampleStat2[2].setText(format(sc.n2));
			break;

		case ZPROP_TEST:
		case ZPROP_CI:
			fldSampleStat1[0].setText(format(sc.count));
			fldSampleStat1[1].setText(format(sc.n));
			break;

		case ZPROP2_TEST:
		case ZPROP2_CI:
			fldSampleStat1[0].setText(format(sc.count));
			fldSampleStat1[1].setText(format(sc.n));
			fldSampleStat2[0].setText(format(sc.count2));
			fldSampleStat2[1].setText(format(sc.n2));
			break;
		}

		for (int i = 0; i < 3; i++) {
			fldSampleStat1[i].addActionListener(this);
			fldSampleStat2[i].addActionListener(this);
		}

		fldConfLevel.setText(format(sc.level));
		fldNullHyp.setText(format(sc.nullHyp));

	}

	private void updateGUI() {

		setHypParameterLabel();
		setSampleFieldLabels();
		setSampleFieldText();
		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			fldSampleStat1[i].setVisible(lblSampleStat1[i].getText() != null);
			lblSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
			fldSampleStat2[i].setVisible(lblSampleStat2[i].getText() != null);
		}

		lblSampleHeader2.setVisible((lblSampleStat2[0].getText() != null));

		ckPooled.setVisible(selectedProcedure == Procedure.TMEAN2_TEST
				|| selectedProcedure == Procedure.TMEAN2_CI);

		setPanelLayout();
		revalidate();

	}

	private void setPanelLayout() {

		panelBasicProcedures.setVisible(false);
		panelChiSquare.setVisible(false);

		switch (selectedProcedure) {

		case CHISQ_TEST:
		case GOF_TEST:
			panelChiSquare.setVisible(true);
			panelChiSquare.updateGUI();
			break;

		default:
			setInputPanelLayout();
			panelBasicProcedures.setVisible(true);
		}

	}

	public void updateResult() {

		updateStatisticCollection();
		statProcessor.doCalculate();

		bodyText = new StringBuilder();
		bodyText.append(statHTML.getStatString());
		updateResultText();

		// prevent auto scrolling
		resultPane.setCaretPosition(0);

	}

	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e);
	}

	public void doActionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		if (source == cbProcedure && cbProcedure.getSelectedIndex() >= 0) {
			selectedProcedure = mapNameToProcedure.get(cbProcedure
					.getSelectedItem());
			updateGUI();
			updateResult();
			//setLabels();

			// reset the scrollpane to the top
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scroller.getVerticalScrollBar().setValue(0);
				}
			});

		}

		if (source == btnLeft || source == btnRight || source == btnTwo) {
			updateResult();
		}

		if (source == ckPooled) {
			sc.pooled = ckPooled.isSelected();
			updateResult();
		}

		if (source == btnCalculate) {
			updateResult();
		}

	}

	public void doTextFieldActionPerformed(JTextField source) {

		if (source.getText().equals(ListSeparatorRenderer.SEPARATOR)) {
			return;
		}

		updateResult();
	}

	private double parseNumberText(String s) {

		if (s == null || s.length() == 0) {
			return Double.NaN;
		}

		try {
			String inputText = s.trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = cons.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			return nv.getDouble();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return Double.NaN;
	}

	private void updateStatisticCollection() {
		try {

			sc.level = parseNumberText(fldConfLevel.getText());
			sc.sd = parseNumberText(fldSigma.getText());
			sc.nullHyp = parseNumberText(fldNullHyp.getText());

			if (btnLeft.isSelected()) {
				sc.tail = tail_left;
			} else if (btnRight.isSelected()) {
				sc.tail = tail_right;
			} else {
				sc.tail = tail_two;
			}

			for (int i = 0; i < s1.length; i++) {
				s1[i] = (parseNumberText(fldSampleStat1[i].getText()));
			}
			for (int i = 0; i < s2.length; i++) {
				s2[i] = (parseNumberText(fldSampleStat2[i].getText()));
			}

			switch (selectedProcedure) {

			case ZMEAN_TEST:
			case ZMEAN_CI:
			case TMEAN_TEST:
			case TMEAN_CI:
				sc.mean = s1[0];
				sc.sd = s1[1];
				sc.n = s1[2];
				break;

			case ZMEAN2_TEST:
			case ZMEAN2_CI:
			case TMEAN2_TEST:
			case TMEAN2_CI:
				sc.mean = s1[0];
				sc.sd = s1[1];
				sc.n = s1[2];
				sc.mean2 = s2[0];
				sc.sd2 = s2[1];
				sc.n2 = s2[2];

				// force the null hypothesis to zero
				// TODO: allow non-zero values
				sc.nullHyp = 0;
				break;

			case ZPROP_TEST:
			case ZPROP_CI:
				sc.count = s1[0];
				sc.n = s1[1];
				break;

			case ZPROP2_TEST:
			case ZPROP2_CI:
				sc.count = s1[0];
				sc.n = s1[1];
				sc.count2 = s2[0];
				sc.n2 = s2[1];

				// force the null hypothesis to zero
				// TODO: allow non-zero values
				sc.nullHyp = 0;
				break;
			}

			sc.validate();
			setSampleFieldText();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Formats a number string using local format settings.
	 * 
	 * @param x
	 * @return
	 */
	public String format(double x) {
		StringTemplate highPrecision;

		if (kernel.useSignificantFigures) {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					kernel.getPrintFigures(), false);
		} else {
			// override the default decimal place if < 4
			int d = kernel.getPrintDecimals() < 4 ? 4 : cons.getKernel()
					.getPrintDecimals();
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					d, false);
		}
		// get the formatted string
		String result = kernel.format(x, highPrecision);

		return result;
	}

	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof MyTextField) {
			((MyTextField) e.getSource()).selectAll();
		}

	}

	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof MyTextField)
			doTextFieldActionPerformed((MyTextField) e.getSource());

	}

	public void updateFonts(Font font) {
		setStyleSheetFontSize((HTMLEditorKit) resultPane.getEditorKit(), font);
		this.setFont(font);
		this.updateResultText();
	}

	private static void setStyleSheetFontSize(HTMLEditorKit kit, Font font) {

		StyleSheet styleSheet = kit.getStyleSheet();
		String size = "" + font.getSize();
		styleSheet.addRule("body {font-size : " + size + "pt }");

	}

	private static void setStyleSheets(HTMLEditorKit kit) {
		// add some styles to the html
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet
				.addRule("body {color:#00008B; font : 9pt verdana; margin: 4px;  }");

		String padding = "padding-top:2px; padding-bottom:2px;padding-left:5px;padding-right:5px;";
		styleSheet
				.addRule("td {text-align: center; border-top-width: 1px; border-bottom-width: 1px;border-left-width: 1px;border-right-width: 1px;border-style:solid; border-color:#00008B;"
						+ padding + "}");

	}

	private void updateResultText() {

		String htmlString = "<html><body>\n" + bodyText.toString()
				+ "</body>\n";
		resultPane.setText(htmlString);

	}

}
