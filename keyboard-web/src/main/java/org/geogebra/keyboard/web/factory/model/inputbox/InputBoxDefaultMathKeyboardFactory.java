package org.geogebra.keyboard.web.factory.model.inputbox;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.INFINITY;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.Characters;
import org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil;

public class InputBoxDefaultMathKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow(9.2f);
		addInputButton(row, buttonFactory, Characters.x, "x");
		addInputButton(row, buttonFactory, Characters.y, "y");
		addInputButton(row, buttonFactory, Characters.z, "z");
		addButton(row, buttonFactory.createEmptySpace(1.2f));
		NumberKeyUtil.addFirstRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
		addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
		addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
		addConstantInputButton(row, buttonFactory, Resource.FRACTION, "/");
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addSecondRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		addInputButton(row, buttonFactory, Characters.imaginaryI, "i", "altText.Imaginaryi");
		addInputButton(row, buttonFactory, INFINITY);
		addInputButton(row, buttonFactory, DEGREE);
		addInputButton(row, buttonFactory, ",");
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addThirdRow(row, buttonFactory);

		row = mathKeyboard.nextRow(9.2f);
		addInputButton(row, buttonFactory, "(");
		addInputButton(row, buttonFactory, ")");
		addInputButton(row, buttonFactory, PI);
		addInputButton(row, buttonFactory, "e", EULER);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		NumberKeyUtil.addFourthRow(row, buttonFactory);

		return mathKeyboard;
	}
}
