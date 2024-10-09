package org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard;

import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

public class InputBoxDefaultMathKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow();
		MathKeyUtil.addXYZ(row, buttonFactory);
		addInputButton(row, buttonFactory, ",");
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		MathKeyUtil.addSqExpRootFrac(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		MathKeyUtil.addPiEIDegree(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow();
		MathKeyUtil.addParenthesesFractionMixed(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}
}
