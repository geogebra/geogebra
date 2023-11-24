package org.geogebra.keyboard.web.factory.model.solver;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.CURLY_PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SEMICOLON;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.Characters;
import org.geogebra.keyboard.base.model.impl.factory.FunctionKeyUtil;

public class SolverFunctionKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
		float width = 5.0f / 3;
		RowImpl row = functionKeyboard.nextRow();
		addTranslateInputCommandButton(row, buttonFactory, "sin", "sin", width);
		addTranslateInputCommandButton(row, buttonFactory, "cos", "cos", width);
		addTranslateInputCommandButton(row, buttonFactory, "tan", "tan", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addInputButton(row, buttonFactory, "%");
		addInputButton(row, buttonFactory, "!");
		addInputButton(row, buttonFactory, CURLY_PI, PI);
		addInputButton(row, buttonFactory, DEGREE);

		row = functionKeyboard.nextRow();
		FunctionKeyUtil.addInverseSinCosTan(row, buttonFactory, width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addInputButton(row, buttonFactory, "{");
		addInputButton(row, buttonFactory, "}");
		addInputButton(row, buttonFactory, SEMICOLON);
		addInputButton(row, buttonFactory, ":=", "\u2254");

		row = functionKeyboard.nextRow();
		addInputButton(row, buttonFactory, "ln", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}", width);
		addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addConstantInputCommandButton(row, buttonFactory, Resource.DERIVATIVE, "Derivative", 1.0f);
		addConstantInputCommandButton(row, buttonFactory, Resource.INTEGRAL, "Integral", 1.0f);
		addInputButton(row, buttonFactory, Characters.imaginaryI, "\u03af", "altText.Imaginaryi");
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);

		row = functionKeyboard.nextRow();
		addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^", width);
		addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^", width);
		addConstantInputButton(row, buttonFactory, Resource.RECURRING_DECIMAL,
				"recurringDecimal", width);
		addButton(row, buttonFactory.createEmptySpace(0.2f));
		addConstantInputButton(row, buttonFactory, Resource.A_N, "a_n");
		addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

		return functionKeyboard;
	}
}

