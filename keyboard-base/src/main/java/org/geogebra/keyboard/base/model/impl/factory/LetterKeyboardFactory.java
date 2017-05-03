package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.AMPERSAND;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.HASHTAG;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.NOT_SIGN;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

class LetterKeyboardFactory {

    private static final double MIN_PADDING_WEIGHT = 1.e-4;
    private static final String EXCEPTION_MESSAGE = "Deformed keyboard definition with long bottom row.";

    KeyboardModel createLetterKeyboard(ButtonFactory buttonFactory, String topRow, String middleRow, String bottomRow) {
        int topRowLength = topRow.length();
        int middleRowLength = middleRow.length();
        int bottomRowLength = bottomRow.length();

        // sanity checks
        if (bottomRowLength > topRowLength - 1 && bottomRowLength > middleRowLength - 1) {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }

        int rowWeightSum;
        if (topRowLength == middleRowLength && middleRowLength - 1 == bottomRowLength) {
            rowWeightSum = middleRowLength + 1;
        } else {
            rowWeightSum = Math.max(topRowLength, middleRowLength);
        }

        float topRowPadding = (rowWeightSum - topRowLength) / 2.0f;
        float middleRowPadding = (rowWeightSum - middleRowLength) / 2.0f;

        float actionButtonSize;
        float actionButtonMargin;
        if (rowWeightSum - bottomRowLength == 2) {
            actionButtonSize = 1.0f;
            actionButtonMargin = 0.0f;
        } else if (rowWeightSum - bottomRowLength > 2) {
            actionButtonSize = 1.2f;
            actionButtonMargin = (rowWeightSum - bottomRowLength - 2.4f) / 2.0f;
        } else {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
        float spaceSize = rowWeightSum - 6;
        StringBuilder builder = new StringBuilder();

        KeyboardModelImpl letterKeyboard = new KeyboardModelImpl();

        createRow(letterKeyboard, buttonFactory, topRow, rowWeightSum, topRowPadding);
        createRow(letterKeyboard, buttonFactory, middleRow, rowWeightSum, middleRowPadding);

        RowImpl bottomRowImpl = letterKeyboard.nextRow(rowWeightSum);

        addConstantCustomButton(bottomRowImpl, buttonFactory, Resource.CAPS_LOCK, Action.CAPS_LOCK, actionButtonSize);
        addButton(bottomRowImpl, buttonFactory.createEmptySpace(actionButtonMargin));
        addButtons(bottomRowImpl, buttonFactory, bottomRow);
        addButton(bottomRowImpl, buttonFactory.createEmptySpace(actionButtonMargin));
        addConstantCustomButton(bottomRowImpl, buttonFactory, Resource.BACKSPACE_DELETE, Action.BACKSPACE_DELETE, actionButtonSize);

        RowImpl controlRow = letterKeyboard.nextRow(rowWeightSum);

        builder.append(HASHTAG);
        builder.append(AMPERSAND);
        builder.append(NOT_SIGN);
        addCustomButton(controlRow, buttonFactory, builder.toString(), Action.SWITCH_TO_SPECIAL_SYMBOLS);
        addInputButton(controlRow, buttonFactory, ",");
        addInputButton(controlRow, buttonFactory, "'");
        addInputButton(controlRow, buttonFactory, " ", spaceSize);
        addConstantCustomButton(controlRow, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(controlRow, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(controlRow, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return letterKeyboard;
    }

    private void createRow(KeyboardModelImpl keyboard, ButtonFactory buttonFactory, String definition, float rowWeightSum, float rowPadding) {
        RowImpl rowImpl = keyboard.nextRow(rowWeightSum);
        addPaddingIfNecessary(rowImpl, buttonFactory, rowPadding);
        addButtons(rowImpl, buttonFactory, definition);
        addPaddingIfNecessary(rowImpl, buttonFactory, rowPadding);
    }

    private void addButtons(RowImpl rowImpl, ButtonFactory buttonFactory, String definition) {
        for (int i = 0; i < definition.length(); i++) {
            addButtonCharacter(rowImpl, buttonFactory, definition.charAt(i));
        }
    }

    private void addButtonCharacter(RowImpl rowImpl, ButtonFactory buttonFactory, char character) {
        String resource = String.valueOf(character);
        switch (resource) {
            case Accents.ACCENT_ACUTE:
                addCustomButton(rowImpl, buttonFactory, resource, Action.TOGGLE_ACCENT_ACUTE.name(), Background.STANDARD);
                break;
            case Accents.ACCENT_CARON:
                addCustomButton(rowImpl, buttonFactory, resource, Action.TOGGLE_ACCENT_CARON.name(), Background.STANDARD);
                break;
            case Accents.ACCENT_CIRCUMFLEX:
                addCustomButton(rowImpl, buttonFactory, resource, Action.TOGGLE_ACCENT_CIRCUMFLEX.name(), Background.STANDARD);
                break;
            case Accents.ACCENT_GRAVE:
                addCustomButton(rowImpl, buttonFactory, resource, Action.TOGGLE_ACCENT_GRAVE.name(), Background.STANDARD);
                break;
            default:
                addInputButton(rowImpl, buttonFactory, resource);
        }
    }

    private void addPaddingIfNecessary(RowImpl rowImpl, ButtonFactory buttonFactory, float paddingWeight) {
        if (paddingWeight > MIN_PADDING_WEIGHT) {
            addButton(rowImpl, buttonFactory.createEmptySpace(paddingWeight));
        }
    }
}
