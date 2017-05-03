package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.*;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

public class SpecialSymbolsKeyboardFactory {

    KeyboardModel createSpecialSymbolsKeyboard(ButtonFactory buttonFactory) {
        KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();
        StringBuilder name = new StringBuilder();
        StringBuilder command = new StringBuilder();

        RowImpl row = mathKeyboard.nextRow(10.0f);
        addInputButton(row, buttonFactory, INFINITY);
        addInputButton(row, buttonFactory, QUESTIONED_EQUAL_TO);
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
        command.append(LEFT_FLOOR);
        command.append(RIGHT_FLOOR);
        addInputCommandButton(row, buttonFactory, name.toString(), command.toString(), 1.0f);

        name.setLength(0);
        command.setLength(0);
        name.append(LEFT_CEILING);
        name.append("x");
        name.append(RIGHT_CEILING);
        command.append(LEFT_CEILING);
        command.append(RIGHT_CEILING);
        addInputCommandButton(row, buttonFactory, name.toString(), command.toString(), 1.0f);

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
        addButton(row, buttonFactory.createEmptySpace(1.5f));

        row = mathKeyboard.nextRow(10.0f);

        name.setLength(0);
        name.append(HASHTAG);
        name.append(AMPERSAND);
        name.append(NOT_SIGN);
        addCustomButton(row, buttonFactory, name.toString(), Action.SWITCH_TO_ABC);
        addInputButton(row, buttonFactory, ",");
        addInputButton(row, buttonFactory, "'");
        addInputButton(row, buttonFactory, " ", 4.0f);
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return mathKeyboard;
    }
}
