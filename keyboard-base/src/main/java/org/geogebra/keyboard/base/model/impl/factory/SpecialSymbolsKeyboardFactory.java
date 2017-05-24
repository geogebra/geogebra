package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.AMPERSAND;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ANGLE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.AT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.CIRCLED_TIMES;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.COLON;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ELEMENT_OF;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.HASHTAG;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.INFINITY;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_CEILING;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_FLOOR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_GUILLEMET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEFT_SQUARE_BRACKET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LOGICAL_AND;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LOGICAL_OR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_SIGN;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PARALLEL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.QUESTIONED_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.QUOTATION_MARK;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHTWARDS_ARROW;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHT_CEILING;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHT_FLOOR;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHT_GUILLEMET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.RIGHT_SQUARE_BRACKET;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUBSET_OF;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUBSET_OF_OR_EQUAL_TO;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.UP_TACK;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addLatexInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

class SpecialSymbolsKeyboardFactory {

    KeyboardModel createSpecialSymbolsKeyboard(ButtonFactory buttonFactory) {
        KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();
        StringBuilder name = new StringBuilder();

        RowImpl row = mathKeyboard.nextRow(10.0f);
        addInputButton(row, buttonFactory, INFINITY);
        addLatexInputButton(row, buttonFactory, "\\questeq", String.valueOf(QUESTIONED_EQUAL_TO));
        addInputButton(row, buttonFactory, NOT_EQUAL_TO);
        addInputButton(row, buttonFactory, LOGICAL_AND);
        addInputButton(row, buttonFactory, LOGICAL_OR);
        addInputButton(row, buttonFactory, RIGHTWARDS_ARROW);
        addInputButton(row, buttonFactory, NOT_SIGN);
        addInputButton(row, buttonFactory, CIRCLED_TIMES);
        addInputButton(row, buttonFactory, PARALLEL_TO);
        addInputButton(row, buttonFactory, UP_TACK);

        row = mathKeyboard.nextRow(10.0f);
        addButton(row, buttonFactory.createEmptySpace(0.5f));
        addInputButton(row, buttonFactory, ELEMENT_OF);
        addInputButton(row, buttonFactory, SUBSET_OF);
        addInputButton(row, buttonFactory, SUBSET_OF_OR_EQUAL_TO);
        addInputButton(row, buttonFactory, ANGLE);

        name.append(LEFT_FLOOR);
        name.append("x");
        name.append(RIGHT_FLOOR);
		addInputCommandButton(row, buttonFactory, name.toString(),
				LEFT_FLOOR + "", 1.0f);

        name.setLength(0);
        name.append(LEFT_CEILING);
        name.append("x");
        name.append(RIGHT_CEILING);
		addInputCommandButton(row, buttonFactory, name.toString(),
				LEFT_CEILING + "", 1.0f);

        addInputButton(row, buttonFactory, AMPERSAND);
        addInputButton(row, buttonFactory, AT);
        addInputButton(row, buttonFactory, HASHTAG);
        addButton(row, buttonFactory.createEmptySpace(0.5f));

        row = mathKeyboard.nextRow(10.0f);
        addButton(row, buttonFactory.createEmptySpace(1.5f));
        addInputButton(row, buttonFactory, LEFT_SQUARE_BRACKET);
        addInputButton(row, buttonFactory, RIGHT_SQUARE_BRACKET);
        addInputButton(row, buttonFactory, COLON);
        addInputButton(row, buttonFactory, QUOTATION_MARK);
        addTranslateInputCommandButton(row, buttonFactory, "Translate.currency", "Translate.currency", 1.0f);
        addInputButton(row, buttonFactory, LEFT_GUILLEMET);
        addInputButton(row, buttonFactory, RIGHT_GUILLEMET);
        addButton(row, buttonFactory.createEmptySpace(0.3f));
        addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE, Action.BACKSPACE_DELETE, 1.2f);

        row = mathKeyboard.nextRow(10.0f);

        addCustomButton(row, buttonFactory, "ABC", Action.SWITCH_TO_ABC);
        addInputButton(row, buttonFactory, ",");
        addInputButton(row, buttonFactory, "'");
        addInputButton(row, buttonFactory, " ", 4.0f);
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return mathKeyboard;
    }
}
