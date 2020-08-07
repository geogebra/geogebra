package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.GEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFirstRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addFourthRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addSecondRow;
import static org.geogebra.keyboard.base.model.impl.factory.NumberKeyUtil.addThirdRow;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class MathKeyboardFactory implements KeyboardModelFactory {

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

        RowImpl row = mathKeyboard.nextRow(9.2f);
        addInputButton(row, buttonFactory, Characters.x, "x");
        addInputButton(row, buttonFactory, Characters.y, "y");
        addInputButton(row, buttonFactory, PI);
        addInputButton(row, buttonFactory, "e", EULER);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addFirstRow(row, buttonFactory);

        row = mathKeyboard.nextRow(9.2f);
        addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
        addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
        addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
        addConstantInputButton(row, buttonFactory, Resource.ABS, "|");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addSecondRow(row, buttonFactory);

        row = mathKeyboard.nextRow(9.2f);
        addInputButton(row, buttonFactory, "<");
        addInputButton(row, buttonFactory, ">");
        addInputButton(row, buttonFactory, LEQ);
        addInputButton(row, buttonFactory, GEQ);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addThirdRow(row, buttonFactory);

        row = mathKeyboard.nextRow(9.2f);
        addCustomButton(row, buttonFactory, "ans", Action.ANS);
        addInputButton(row, buttonFactory, ",");
        addInputButton(row, buttonFactory, "(");
        addInputButton(row, buttonFactory, ")");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addFourthRow(row, buttonFactory);

        return mathKeyboard;
    }
}
