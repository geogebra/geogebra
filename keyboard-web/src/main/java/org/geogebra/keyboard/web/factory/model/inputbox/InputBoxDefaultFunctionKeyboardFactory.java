package org.geogebra.keyboard.web.factory.model.inputbox;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.GEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

public class InputBoxDefaultFunctionKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
		float width = 5.0f / 3;

		RowImpl row = functionKeyboard.nextRow(9.2f);
		addTranslateInputCommandButton(row, buttonFactory, "sin", "sin", width);
		addTranslateInputCommandButton(row, buttonFactory, "cos", "cos", width);
		addTranslateInputCommandButton(row, buttonFactory, "tan", "tan", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^");
		addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^");
		addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot");
		addConstantInputButton(row, buttonFactory, Resource.ABS, "|");

		row = functionKeyboard.nextRow(9.2f);
		addTranslateInputCommandButton(row, buttonFactory, "asin",
				"altText.asin", "asin", width);
		addTranslateInputCommandButton(row, buttonFactory, "acos",
				"altText.acos", "acos", width);
		addTranslateInputCommandButton(row, buttonFactory, "atan",
				"altText.atan", "atan", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addInputButton(row, buttonFactory, "<");
		addInputButton(row, buttonFactory, ">");
		addInputButton(row, buttonFactory, LEQ);
		addInputButton(row, buttonFactory, GEQ);

		row = functionKeyboard.nextRow(9.2f);
		addTranslateInputCommandButton(row, buttonFactory, "sec", "sec", width);
		addTranslateInputCommandButton(row, buttonFactory, "csc", "csc", width);
		addTranslateInputCommandButton(row, buttonFactory, "cot", "cot", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addInputButton(row, buttonFactory, "%");
		addInputButton(row, buttonFactory, "!");
		addInputButton(row, buttonFactory, NOT_EQUAL_TO);
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);

		row = functionKeyboard.nextRow(9.2f);
		addInputButton(row, buttonFactory, "ln", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addConstantInputButton(row, buttonFactory, Resource.A_N, "a_n");
		addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

		return functionKeyboard;
	}
}
