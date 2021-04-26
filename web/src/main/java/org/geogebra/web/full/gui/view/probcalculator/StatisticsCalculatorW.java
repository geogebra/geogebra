package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.App;
import org.geogebra.common.util.TextObject;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ListBoxApi;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author gabor
 * 
 *         Statistics calculator for web
 *
 */
public class StatisticsCalculatorW extends StatisticsCalculator
		implements ChangeHandler, ClickHandler, ValueChangeHandler<Boolean>,
		BlurHandler, KeyUpHandler {

	private FlowPanel wrappedPanel;
	private FlowPanel resultPane;
	private Label lblResult;
	private Label lblSampleHeader1;
	private Label lblSampleHeader2;
	private CheckBox ckPooled;
	private ListBox cbProcedure;
	private Button btnCalculate;
	private RadioButton btnLeft;
	private RadioButton btnRight;
	private RadioButton btnTwo;
	private Label lblNull;
	private Label lblHypParameter;
	private Label lblTailType;

	private Label lblConfLevel;
	private Label lblSigma;
	private Label[] lblSampleStat1;
	private Label[] lblSampleStat2;
	private FlowPanel panelControl;
	private FlowPanel panelBasicProcedures;
	private FlowPanel panelSample1;
	private FlowPanel panelSample2;
	private FlowPanel panelTestAndCI;
	private ChiSquarePanelW panelChiSquare;
	private FlowPanel scroller;
	private int tabIndex;

	/**
	 * @param app
	 *            application
	 */
	public StatisticsCalculatorW(App app) {
		super(app);
		createGUI(new FlowPanel());
	}

	private void createGUI(FlowPanel root) {
		this.wrappedPanel = root;
		wrappedPanel.addStyleName("StatisticsCalculatorW");

		createGUIElements();
		createControlPanel();
		setInputPanelLayout();

		panelChiSquare = new ChiSquarePanelW(loc, this);

		// prepare result panel
		FlowPanel resultPanel = new FlowPanel();
		resultPanel.setStyleName("resultPanel");
		resultPanel.add(lblResult);
		resultPanel.add(resultPane);

		// procedure panel
		FlowPanel procedurePanel = new FlowPanel();
		procedurePanel.add(panelBasicProcedures);
		procedurePanel.add(panelChiSquare.getWrappedPanel());
		procedurePanel.add(resultPanel);

		FlowPanel procedureWrapper = new FlowPanel();
		procedureWrapper.add(procedurePanel);

		scroller = new FlowPanel();
		scroller.addStyleName("scroller");
		scroller.add(procedureWrapper);

		// main content panel
		FlowPanel main = new FlowPanel();
		main.add(panelControl);
		main.add(scroller);

		wrappedPanel.add(main);

		setLabels();
		updateGUI();
		panelChiSquare.updateVisibility();
	}

	/**
	 * Update translation
	 */
	void setLabels() {
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

		cbProcedure.clear();
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_TEST));

		cbProcedure.addItem(ProbabilityCalculatorViewW.SEPARATOR);

		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.TMEAN2_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP_CI));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.ZPROP2_CI));

		cbProcedure.addItem(ProbabilityCalculatorViewW.SEPARATOR);

		cbProcedure.addItem(mapProcedureToName.get(Procedure.GOF_TEST));
		cbProcedure.addItem(mapProcedureToName.get(Procedure.CHISQ_TEST));
		ListBoxApi.select(mapProcedureToName.get(sc.getSelectedProcedure()),
				cbProcedure);
		// cbProcedure.setMaximumRowCount(cbProcedure.getItemCount());

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
					.setVisible(!"".equals(lblSampleStat1[i].getText()));
			fldSampleStat1[i]
					.setVisible(!"".equals(lblSampleStat1[i].getText()));
			lblSampleStat2[i]
					.setVisible(!"".equals(lblSampleStat2[i].getText()));
			fldSampleStat2[i]
					.setVisible(!"".equals(lblSampleStat2[i].getText()));
		}

		lblSampleHeader2.setVisible((lblSampleStat2[0].getText() != null
				&& !"".equals(lblSampleStat2[0].getText())));
		setLabels();

		ckPooled.setVisible(sc.getSelectedProcedure() == Procedure.TMEAN2_TEST
				|| sc.getSelectedProcedure() == Procedure.TMEAN2_CI);

		setPanelLayout();

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

	private void setInputPanelLayout() {
		if (panelBasicProcedures == null) {
			panelBasicProcedures = new FlowPanel();
		}

		if (panelSample1 == null) {
			panelSample1 = new FlowPanel();
			panelSample1.addStyleName("panelSample1");
		}

		if (panelSample2 == null) {
			panelSample2 = new FlowPanel();
			panelSample2.addStyleName("panelSample2");
		}

		if (panelTestAndCI == null) {
			panelTestAndCI = new FlowPanel();
			panelTestAndCI.addStyleName("panelTestAndCI");
		}

		// ---- add components

		panelBasicProcedures.clear();
		panelSample1.clear();
		panelSample2.clear();
		panelTestAndCI.clear();

		panelSample1.add(lblSampleHeader1);

		for (int i = 0; i < lblSampleStat1.length; i++) {
			panelSample1.add(lblSampleStat1[i]);
			panelSample1.add((AutoCompleteTextFieldW) fldSampleStat1[i]);
			panelSample1.add(new LineBreak());
			// panelSample1.getElement().appendChild(Document.get().createBRElement());
		}

		// panelSample2.add(new Label(" ")); //TODO: ?????????? CSS!!!!!
		panelSample2.add(lblSampleHeader2);

		for (int i = 0; i < lblSampleStat2.length; i++) {
			panelSample2.add(lblSampleStat2[i]);
			panelSample2.add((AutoCompleteTextFieldW) fldSampleStat2[i]);
			panelSample2.add(new LineBreak());
			// panelSample2.getElement().appendChild(Document.get().createBRElement());
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
				panelTestAndCI.add(lblNull);
				panelTestAndCI.add((AutoCompleteTextFieldW) fldNullHyp);
				panelTestAndCI.add(lblHypParameter);
				// panelTestAndCI.getElement().appendChild((Document.get().createBRElement()));
			} else {
				// eg mu = 1.1
				panelTestAndCI.add(lblNull);
				panelTestAndCI.add(lblHypParameter);
				panelTestAndCI.add((AutoCompleteTextFieldW) fldNullHyp);
				// panelTestAndCI.getElement().appendChild((Document.get().createBRElement()));

			}
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(lblTailType);
			panelTestAndCI.add(btnLeft);
			panelTestAndCI.add(btnRight);
			panelTestAndCI.add(btnTwo);
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(ckPooled);
			// panelTestAndCI.getElement().appendChild(Document.get().createBRElement());
			break;

		case ZMEAN_CI:
		case ZMEAN2_CI:
		case TMEAN_CI:
		case TMEAN2_CI:
		case ZPROP_CI:
		case ZPROP2_CI:
			panelTestAndCI.add(lblConfLevel);
			panelTestAndCI.add((AutoCompleteTextFieldW) fldConfLevel);
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(ckPooled);
			// panelTestAndCI.getElement().appendChild(Document.get().createBRElement());
			break;
		}

		if (forceZeroHypothesis()) {
			fldNullHyp.setText("0");
			fldNullHyp.setEditable(false);
		} else {
			fldNullHyp.setEditable(true);
		}

		panelBasicProcedures.add(panelTestAndCI);
		panelBasicProcedures.add(panelSample1);
		panelBasicProcedures.add(panelSample2);
	}

	private static class LineBreak extends FlowPanel {
		public LineBreak() {
			this.setStyleName("lineBreak");
		}
	}

	private void createControlPanel() {
		panelControl = new FlowPanel();
		panelControl.addStyleName("panelControl");
		panelControl.add(cbProcedure);
	}

	private void addNextTabIndex(AutoCompleteTextFieldW field) {
		field.getTextField().setTabIndex(tabIndex);
		tabIndex++;
	}

	private void createGUIElements() {
		tabIndex = 1; // 0 as first tabindex does not work.
		// resultPane = new RichTextArea();
		resultPane = new FlowPanel();
		resultPane.addStyleName("resultPane");
		// resultPane.setEnabled(false);

		s1 = new double[3];
		s2 = new double[3];

		lblResult = new Label();
		lblResult.addStyleName("lblHeading");

		lblSampleHeader1 = new Label();
		lblSampleHeader2 = new Label();

		bodyText = new StringBuilder();

		ckPooled = new CheckBox();
		ckPooled.addStyleName("ckPooled");
		ckPooled.setValue(false);
		ckPooled.addValueChangeHandler(this);

		cbProcedure = new ListBox();
		cbProcedure.addChangeHandler(this);

		btnCalculate = new Button();
		btnCalculate.addClickHandler(this);
		String id = DOM.createUniqueId();
		btnLeft = new RadioButton(id);
		btnLeft.setText(StatisticsCollection.tail_left);
		btnRight = new RadioButton(id);
		btnRight.setText(StatisticsCollection.tail_right);
		btnTwo = new RadioButton(id);
		btnTwo.setText(StatisticsCollection.tail_two);

		FlowPanel group = new FlowPanel();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);

		btnLeft.addValueChangeHandler(this);
		btnRight.addValueChangeHandler(this);
		btnTwo.addValueChangeHandler(this);
		btnTwo.setValue(true);

		lblNull = new Label();
		lblHypParameter = new Label();
		lblTailType = new Label();

		fldNullHyp = buildTextField();

		lblConfLevel = new Label();
		fldConfLevel = buildTextField();

		lblSigma = new Label();
		fldSigma = buildTextField();

		lblSampleStat1 = new Label[3];
		for (int i = 0; i < lblSampleStat1.length; i++) {
			lblSampleStat1[i] = new Label();
			lblSampleStat1[i].addStyleName("lblSampleStat");
		}

		fldSampleStat1 = new AutoCompleteTextFieldW[3];
		// fldSampleStat1KeyHandlers = new HandlerRegistration[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = buildTextField();
		}

		lblSampleStat2 = new Label[3];
		for (int i = 0; i < lblSampleStat2.length; i++) {
			lblSampleStat2[i] = new Label();
			lblSampleStat2[i].addStyleName("lblSampleStat");
		}

		fldSampleStat2 = new AutoCompleteTextFieldW[3];
		// fldSampleStat2KeyHandlers = new HandlerRegistration[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = buildTextField();
		}

	}

	private TextObject buildTextField() {
		AutoCompleteTextFieldW textField = new AutoCompleteTextFieldW(app);
		textField.setWidthInEm(fieldWidth);
		textField.addKeyUpHandler(this);
		textField.addBlurHandler(this);
		addInsertHandler(textField);
		addNextTabIndex(textField);
		return textField;
	}

	@Override
	protected void updateResultText(String str) {

		resultPane.getElement().setInnerHTML(str);
	}

	/**
	 * Listens to listbox changes
	 */
	@Override
	public void onChange(ChangeEvent event) {
		sc.setSelectedProcedure(mapNameToProcedure
				.get(cbProcedure.getValue(cbProcedure.getSelectedIndex())));
		this.panelChiSquare.updateCollection();
		updateGUI();
		updateResult(true);
	}

	@Override
	public void onClick(ClickEvent event) {
		updateResult(true);
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		Object source = event.getSource();
		if (source == ckPooled) {
			sc.pooled = ckPooled.getValue();
			updateResult(true);
		}

		if (source == btnLeft || source == btnRight || source == btnTwo) {
			updateResult(true);
		}

		if (source == btnCalculate) {
			updateResult(true);
		}
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if ((event.getNativeKeyCode() != KeyCodes.KEY_LEFT
				&& event.getNativeKeyCode() != KeyCodes.KEY_RIGHT)) {
			doTextFieldActionPerformed(
					event.getNativeKeyCode() == KeyCodes.KEY_ENTER);
		}
	}

	/**
	 * @param value
	 *            current value in textfield
	 * @return whether we can handle the key
	 */
	static boolean keyUpNeeded(String value) {
		char last = value.charAt(value.length() - 1);
		return !"".equals(value) && !"-".equals(value) && last != '.';
	}

	private void addInsertHandler(final AutoCompleteTextFieldW field) {
		field.enableGGBKeyboard();
		field.addInsertHandler(new AutoCompleteTextFieldW.InsertHandler() {
			@Override
			public void onInsert(String text) {
				field.removeDummyCursor();
				doTextFieldActionPerformed(false);

				if (Browser.isTabletBrowser()) {
					field.addDummyCursor(field.getCaretPosition());
				}
			}
		});
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (event.getSource() instanceof TextBox) {
			doTextFieldActionPerformed(true);
		}

	}

	/**
	 * Update results when key is pressed or focus lost
	 */
	void doTextFieldActionPerformed(boolean userInitiated) {
		updateResult(userInitiated);
	}

	/**
	 * @return the wrapped Panel
	 */
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}

	@Override
	protected void resetCaret() {
		// not needed
	}

	@Override
	protected boolean btnRightIsSelected() {
		return btnRight.getValue();
	}

	@Override
	protected boolean btnLeftIsSelected() {
		return btnLeft.getValue();
	}

	@Override
	protected void updateTailCheckboxes(boolean left, boolean right) {
		btnLeft.setValue(left);
		btnRight.setValue(right);
		btnTwo.setValue(!left && !right);
	}

	@Override
	public void settingsChanged() {
		wrappedPanel.clear();
		createGUI(wrappedPanel);
	}
}
