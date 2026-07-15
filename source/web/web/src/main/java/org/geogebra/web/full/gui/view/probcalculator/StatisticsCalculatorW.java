/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.view.probcalculator;

import java.util.Arrays;
import java.util.function.Consumer;

import org.geogebra.common.gui.view.probcalculator.Procedure;
import org.geogebra.common.gui.view.probcalculator.StatisticsCalculator;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.impl.statistics.StatisticalTestTypeProperty;
import org.geogebra.common.util.TextObject;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.event.dom.client.KeyUpEvent;
import org.gwtproject.event.dom.client.KeyUpHandler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.TextBox;
import org.gwtproject.user.client.ui.Widget;

/**
 *  Statistics calculator for classic
 */
public class StatisticsCalculatorW extends StatisticsCalculator
		implements ChangeHandler, BlurHandler, KeyUpHandler {
	private FlowPanel wrappedPanel;
	private FlowPanel resultPane;
	private Label lblResult;
	private Label lblSampleHeader1;
	private Label lblSampleHeader2;
	private ComponentCheckbox ckPooled;
	private ComponentDropDown statisticalTest;
	private RadioButtonPanel<String> tailRadioButtonPanel;
	private Label lblNull;
	private Label lblTailType;

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

		ckPooled.setLabels();

		setHypParameterLabel();
		setLabelStrings();
		setSampleFieldLabels();

		panelChiSquare.setLabels();

		// reset the text in the result panel
		recompute(false);
	}

	private void setHypParameterLabel() {
		switch (sc.getSelectedProcedure()) {
		case ZMEAN_TEST:
		case TMEAN_TEST:
			fldNullHyp.updateLabel("HypothesizedMean.short");
			break;
		case ZMEAN2_TEST:
		case TMEAN2_TEST:
			fldNullHyp.updateLabel("DifferenceOfMeans.short");
			break;
		case ZPROP_TEST:
			fldNullHyp.updateLabel("HypothesizedProportion.short");
			break;
		case ZPROP2_TEST:
			fldNullHyp.updateLabel("DifferenceOfProportions.short");
			break;
		default:
			fldNullHyp.updateLabel("");
		}
	}

	private void initStatisticalTest() {
		PropertyView statisticalTestProperty = PropertyView.of(
				new StatisticalTestTypeProperty(loc, sc));
		if (statisticalTestProperty instanceof PropertyView.Dropdown dropdown) {
			statisticalTest = new ComponentDropDown((AppW) app,
					dropdown.getPropertyName(), dropdown);
			statisticalTest.addChangeHandler(() -> {
				this.panelChiSquare.updateCollection();
				updateGUI();
				updateResult(true);
			});
		}
	}

	private void updateFieldLabelAndVisibility(TextObject textObject, String label,
			boolean visibility) {
		textObject.updateLabel(label);
		textObject.setVisible(visibility);
	}

	private void setSampleFieldLabels() {
		for (int i = 0; i < 3; i++) {
			updateFieldLabelAndVisibility(fldSampleStat1[i], "", false);
			updateFieldLabelAndVisibility(fldSampleStat2[i], "", false);
		}

		switch (sc.getSelectedProcedure()) {
		case ZMEAN_TEST:
		case ZMEAN_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strSigma, true);
			updateFieldLabelAndVisibility(fldSampleStat1[2], strN, true);
			break;
		case TMEAN_TEST:
		case TMEAN_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strSD, true);
			updateFieldLabelAndVisibility(fldSampleStat1[2], strN, true);
			break;
		case ZMEAN2_TEST:
		case ZMEAN2_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strSigma, true);
			updateFieldLabelAndVisibility(fldSampleStat1[2], strN, true);
			updateFieldLabelAndVisibility(fldSampleStat2[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat2[1], strSigma, true);
			updateFieldLabelAndVisibility(fldSampleStat2[2], strN, true);
			break;
		case TMEAN2_TEST:
		case TMEAN2_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strSD, true);
			updateFieldLabelAndVisibility(fldSampleStat1[2], strN, true);
			updateFieldLabelAndVisibility(fldSampleStat2[0], strMean, true);
			updateFieldLabelAndVisibility(fldSampleStat2[1], strSD, true);
			updateFieldLabelAndVisibility(fldSampleStat2[2], strN, true);
			break;
		case ZPROP_TEST:
		case ZPROP_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strSuccesses, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strN, true);
			break;
		case ZPROP2_TEST:
		case ZPROP2_CI:
			updateFieldLabelAndVisibility(fldSampleStat1[0], strSuccesses, true);
			updateFieldLabelAndVisibility(fldSampleStat1[1], strN, true);
			updateFieldLabelAndVisibility(fldSampleStat2[0], strSuccesses, true);
			updateFieldLabelAndVisibility(fldSampleStat2[1], strN, true);
			break;
		default:
			// do nothing
			break;
		}
	}

	private void updateGUI() {
		setHypParameterLabel();
		setSampleFieldLabels();
		setSampleFieldText();

		lblSampleHeader2.setVisible(((Widget) fldSampleStat2[0]).asWidget().isVisible());
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

		panelBasicProcedures.clear();
		panelSample1.clear();
		panelSample2.clear();
		panelTestAndCI.clear();

		panelSample1.add(lblSampleHeader1);

		for (TextObject textObject : fldSampleStat1) {
			panelSample1.add((Widget) textObject);
			panelSample1.add(new LineBreak());
		}

		panelSample2.add(lblSampleHeader2);

		for (TextObject textObject : fldSampleStat2) {
			panelSample2.add((Widget) textObject);
			panelSample2.add(new LineBreak());
		}

		switch (sc.getSelectedProcedure()) {
		case ZMEAN_TEST:
		case ZMEAN2_TEST:
		case TMEAN_TEST:
		case TMEAN2_TEST:
		case ZPROP_TEST:
		case ZPROP2_TEST:
			panelTestAndCI.add(lblNull);
			panelTestAndCI.add((Widget) fldNullHyp);
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(lblTailType);
			panelTestAndCI.add(tailRadioButtonPanel);
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(ckPooled);
			break;
		case ZMEAN_CI:
		case ZMEAN2_CI:
		case TMEAN_CI:
		case TMEAN2_CI:
		case ZPROP_CI:
		case ZPROP2_CI:
			panelTestAndCI.add((Widget) fldConfLevel);
			panelTestAndCI.add(new LineBreak());
			panelTestAndCI.add(ckPooled);
			break;
		default:
			// do nothing
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

	private static final class LineBreak extends FlowPanel {
		private LineBreak() {
			this.setStyleName("lineBreak");
		}
	}

	private void createControlPanel() {
		panelControl = new FlowPanel();
		panelControl.addStyleName("panelControl");
		panelControl.add(statisticalTest);
	}

	private void addNextTabIndex(ComponentInputField field) {
		field.getTextWidget().getTextField().setTabIndex(tabIndex);
		tabIndex++;
	}

	private void createGUIElements() {
		tabIndex = 1; // 0 as first tabindex does not work.
		resultPane = new FlowPanel();
		resultPane.addStyleName("resultPane");

		s1 = new double[3];
		s2 = new double[3];

		lblResult = new Label();
		lblResult.addStyleName("lblHeading");

		lblSampleHeader1 = new Label();
		lblSampleHeader2 = new Label();

		bodyText = new StringBuilder();

		ckPooled = new ComponentCheckbox(loc, false, "Pooled", selected -> {
					sc.pooled = selected;
					updateResult(true);
				});
		ckPooled.addStyleName("ckPooled");

		initStatisticalTest();

		tailRadioButtonPanel = new RadioButtonPanel<>(loc,
				Arrays.asList(newRadioButtonData(StatisticsCollection.tail_left),
						newRadioButtonData(StatisticsCollection.tail_right),
						newRadioButtonData(StatisticsCollection.tail_two)),
				StatisticsCollection.tail_two,
				ignore -> updateResult(true));

		lblNull = new Label();
		lblTailType = new Label();

		fldNullHyp = buildTextField("NullHypothesis");
		fldConfLevel = buildTextField("ConfidenceLevel");
		fldSigma = buildTextField("StandardDeviation.short");

		fldSampleStat1 = new ComponentInputField[3];
		for (int i = 0; i < fldSampleStat1.length; i++) {
			fldSampleStat1[i] = buildTextField("Mean");
		}

		fldSampleStat2 = new ComponentInputField[3];
		for (int i = 0; i < fldSampleStat2.length; i++) {
			fldSampleStat2[i] = buildTextField("Mean");
		}
	}

	private RadioButtonData<String> newRadioButtonData(String label) {
		return new RadioButtonData<>(label, label);
	}

	private TextObject buildTextField(String label) {
		ComponentInputField textField = new ComponentInputField((AppW) app, "", loc.getMenu(label),
				"", "");
		textField.addDomHandler(this, KeyUpEvent.getType());
		textField.addDomHandler(this, BlurEvent.getType());
		addInsertHandler(textField.getTextWidget(), this::doTextFieldActionPerformed);
		addNextTabIndex(textField);
		return textField;
	}

	@Override
	protected void updateResultText(String str) {
		resultPane.getElement().setInnerHTML(str);
	}

	@Override
	public void onChange(ChangeEvent event) {
		this.panelChiSquare.updateCollection();
		updateGUI();
		updateResult(true);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() != KeyCodes.KEY_LEFT
				&& event.getNativeKeyCode() != KeyCodes.KEY_RIGHT) {
			doTextFieldActionPerformed(
					event.getNativeKeyCode() == KeyCodes.KEY_ENTER);
		}
	}

	/**
	 * use ggb keyboard and add input handler for text field
	 * @param field - text field
	 * @param handler - on input
	 */
	public static void addInsertHandler(final AutoCompleteTextFieldW field,
			Consumer<Boolean> handler) {
		field.enableGGBKeyboard();
		field.addInsertHandler(text -> {
			handler.accept(false);
			if (NavigatorUtil.isMobile()) {
				field.updateCursorOverlay();
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
	protected String getSelectedTail() {
		return tailRadioButtonPanel.getValue();
	}

	@Override
	protected void updateTailCheckboxes(String tail) {
		tailRadioButtonPanel.setValue(tail);
	}

	@Override
	public void settingsChanged() {
		wrappedPanel.clear();
		createGUI(wrappedPanel);
	}
}
