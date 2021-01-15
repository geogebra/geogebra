package org.geogebra.keyboard.web.factory.model.inputbox.math;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.INFINITY;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.Characters;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;
import org.geogebra.keyboard.web.factory.model.inputbox.util.MathKeyUtil;

public class VectorMatrixMathKeyboardFactory implements KeyboardModelFactory {

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
		addInputButton(row, buttonFactory, Characters.imaginaryI, "i", "altText.Imaginaryi");
		addInputButton(row, buttonFactory, INFINITY);
		addInputButton(row, buttonFactory, DEGREE);
		addInputButton(row, buttonFactory, ".");
		addButton(row, buttonFactory.createEmptySpace(0.2f));

		addInputButton(row, buttonFactory, "1");
		addInputButton(row, buttonFactory, "2");
		addInputButton(row, buttonFactory, "3");
		addConstantCustomButton(row, buttonFactory, Resource.UP_ARROW,
				Action.UP_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);

		row = mathKeyboard.nextRow(9.2f);
		MathKeyUtil.addParenthesesPiE(row, buttonFactory);
		addButton(row, buttonFactory.createEmptySpace(0.2f));

		addInputButton(row, buttonFactory, "0");
		addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.DOWN_ARROW, Action.DOWN_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

		return mathKeyboard;
	}
}
