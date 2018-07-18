package org.geogebra.keyboard.scientific.model;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

public class ScientificFunctionKeyboardFactory implements KeyboardModelFactory {

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
        RowImpl row = functionKeyboard.nextRow(6.2f);
        addTranslateInputCommandButton(row, buttonFactory, "asin", "asin", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "acos", "acos", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "atan", "atan", 1.0f);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addTranslateInputCommandButton(row, buttonFactory, "mean", "mean", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "stdev", "stdev", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "stdevp", "stdevp", 1.0f);

        row = functionKeyboard.nextRow(6.2f);
        addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot", 1.0f);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputCommandButton(row, buttonFactory, "\u207FP\u1D63", "nPr", 1.0f);
        addInputCommandButton(row, buttonFactory, "\u207FC\u1D63", "BinomialCoefficient", 1.0f);
        addInputButton(row, buttonFactory, "!");

        row = functionKeyboard.nextRow(6.2f);
        addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb", 1.0f);
        addInputButton(row, buttonFactory, "abs", "|");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputCommandButton(row, buttonFactory, "rand", "rand", 1.0f);
        addInputCommandButton(row, buttonFactory, "round", "round", 1.0f);
        addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
                Action.BACKSPACE_DELETE);

        row = functionKeyboard.nextRow(6.2f);
        addCustomButton(row, buttonFactory, "ans", Action.ANS);
        addInputButton(row, buttonFactory, "{");
        addInputButton(row, buttonFactory, "}");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return functionKeyboard;
    }
}
