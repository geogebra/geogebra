package org.geogebra.keyboard.web.factory.model.inputbox.defaultKeyboard;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.AMPERSAND;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ANGLE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.AT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.CIRCLED_TIMES;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.COLON;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.COMMA;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ELEMENT_OF;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.HASHTAG;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.INFINITY;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_CEILING;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_FLOOR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_SQUARE_BRACKET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LOGICAL_AND;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LOGICAL_OR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.MINUTES;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_SIGN;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PARALLEL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PERPENDICULAR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.QUESTIONED_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHTWARDS_ARROW;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHT_SQUARE_BRACKET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SECONDS;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SEMICOLON;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUBSET_OF;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUBSET_OF_OR_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
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

public class InputBoxDefaultSymbolsKeyboardFactory implements KeyboardModelFactory {

	@Override
	public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
		KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

		RowImpl row = mathKeyboard.nextRow(8.0f);
		addInputButton(row, buttonFactory, INFINITY);
		addInputButton(row, buttonFactory, String.valueOf(QUESTIONED_EQUAL_TO));
		addInputButton(row, buttonFactory, NOT_EQUAL_TO);
		addInputButton(row, buttonFactory, LOGICAL_AND);
		addInputButton(row, buttonFactory, LOGICAL_OR);
		addInputButton(row, buttonFactory, RIGHTWARDS_ARROW);
		addInputButton(row, buttonFactory, NOT_SIGN);
		addInputButton(row, buttonFactory, CIRCLED_TIMES);

		row = mathKeyboard.nextRow(8.0f);
		addInputButton(row, buttonFactory, PARALLEL_TO);
		addInputButton(row, buttonFactory, PERPENDICULAR);
		addInputButton(row, buttonFactory, ELEMENT_OF);
		addInputButton(row, buttonFactory, SUBSET_OF);
		addInputButton(row, buttonFactory, SUBSET_OF_OR_EQUAL_TO);
		addInputButton(row, buttonFactory, ANGLE);
		addConstantInputCommandButton(row, buttonFactory, Resource.FLOOR,
				LEFT_FLOOR + "", 1.0f);
		addConstantInputCommandButton(row, buttonFactory, Resource.CEIL,
				LEFT_CEILING + "", 1.0f);

		row = mathKeyboard.nextRow(8.0f);
		addInputButton(row, buttonFactory, LEFT_SQUARE_BRACKET);
		addInputButton(row, buttonFactory, RIGHT_SQUARE_BRACKET);
		addInputButton(row, buttonFactory, COLON);
		addInputButton(row, buttonFactory, AMPERSAND);
		addInputButton(row, buttonFactory, AT);
		addInputButton(row, buttonFactory, HASHTAG);
		addTranslateInputCommandButton(row, buttonFactory, "Translate.currency",
				"Translate.currency", 1.0f);
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);

		row = mathKeyboard.nextRow(8.0f);
		addInputButton(row, buttonFactory, COMMA);
		addInputButton(row, buttonFactory, SEMICOLON);
		addInputButton(row, buttonFactory, ":=", "\u2254");
		addInputButton(row, buttonFactory, MINUTES);
		addInputButton(row, buttonFactory, SECONDS);
		addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW,
				Action.LEFT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW,
				Action.RIGHT_CURSOR);
		addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER,
				Action.RETURN_ENTER);

		return mathKeyboard;
	}
}
