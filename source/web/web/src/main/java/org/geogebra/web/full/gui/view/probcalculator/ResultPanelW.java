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

import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_INTERVAL;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_LEFT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_RIGHT;
import static org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView.PROB_TWO_TAILED;

import org.geogebra.common.gui.view.probcalculator.ResultPanel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;

/**
 * Panel to edit and display probability bounds and results
 */
public class ResultPanelW extends FlowPanel implements ResultPanel {

	private String lblEquals;
	private String lblPlus;
	private Label lblTwoTailedResult;
	private MathTextFieldW fldLow;
	private MathTextFieldW fldResult;
	private Label lblResult;
	private MathTextFieldW fldHigh;
	private final Localization loc;
	private final InsertHandler insertHandler;
	private final App app;
	private String lblXGreater;
	private String lblXSign;

	/**
	 * Constructor
	 * @param app the application
	 * @param insertHandler to update
	 */
	public ResultPanelW(App app, InsertHandler insertHandler) {
		super();
		this.app = app;
		loc = app.getLocalization();
		this.insertHandler = insertHandler;
		initGUI();
		AriaHelper.setRole(this, "group");
	}

	private void initGUI() {
		addStyleName("resultPanel");
		lblTwoTailedResult = getHiddenLabel("");
		lblEquals = " = ";
		lblPlus = " + ";
		lblXGreater = "X >";
		fldLow = createField(80);
		fldHigh = createField(80);
		fldResult = createField(96);
		lblResult = new Label();
		AriaHelper.setLabel(lblResult, loc.getMenu("Probability"));
	}

	private MathTextFieldW createField(int width) {
		MathTextFieldW field = new MathTextFieldW(app);
		field.setPxWidth(width);
		addInsertHandler(field);
		return field;
	}

	/**
	 *
	 * @param field to add handler to.
	 */
	void addInsertHandler(final MathTextFieldW field) {
		field.addInputHandler(() -> insertHandler.doTextFieldActionPerformed(field, false));
		field.addChangeHandler((enter) -> insertHandler.doTextFieldActionPerformed(field, true));
	}

	@Override
	public void showInterval() {
		clear();
		add(getHiddenLabel(loc.getMenu("ProbabilityOf")));
		add(wrapInFocusHolder(fldLow, "Lower.Bound"));
		add(getHiddenLabel(SpreadsheetViewInterface.X_BETWEEN));
		add(wrapInFocusHolder(fldHigh, "Upper.Bound"));
		add(getHiddenLabel(loc.getMenu("EndProbabilityOf") + " = "));
		add(lblResult);
	}

	@Override
	public void showTwoTailed() {
		showTwoTailed(greaterThanEqual());
	}

	private void showTwoTailed(String greaterSign) {
		clear();
		lblXSign = greaterSign;
		wrapProbabilityOf(xLessThanEqual(), fldLow, "", lblPlus, "Left.Upper.Bound");
		wrapProbabilityOf(lblXSign, fldHigh, "", lblEquals, "Right.Lower.Bound");
		add(lblTwoTailedResult);
		add(lblResult);
	}

	@Override
	public void showTwoTailedOnePoint() {
		showTwoTailed(lblXGreater);
	}

	@Override
	public void showLeft() {
		clear();
		wrapProbabilityOf(xLessThanEqual(), fldHigh, "", lblEquals, "Upper.Bound");
		add(wrapInFocusHolder(fldResult, "Probability"));
	}

	private String xLessThanEqual() {
		return loc.getMenu("XLessThanOrEqual");
	}

	@Override
	public void showRight() {
		clear();
		wrapProbabilityOf("", fldLow, lessThanEqual(), lblEquals, "Lower.Bound");
		add(wrapInFocusHolder(fldResult, "Probability"));
	}

	private String lessThanEqual() {
		return SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X;
	}

	private String greaterThanEqual() {
		return SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	}

	private void wrapProbabilityOf(String before, MathTextFieldW widget,
			String after, String sign, String ariaLabel) {
		Label begin = getHiddenLabel(loc.getMenu("ProbabilityOf") + " " + before);
		Label end = getHiddenLabel(after + " " + loc.getMenu("EndProbabilityOf") + sign);
		add(begin);
		add(wrapInFocusHolder(widget, ariaLabel));
		add(end);
	}

	private IsWidget wrapInFocusHolder(MathTextFieldW widget, String ariaLabel) {
		FlowPanel holder = new FlowPanel();
		holder.setStyleName("holder");
		holder.add(widget);
		AriaHelper.setLabel(widget.asWidget(), loc.getMenu(ariaLabel));
		return holder;
	}

	@Override
	public void setResultEditable(boolean value) {
		fldResult.setEditable(value);
	}

	@Override
	public void updateResult(String text) {
		fldResult.setText(text);
		lblResult.setText(text);
	}

	@Override
	public void updateLowHigh(String low, String high) {
		fldLow.setText(low);
		fldHigh.setText(high);
	}

	@Override
	public void updateTwoTailedResult(String low, String high) {
		lblTwoTailedResult.setText(low + " + " + high + " =");
	}

	boolean isFieldLow(Object source) {
		return source == fldLow;
	}

	boolean isFieldHigh(Object source) {
		return source == fldHigh;
	}

	boolean isFieldResult(Object source) {
		return source == fldResult;
	}

	@Override
	public void setGreaterThan() {
		lblXSign = "X >";
	}

	@Override
	public void setGreaterOrEqualThan() {
		lblXSign = SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	}

	/**
	 * Updates the aria-label of the result panel based on the selected probability mode.
	 * @param probMode probability mode
	 */
	public void updateAccessibleName(int probMode) {
		String key = switch (probMode) {
			case PROB_INTERVAL -> "Interval.Probability";
			case PROB_LEFT -> "Left.Sided.Probability";
			case PROB_RIGHT -> "Right.Sided.Probability";
			case PROB_TWO_TAILED -> "Two.Tailed.Probability";
			default -> "";
		};
		AriaHelper.setLabel(this, loc.getMenu(key));
	}

	private Label getHiddenLabel(String text) {
		Label label = new Label(text);
		AriaHelper.setAriaHidden(label);
		return label;
	}
}
