package org.geogebra.keyboard.web.factory.model.inputbox.math;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.CursiveBoldLetter;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

public class FunctionMathKeyboardFactory implements KeyboardModelFactory {
	private String vars;
	private static final int MAX_VARS = 4;

	public FunctionMathKeyboardFactory(String vars) {
		this.vars = vars;
	}

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow(9.2f);
		addFunctionVarButtons(row, buttonFactory);
		NumberKeyUtil.addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addSqExpRootFrac(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addImInfDegComma(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addParenthesesPiE(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}

	private void addFunctionVarButtons(RowImpl row, ButtonFactory buttonFactory) {
		int nrVars = vars.length();
		for (int i = 0; i < nrVars && i < MAX_VARS; i++) {
			String character = String.valueOf(vars.charAt(i));
			String cursiveBoldLetter = CursiveBoldLetter.getCursiveBoldLetter(
					character);
			addInputButton(row, buttonFactory, cursiveBoldLetter == null
					? character : cursiveBoldLetter, character);
		}
		addButton(row, buttonFactory.createEmptySpace(nrVars > MAX_VARS ? 0.2f : 4.2f - nrVars));
	}
}
