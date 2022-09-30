package org.geogebra.desktop.gui.view.probcalculator;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geogebra.common.gui.view.probcalculator.ResultPanel;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

class ResultPanelD extends JPanel implements ResultPanel {
	private final LocalizationD loc;
	JLabel lblProb;
	JLabel lblProbOf;
	private JLabel lblBetween;
	private JLabel lblEndProbOf;
	private JLabel lblEquals;
	private JLabel lblPlus;
	private JLabel lblTwoTailedResult;
	private JLabel lblXGreater;
	private JLabel lblXSign;
	private final AppD app;
	private JTextField fldLow;
	private JTextField fldHigh;
	private JTextField fldResult;

	public ResultPanelD(AppD app, int hgap, int vgap, int tab) {
		super(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
		this.app = app;
		loc = app.getLocalization();
		add(Box.createHorizontalStrut(tab));
		initGUI();
	}

	private void initGUI() {
		lblProb = new JLabel();
		lblProbOf = new JLabel();
		lblBetween = new JLabel();
		lblEndProbOf = new JLabel();
		lblTwoTailedResult = new JLabel();
		lblEquals = new JLabel(" = ");
		lblPlus = new JLabel(" + ");
		lblXGreater = new JLabel("X > ");
		fldLow = createField(5);
		fldHigh = createField(6);
		fldResult = createField(6);
	}

	private JTextField createField(int width) {
		JTextField field = new MyTextFieldD(app);
		field.setColumns(width);
		return field;
	}

	@Override
	public void showInterval() {
		removeAll();
		add(lblProbOf);
		add(fldLow);
		add(lblBetween);
		add(fldHigh);
		add(lblEndProbOf);
		add(fldResult);
		lblBetween.setText(SpreadsheetViewInterface.X_BETWEEN);
		revalidate();
	}

	@Override
	public void showTwoTailed() {
		showTwoTailed(greaterThanEqual());
	}

	@Override
	public void showTwoTailedOnePoint() {
		showTwoTailed(lblXGreater);
	}

	private void showTwoTailed(JComponent greaterSign) {
		removeAll();
		lblXSign = (JLabel) greaterSign;
		wrapProbabilityOf(xLessThanEqual(), fldLow);
		add(lblPlus);
		wrapProbabilityOf(lblXSign, fldHigh);
		add(lblTwoTailedResult);
		add(lblEquals);
		add(fldResult);
		revalidate();
	}

	@Override
	public void showLeft() {
		removeAll();
		wrapProbabilityOf(xLessThanEqual(), fldHigh);
		add(lblEquals);
		add(fldResult);
		revalidate();
	}

	private JComponent xLessThanEqual() {
		return new JLabel(loc.getMenu("XLessThanOrEqual"));
	}

	@Override
	public void showRight() {
		removeAll();
		wrapProbabilityOf(fldLow, lessThanEqual());
		add(lblEquals);
		add(fldResult);
		revalidate();
	}

	private JComponent lessThanEqual() {
		return new JLabel(SpreadsheetViewInterface.LESS_THAN_OR_EQUAL_TO_X);
	}

	private JComponent greaterThanEqual() {
		return new JLabel(SpreadsheetViewInterface.GREATER_THAN_OR_EQUAL_TO_X);
	}

	private void wrapProbabilityOf(JComponent... components) {
		JLabel begin = new JLabel(loc.getMenu("ProbabilityOf"));
		JLabel end = new JLabel(loc.getMenu("EndProbabilityOf"));
		add(begin);
		for (JComponent component: components) {
			add(component);
		}
		add(end);

	}

	public void setLabels() {
		lblProb.setText(loc.getMenu("Probability") + ": ");

		lblEndProbOf.setText(loc.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(loc.getMenu("ProbabilityOf"));
	}

	@Override
	public void setResultEditable(boolean value) {
		if (value) {
			fldResult.setBackground(fldLow.getBackground());
			fldResult.setBorder(fldLow.getBorder());
			fldResult.setEditable(true);
			fldResult.setFocusable(true);
		} else {
			fldResult.setBackground(getBackground());
			fldResult.setBorder(BorderFactory.createEmptyBorder());
			fldResult.setEditable(false);
			fldResult.setFocusable(false);
		}
	}

	@Override
	public void updateResult(String text) {
		fldResult.setText(text);
		fldResult.setCaretPosition(0);
	}

	@Override
	public void updateLowHigh(String low, String high) {
		fldLow.setText(low);
		fldHigh.setText(high);
		fldLow.setCaretPosition(0);
		fldHigh.setCaretPosition(0);
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

	public void removeActionListener(ActionListener listener) {
		fldLow.removeActionListener(listener);
		fldHigh.removeActionListener(listener);
		fldResult.removeActionListener(listener);
	}

	public void addActionListener(ActionListener listener) {
		fldLow.addActionListener(listener);
		fldHigh.addActionListener(listener);
		fldResult.addActionListener(listener);
	}

	@Override
	public synchronized void addFocusListener(FocusListener listener) {
		fldLow.addFocusListener(listener);
		fldHigh.addFocusListener(listener);
		fldResult.addFocusListener(listener);
	}
}
