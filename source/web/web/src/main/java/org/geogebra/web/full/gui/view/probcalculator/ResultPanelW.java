package org.geogebra.web.full.gui.view.probcalculator;

import org.geogebra.common.gui.view.probcalculator.ResultPanel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
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
	}

	private void initGUI() {
		addStyleName("resultPanel");
		lblTwoTailedResult = new Label();
		lblEquals = " = ";
		lblPlus = " + ";
		lblXGreater = "X >";
		fldLow = createField(80);
		fldHigh = createField(80);
		fldResult = createField(96);
		lblResult = new Label();
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
		add(new Label(loc.getMenu("ProbabilityOf")));
		add(wrapInFocusHolder(fldLow));
		add(new Label(SpreadsheetViewInterface.X_BETWEEN));
		add(wrapInFocusHolder(fldHigh));
		add(new Label(loc.getMenu("EndProbabilityOf") + " = "));
		add(lblResult);
	}

	@Override
	public void showTwoTailed() {
		showTwoTailed(greaterThanEqual());
	}

	private void showTwoTailed(String greaterSign) {
		clear();
		lblXSign = greaterSign;
		wrapProbabilityOf(xLessThanEqual(), fldLow, "", lblPlus);
		wrapProbabilityOf(lblXSign, fldHigh, "", lblEquals);
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
		wrapProbabilityOf(xLessThanEqual(), fldHigh, "", lblEquals);
		add(wrapInFocusHolder(fldResult));
	}

	private String xLessThanEqual() {
		return loc.getMenu("XLessThanOrEqual");
	}

	@Override
	public void showRight() {
		clear();
		wrapProbabilityOf("", fldLow, lessThanEqual(), lblEquals);
		add(wrapInFocusHolder(fldResult));
	}

	private String lessThanEqual() {
		return SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X;
	}

	private String greaterThanEqual() {
		return SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	}

	private void wrapProbabilityOf(String before, MathTextFieldW widget,
			String after, String sign) {
		Label begin = new Label(loc.getMenu("ProbabilityOf") + " " + before);
		Label end = new Label(after + " " + loc.getMenu("EndProbabilityOf") + sign);
		add(begin);
		add(wrapInFocusHolder(widget));
		add(end);
	}

	private IsWidget wrapInFocusHolder(MathTextFieldW widget) {
		FlowPanel holder = new FlowPanel();
		holder.setStyleName("holder");
		holder.add(widget);
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
		lblXSign = "X >";
	}

	@Override
	public void setGreaterOrEqualThan() {
		lblXSign = SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X;
	}
}