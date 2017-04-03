package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.LinearKeyboardImpl;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import static org.geogebra.keyboard.base.ButtonConstants.DEGREE;
import static org.geogebra.keyboard.base.ButtonConstants.EULER;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addTranslateInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.createEmptySpace;

class FunctionKeyboardFactory {

    LinearKeyboard createFunctionKeyboard() {
        LinearKeyboardImpl functionKeyboard = new LinearKeyboardImpl();
        float width = 5.0f / 3;
        RowImpl row = functionKeyboard.nextRow(9.2f);
        addTranslateInputCommandButton(row, "Function.sin", "Function.sin", width);
        addTranslateInputCommandButton(row, "Function.cos", "Function.cos", width);
        addTranslateInputCommandButton(row, "Function.tan", "Function.tan", width);
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "%");
        addInputButton(row, "!");
        addInputButton(row, "$");
        addInputButton(row, DEGREE);

        row = functionKeyboard.nextRow(9.2f);
        addTranslateInputCommandButton(row, "Function.asin", "Function.asin", width);
        addTranslateInputCommandButton(row, "Function.acos", "Function.acos", width);
        addTranslateInputCommandButton(row, "Function.atan", "Function.atan", width);
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "{");
        addInputButton(row, "}");
        addInputButton(row, ";");
        addInputButton(row, ":=", "\u2254");

        row = functionKeyboard.nextRow(9.2f);
        addInputButton(row, "ln", width);
        addConstantInputButton(row, Resource.LOG_10, "log_{10}", width);
        addConstantInputButton(row, Resource.LOG_B, "logb", width);
        addButton(row, createEmptySpace(0.2f));
        addConstantInputCommandButton(row, Resource.LOG_10, "Derivative", 1.0f);
        addConstantInputCommandButton(row, Resource.INTEGRAL, "Integral", 1.0f);
        addInputButton(row, "i", "\u03af");
        addConstantCustomButton(row, Resource.BACKSPACE, Action.BACKSPACE);

        row = functionKeyboard.nextRow(9.2f);
        addConstantInputButton(row, Resource.POWE_X, EULER + "^", width);
        addConstantInputButton(row, Resource.POW10_X, "10^", width);
        addConstantInputButton(row, Resource.N_ROOT, "nroot", width);
        addButton(row, createEmptySpace(0.2f));
        addConstantInputButton(row, Resource.A_N, "a_n");
        addConstantCustomButton(row, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return functionKeyboard;
    }
}
