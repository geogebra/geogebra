package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.util.TextObject;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.gui.util.LayoutUtil;
import org.geogebra.desktop.gui.util.ListSeparatorRenderer;
import org.geogebra.desktop.main.AppD;

/**
 * @author G. Sturr
 * 
 */
public class StatisticsCalculatorD extends StatisticsCalculator
		implements ActionListener, FocusListener, SetLabels {


	// =========================================
	// support classes
	// =========================================

	// =========================================
	// GUI components
	// =========================================

	// labels
	private JLabel[] lblSampleStat1, lblSampleStat2;
	private JLabel lblResult, lblHypParameter, lblTailType, lblNull,
			lblConfLevel, lblSigma, lblSampleHeader1,
			lblSampleHeader2;

	// buttons and combo boxes
	private JRadioButton btnLeft, btnRight, btnTwo;
	private JButton btnCalculate;
	private JComboBox<String> cbProcedure;
	private JCheckBox ckPooled;

	// panels
	private JPanel panelBasicProcedures, panelControl, panelSample1,
			panelSample2, panelTestAndCI;
	private ChiSquarePanelD panelChiSquare;
	private JEditorPane resultPane;
	private JScrollPane scroller;

	private JPanel wrappedPanel;

	/******************************************************************
	 * 
	 * Construct StatisticsCalculator
	 * 
	 * @param app
	 */
	public StatisticsCalculatorD(AppD app) {
		super(app);
		createGUI();
	}

	// =========================================
	// GUI
	// =========================================

	private void createGUI() {

		this.wrappedPanel = new JPanel();

		createGUIElements();
		createControlPanel();
		setInputPanelLayout();
		panelChiSquare = new ChiSquarePanelD(loc, this);

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
		procedurePanel.add(panelChiSquare.getWrappedPanel());
		procedurePanel.add(Box.createVerticalStrut(20));
		procedurePanel.add(resultPanel);
		procedurePanel.setAlignmentY(Component.TOP_ALIGNMENT);

		// wrapper for procedure panel
		JPanel procedureWrapper = new JPanel(new BorderLayout());
		procedureWrapper.add(procedurePanel, BorderLayout.NORTH);
		procedureWrapper
				.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		scroller = new JScrollPane(procedureWrapper);
		scroller.getVerticalScrollBar().setUnitIncrement(30);

		// main content panel
		JPanel main = new JPanel(new BorderLayout());
		main.add(scroller, BorderLayout.CENTER);
		main.add(panelControl, BorderLayout.NORTH);
		wrappedPanel.setLayout(new BorderLayout());
		wrappedPanel.add(main, BorderLayout.CENTER);

		setLabels();
		updateGUI();
		panelChiSquare.updateVisibility();
		panelChiSquare.getWrappedPanel().revalidate();
	}

	private void createControlPanel() {

		panelControl = new JPanel(new BorderLayout());
		panelControl.add(LayoutUtil.flowPanel(cbProcedure),
				loc.borderWest());
		// panelControl.add(LayoutUtil.flowPanel(btnCalculate),
		// BorderLayout.CENTER);
		panelControl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

	}

	private void setInputPanelLayout() {

		// ---- prepare panels

		if (panelBasicProcedures == null) {
			panelBasicProcedures = new JPanel();
			panelBasicProcedures.setLayout(new GridBagLayout());
			panelBasicProcedures.setAlignmentY(Component.TOP_ALIGNMENT);
		}

		if (panelSample1 == null) {
			panelSample1 = new JPanel();
			panelSample1
					.setLayout(new BoxLayout(panelSample1, BoxLayout.Y_AXIS));
			panelSample1.setAlignmentY(Component.TOP_ALIGNMENT);
		}
		if (panelSample2 == null) {
			panelSample2 = new JPanel();
			panelSample2
					.setLayout(new BoxLayout(panelSample2, BoxLayout.Y_AXIS));
			panelSample2.setAlignmentY(Component.TOP_ALIGNMENT);
		}
		if (panelTestAndCI == null) {
			panelTestAndCI = new JPanel();
			panelTestAndCI
					.setLayout(new BoxLayout(panelTestAndCI, BoxLayout.Y_AXIS));
			panelTestAndCI.setAlignmentY(Component.TOP_ALIGNMENT);
		}

		panelBasicProcedures.removeAll();
		panelSample1.removeAll();
		panelSample2.removeAll();
		panelTestAndCI.removeAll();

		// ---- add components

		panelSample1.add(LayoutUtil.flowPanelRight(4, 2, 0, lblSampleHeader1));
		for (int i = 0; i < lblSampleStat1.length; i++) {
			panelSample1.add(LayoutUtil.flowPanelRight(4, 2, 0,
					lblSampleStat1[i], (MyTextFieldD) fldSampleStat1[i]));
		}

		panelSample2.add(LayoutUtil.flowPanelRight(4, 2, 0, new JLabel(" "),
				lblSampleHeader2));
		for (int i = 0; i < lblSampleStat2.length; i++) {
			panelSample2.add(LayoutUtil.flowPanelRight(4, 2, 0,
					lblSampleStat2[i], (MyTextFieldD) fldSampleStat2[i]));
		}

		switch (sc.getSelectedProcedure()) {
		default:
			// do nothing
			break;
		case ZMEAN_TEST:
		case ZMEAN2_TEST:
		case TMEAN_TEST:
		case TMEAN2_TEST:
		case ZPROP_TEST:
		case ZPROP2_TEST:

			if (app.getLocalization().isRightToLeftReadingOrder()) {
				// eg 1.1 = mu
				panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, lblNull,
						Box.createHorizontalStrut(5), (MyTextFieldD) fldNullHyp,
						lblHypParameter));
			} else {
				// eg mu = 1.1
				panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, lblNull,
						Box.createHorizontalStrut(5), lblHypParameter,
						(MyTextFieldD) fldNullHyp));
			}
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

			panelTestAndCI.add(
					LayoutUtil.flowPanel(4, 2, 0, lblConfLevel,
							(MyTextFieldD) fldConfLevel));
			panelTestAndCI.add(LayoutUtil.flowPanel(4, 2, 0, ckPooled));
			break;
		}

		if (forceZeroHypothesis()) {
			fldNullHyp.setText("0");
			fldNullHyp.setEditable(false);
		} else {
			fldNullHyp.setEditable(true);
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

		cbProcedure = new JComboBox<>();
		cbProcedure.setSelectedItem(sc.getSelectedProcedure().toString());
		cbProcedure.setRenderer(new ListSeparatorRenderer());
		cbProcedure.addActionListener(this);

		btnCalculate = new JButton();
		btnCalculate.addActionListener(this);

		btnLeft = new JRadioButton(StatisticsCollection.tail_left);
		btnRight = new JRadioButton(StatisticsCollection.tail_right);
		btnTwo = new JRadioButton(StatisticsCollection.tail_two);
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

		fldNullHyp = new MyTextFieldD((AppD) app);
		fldNullHyp.setColumns(fieldWidth);
		addActionListener(fldNullHyp);
		((MyTextFieldD) fldNullHyp).addFocusListener(this);

		lblConfLevel = new JLabel();
		fldConfLevel = new MyTextFieldD((AppD) app);
		fldConfLevel.setColumns(fieldWidth);
		addActionListener(fldConfLevel);
		((MyTextFieldD) fldNullHyp).addFocusListener(this);

		lblSigma = new JLabel();
		fldSigma = new MyTextFieldD((AppD) app);
		fldSigma.setColumns(fieldWidth);
		addActionListener(fldSigma);
		((MyTextFieldD) fldNullHyp).addFocusListener(this);

		lblSampleStat1 = new JLabel[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new JLabel();
		}

		fldSampleStat1 = new TextObject[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = new MyTextFieldD((AppD) app);
			fldSampleStat1[i].setColumns(fieldWidth);
			addActionListener(fldSampleStat1[i]);
			((MyTextFieldD) fldSampleStat1[i]).addFocusListener(this);
		}

		lblSampleStat2 = new JLabel[3];
		for (int i = 0; i < lblSampleStat2.length; i++) {
			lblSampleStat2[i] = new JLabel();
		}

		fldSampleStat2 = new MyTextFieldD[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = new MyTextFieldD((AppD) app);
			fldSampleStat2[i].setColumns(fieldWidth);
			addActionListener(fldSampleStat2[i]);
			((MyTextFieldD) fldSampleStat2[i]).addFocusListener(this);
		}
	}

	@Override
	public void setLabels() {

		lblResult.setText(loc.getMenu("Result"));
		lblNull.setText(loc.getMenu("NullHypothesis"));
		lblTailType.setText(loc.getMenu("AlternativeHypothesis"));
		lblConfLevel.setText(loc.getMenu("ConfidenceLevel"));
		lblSigma.setText(loc.getMenu("StandardDeviation.short"));
		btnCalculate.setText(loc.getMenu("Calculate"));

		switch (sc.getSelectedProcedure()) {

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
		case ZMEAN2_CI:
		case TMEAN2_CI:
		case ZPROP2_TEST:
		case ZPROP2_CI:
			lblSampleHeader1.setText(loc.getMenu("Sample1"));
			break;

		default:
			lblSampleHeader1.setText(loc.getMenu("Sample"));
		}

		lblSampleHeader2.setText(loc.getMenu("Sample2"));

		ckPooled.setText(loc.getMenu("Pooled"));

		setHypParameterLabel();
		setLabelStrings();
		setProcedureComboLabels();
		setSampleFieldLabels();

		panelChiSquare.setLabels();

		// reset the text in the result panel
		recompute(false);

	}

	private void setHypParameterLabel() {
		switch (sc.getSelectedProcedure()) {

		case ZMEAN_TEST:
		case TMEAN_TEST:
			lblHypParameter
					.setText(loc.getMenu("HypothesizedMean.short") + " = ");
			break;

		case ZMEAN2_TEST:
		case TMEAN2_TEST:
			lblHypParameter
					.setText(loc.getMenu("DifferenceOfMeans.short") + " = ");
			break;

		case ZPROP_TEST:
			lblHypParameter.setText(
					loc.getMenu("HypothesizedProportion.short") + " = ");
			break;

		case ZPROP2_TEST:
			lblHypParameter.setText(
					loc.getMenu("DifferenceOfProportions.short") + " = ");
			break;

		default:
			lblHypParameter.setText(loc.getMenu(""));
		}
	}

	private void setProcedureComboLabels() {

		combolabelsPreprocess();
		cbProcedure.removeActionListener(this);
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
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_CI));

		cbProcedure.addItem(ListSeparatorRenderer.SEPARATOR);
		cbProcedure.addItem(mapProcedureToName.get(Procedure.GOF_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.CHISQ_TEST));

		cbProcedure.setMaximumRowCount(cbProcedure.getItemCount());
		cbProcedure.setSelectedItem(
				mapProcedureToName.get(sc.getSelectedProcedure()));
		cbProcedure.addActionListener(this);
		// TODO for testing only, remove later
		// cbProcedure.setSelectedItem(mapProcedureToName
		// .get(Procedure.CHISQ_TEST));

	}

	private void setSampleFieldLabels() {

		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i].setText("");
			lblSampleStat2[i].setText("");
		}

		switch (sc.getSelectedProcedure()) {
		default:
			// do nothing
			break;
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

	private void updateGUI() {
		setHypParameterLabel();
		setSampleFieldLabels();
		setSampleFieldText();
		for (int i = 0; i < 3; i++) {
			lblSampleStat1[i]
					.setVisible(isNotEmpty(lblSampleStat1[i].getText()));
			fldSampleStat1[i]
					.setVisible(isNotEmpty(lblSampleStat1[i].getText()));
			lblSampleStat2[i]
					.setVisible(isNotEmpty(lblSampleStat2[i].getText()));
			fldSampleStat2[i]
					.setVisible(isNotEmpty(lblSampleStat2[i].getText()));
		}

		lblSampleHeader2.setVisible((isNotEmpty(lblSampleStat2[0].getText())));

		ckPooled.setVisible(sc.getSelectedProcedure() == Procedure.TMEAN2_TEST
				|| sc.getSelectedProcedure() == Procedure.TMEAN2_CI);

		setPanelLayout();
		wrappedPanel.revalidate();

	}

	private static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s);
	}

	private void setPanelLayout() {

		panelBasicProcedures.setVisible(false);
		panelChiSquare.getWrappedPanel().setVisible(false);

		switch (sc.getSelectedProcedure()) {

		case CHISQ_TEST:
		case GOF_TEST:
			panelChiSquare.getWrappedPanel().setVisible(true);
			panelChiSquare.updateGUI();
			break;

		default:
			setInputPanelLayout();
			panelBasicProcedures.setVisible(true);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e);
	}

	public void doActionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField) source);
		}

		if (source == cbProcedure && cbProcedure.getSelectedIndex() >= 0) {
			sc.setSelectedProcedure(mapNameToProcedure
					.get(cbProcedure.getSelectedItem()));
			this.panelChiSquare.updateCollection();
			updateGUI();
			updateResult();
			// setLabels();

			// reset the scrollpane to the top
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
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

	public void updateResult() {
		updateResult(true);
	}

	public void doTextFieldActionPerformed(JTextField source) {

		if (source.getText().equals(ListSeparatorRenderer.SEPARATOR)) {
			return;
		}

		updateResult();
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof MyTextFieldD) {
			((MyTextFieldD) e.getSource()).selectAll();
		}

	}

	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() instanceof MyTextFieldD) {
			doTextFieldActionPerformed((MyTextFieldD) e.getSource());
		}

	}

	public void updateFonts(Font font) {
		setStyleSheetFontSize((HTMLEditorKit) resultPane.getEditorKit(), font);
		wrappedPanel.setFont(font);
		updateResultText(bodyText.toString());
	}

	private static void setStyleSheetFontSize(HTMLEditorKit kit, Font font) {

		StyleSheet styleSheet = kit.getStyleSheet();
		String size = "" + font.getSize();
		styleSheet.addRule("body {font-size : " + size + "pt }");

	}

	private static void setStyleSheets(HTMLEditorKit kit) {
		// add some styles to the html
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule(
				"body {color:#00008B; font : 9pt verdana; margin: 4px;  }");

		String padding = "padding-top:2px; padding-bottom:2px;padding-left:5px;padding-right:5px;";
		styleSheet.addRule(
				"td {text-align: center; border-top-width: 1px; border-bottom-width: 1px;border-left-width: 1px;border-right-width: 1px;border-style:solid; border-color:#00008B;"
						+ padding + "}");

	}

	@Override
	protected void updateResultText(String str) {

		String htmlString = "<html><body>\n" + str
				+ "</body>\n";
		resultPane.setText(htmlString);

	}

	/**
	 * @return the wrapped panel
	 */
	public JPanel getWrappedPanel() {
		return wrappedPanel;
	}

	@Override
	protected void resetCaret() {
		resultPane.setCaretPosition(0);
	}

	@Override
	protected boolean btnRightIsSelected() {
		return btnRight.isSelected();
	}

	@Override
	protected boolean btnLeftIsSelected() {
		return btnLeft.isSelected();
	}

	@Override
	public void addActionListener(TextObject obj) {
		((MyTextFieldD) obj).addActionListener(this);

	}

	@Override
	public void removeActionListener(TextObject obj) {
		((MyTextFieldD) obj).removeActionListener(this);

	}

	@Override
	protected void updateTailCheckboxes(boolean left, boolean right) {
		btnLeft.setSelected(left);
		btnRight.setSelected(right);
		btnTwo.setSelected(!left && !right);
	}
}
