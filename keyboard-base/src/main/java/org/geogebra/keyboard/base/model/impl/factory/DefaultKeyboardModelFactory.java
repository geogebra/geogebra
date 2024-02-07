package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFirstRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFourthRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addSecondRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addThirdRow;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class DefaultKeyboardModelFactory implements KeyboardModelFactory {

    private CharacterProvider charProvider;

    public DefaultKeyboardModelFactory(CharacterProvider characterProvider) {
        charProvider = characterProvider;
    }

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

        RowImpl row = mathKeyboard.nextRow();
        addInputButton(row, buttonFactory, charProvider.xForButton(), charProvider.xAsInput());
        addInputButton(row, buttonFactory, charProvider.yForButton(), charProvider.yAsInput());
        addInputButton(row, buttonFactory, charProvider.zForButton(), charProvider.zAsInput());
        addInputButton(row, buttonFactory, charProvider.piForButton(), charProvider.piAsInput());
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addFirstRow(row, buttonFactory);

        row = mathKeyboard.nextRow();
        addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
        addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
        addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
        addInputButton(
                row, buttonFactory, charProvider.eulerForButton(), charProvider.eulerAsInput());
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addSecondRow(row, buttonFactory);

        row = mathKeyboard.nextRow();
        addInputButton(row, buttonFactory, "<");
        addInputButton(row, buttonFactory, ">");
        addConstantInputButton(row, buttonFactory, Resource.RECURRING_DECIMAL, "recurringDecimal");
        addConstantInputButton(row, buttonFactory, Resource.MIXED_NUMBER, "mixedNumber");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addThirdRow(row, buttonFactory);

        row = mathKeyboard.nextRow();
        addInputButton(row, buttonFactory, "(");
        addInputButton(row, buttonFactory, ")");
        addConstantInputButton(row, buttonFactory, Resource.ABS, "|");
        addInputButton(row, buttonFactory, ",");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addFourthRow(row, buttonFactory);

        return mathKeyboard;
    }
}