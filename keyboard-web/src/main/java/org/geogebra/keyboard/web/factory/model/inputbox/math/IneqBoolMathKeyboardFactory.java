package org.geogebra.keyboard.web.factory.model.inputbox.math;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyUtil;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

public class IneqBoolMathKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addXYZ(row, buttonFactory);
		NumberKeyUtil.addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addSqExpRootFrac(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		FunctionKeyUtil.addLessGtLessEqGtEq(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addParenthesesPiE(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}

}
