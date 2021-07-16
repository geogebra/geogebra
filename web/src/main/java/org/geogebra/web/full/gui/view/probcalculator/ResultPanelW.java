package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ResultPanel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel to edit and display probability bounds and results
 */
public class ResultPanelW extends FlowPanel implements ResultPanel {
	Label lblProb;
	Label lblProbOf;
	private Label lblBetween;
	private Label lblEndProbOf;
	private Label lblEquals;
	private Label lblPlus;
	private Label lblTwoTailedResult;
	private Label lblResultSum;
	private MathTextFieldW fldLow;
	private MathTextFieldW fldResult;
	private MathTextFieldW fldHigh;
	private final Localization loc;
	private final InsertHandler insertHandler;
	private final App app;
	private Label lblXGreater;
	private Label lblXSign;

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
	}

	private void initGUI() {
		addStyleName("resultPanel");
		lblProb = new Label();
		lblProbOf = new Label();
		lblBetween = new Label();
		lblEndProbOf = new Label();
		lblResultSum = new Label();
		lblTwoTailedResult = new Label();
		lblEquals = new Label(" = ");
		lblPlus = new Label(" + ");
		lblXGreater = new Label("X >");
		fldLow = createField(80);
		fldHigh = createField(80);
		fldResult = createField(96);
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
		field.addChangeHandler(() -> insertHandler.doTextFieldActionPerformed(field, true));
	}

	@Override
	public void showInterval() {
		clear();
		add(lblProbOf);
		add(fldLow);
		add(lblBetween);
		add(fldHigh);
		add(lblEndProbOf);
		add(fldResult);
		lblBetween.setText(SpreadsheetViewInterface.X_BETWEEN);
	}

	@Override
	public void showTwoTailed() {
		showTwoTailed(greaterThanEqual());
	}

	private void showTwoTailed(Label greaterSign) {
		clear();
		lblXSign = greaterSign;
		wrapProbabilityOf(xLessThanEqual(), fldLow.asWidget());
		add(lblPlus);
		wrapProbabilityOf(lblXSign, fldHigh.asWidget());
		add(lblTwoTailedResult);
		add(lblEquals);
		add(fldResult);
	}

	@Override
	public void showTwoTailedOnePoint() {
		showTwoTailed(lblXGreater);
	}

	@Override
	public void showLeft() {
		clear();
		wrapProbabilityOf(xLessThanEqual(), fldHigh.asWidget());
		add(lblEquals);
		add(fldResult);
	}

	private Widget xLessThanEqual() {
		return new Label(loc.getMenu("XLessThanOrEqual"));
	}

	@Override
	public void showRight() {
		clear();
		wrapProbabilityOf(fldLow.asWidget(), lessThanEqual());
		add(lblEquals);
		add(fldResult);
	}

	private Widget lessThanEqual() {
		return new Label(SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X);
	}

	private Label greaterThanEqual() {
		return new Label(SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X);
	}

	private void wrapProbabilityOf(Widget... widgets) {
		Label begin = new Label(loc.getMenu("ProbabilityOf"));
		Label end = new Label(loc.getMenu("EndProbabilityOf"));
		add(begin);
		for (Widget widget: widgets) {
			add(widget);
		}
		add(end);
	}

	@Override
	public void setLabels() {
		lblProb.setText(loc.getMenu("Probability") + ": ");

		lblEndProbOf.setText(loc.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(loc.getMenu("ProbabilityOf"));
	}

	@Override
	public void setResultEditable(boolean value) {
		fldResult.setEditable(value);
	}

	@Override
	public void updateResult(String text) {
		fldResult.setText(text);
	}

	@Override
	public void updateResultSum(String text) {
		lblResultSum.setText(text);
	}

	@Override
	public void updateLowHigh(String low, String high) {
		fldLow.setText(low);
		fldHigh.setText(high);
	}

	@Override
	public void updateTwoTailedResult(String low, String high) {
		lblTwoTailedResult.setText("= " + low + " + " + high);
	}

	@Override
	public boolean isFieldLow(Object source) {
		return source == fldLow;
	}

	@Override
	public boolean isFieldHigh(Object source) {
		return source == fldHigh;
	}

	@Override
	public boolean isFieldResult(Object source) {
		return source == fldResult;
	}

	@Override
	public void setGreaterThan() {
		lblXSign.setText("X >");
	}

	@Override
	public void setGreaterOrEqualThan() {
		lblXSign.setText(SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X);
	}
}