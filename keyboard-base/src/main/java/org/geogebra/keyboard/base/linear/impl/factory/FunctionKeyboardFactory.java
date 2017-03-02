package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.LinearKeyboardImpl;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import static org.geogebra.keyboard.base.ButtonConstants.ACTION_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_LEFT;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RIGHT;
import static org.geogebra.keyboard.base.ButtonConstants.DEGREE;
import static org.geogebra.keyboard.base.ButtonConstants.EULER;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_10_X;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_A_N;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_E_X;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_INTEGRAL;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LEFT_ARROW;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LOG_10;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LOG_B;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_N_ROOT;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RIGHT_ARROW;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addTranslateInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.createEmptySpace;

public class FunctionKeyboardFactory {

    public LinearKeyboard createFunctionKeyboard() {
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
        addConstantInputButton(row, RESOURCE_LOG_10, "log_{10}", width);
        addConstantInputButton(row, RESOURCE_LOG_B, "logb", width);
        addButton(row, createEmptySpace(0.2f));
        addInputCommandButton(row, "d/dx", "Derivative", 1.0f);
        addConstantInputCommandButton(row, RESOURCE_INTEGRAL, "Integral", 1.0f);
        addInputButton(row, "i", "\u03af");
        addConstantCustomButton(row, RESOURCE_BACKSPACE, ACTION_BACKSPACE);

        row = functionKeyboard.nextRow(9.2f);
        addConstantInputButton(row, RESOURCE_E_X, EULER + "^", width);
        addConstantInputButton(row, RESOURCE_10_X, "10^", width);
        addConstantInputButton(row, RESOURCE_N_ROOT, "nroot", width);
        addButton(row, createEmptySpace(0.2f));
        addConstantInputButton(row, RESOURCE_A_N, "a_n");
        addConstantCustomButton(row, RESOURCE_LEFT_ARROW, ACTION_LEFT);
        addConstantCustomButton(row, RESOURCE_RIGHT_ARROW, ACTION_RIGHT);
        addConstantCustomButton(row, RESOURCE_RETURN, ACTION_RETURN);

        return functionKeyboard;
    }
}
