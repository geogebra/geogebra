package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.LinearKeyboardImpl;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import static org.geogebra.keyboard.base.ButtonConstants.ACTION_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_LEFT;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.ACTION_RIGHT;
import static org.geogebra.keyboard.base.ButtonConstants.DEGREE;
import static org.geogebra.keyboard.base.ButtonConstants.INTEGRAL;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_10_X;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_A_N;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_BACKSPACE;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_E_X;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LEFT_ARROW;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LOG_10;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_LOG_B;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_N_ROOT;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RETURN;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_RIGHT_ARROW;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addTranslateInputCommandButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.createEmptySpace;

public class FunctionKeyboardFactory {

    public LinearKeyboard createFunctionKeyboard() {
        LinearKeyboardImpl functionKeyboard = new LinearKeyboardImpl();

        RowImpl row = functionKeyboard.nextRow(8.8f);
        addTranslateInputCommandButton(row, "Function.sin", "Function.sin", 1.4f);
        addTranslateInputCommandButton(row, "Function.cos", "Function.cos", 1.4f);
        addTranslateInputCommandButton(row, "Function.tan", "Function.tan", 1.4f);
        addButton(row, createEmptySpace(0.6f));
        addInputButton(row, "%");
        addInputButton(row, "!");
        addInputButton(row, "$");
        addInputButton(row, DEGREE);

        row = functionKeyboard.nextRow(8.8f);
        addTranslateInputCommandButton(row, "Function.asin", "Function.asin", 1.4f);
        addTranslateInputCommandButton(row, "Function.acos", "Function.acos", 1.4f);
        addTranslateInputCommandButton(row, "Function.atan", "Function.atan", 1.4f);
        addButton(row, createEmptySpace(0.6f));
        addInputButton(row, "{");
        addInputButton(row, "}");
        addInputButton(row, ";");
        addInputButton(row, ":=");

        row = functionKeyboard.nextRow(8.8f);
        addInputButton(row, "ln", 1.4f);
        addConstantInputButton(row, RESOURCE_LOG_10, "log_{10}", 1.4f);
        addConstantInputButton(row, RESOURCE_LOG_B, "logb", 1.4f);
        addButton(row, createEmptySpace(0.6f));
        addInputCommandButton(row, "d/dx", "Derivative", 1.0f);
        addInputCommandButton(row, INTEGRAL, "Integral", 1.0f);
        addInputButton(row, "i");
        addConstantCustomButton(row, RESOURCE_BACKSPACE, ACTION_BACKSPACE);

        row = functionKeyboard.nextRow(8.8f);
        addConstantInputButton(row, RESOURCE_E_X, "e^x", 1.4f);
        addConstantInputButton(row, RESOURCE_10_X, "10^x", 1.4f);
        addConstantInputButton(row, RESOURCE_N_ROOT, "nroot", 1.4f);
        addButton(row, createEmptySpace(0.6f));
        addConstantInputButton(row, RESOURCE_A_N, "a_n");
        addConstantCustomButton(row, RESOURCE_LEFT_ARROW, ACTION_LEFT);
        addConstantCustomButton(row, RESOURCE_RIGHT_ARROW, ACTION_RIGHT);
        addConstantCustomButton(row, RESOURCE_RETURN, ACTION_RETURN);

        return functionKeyboard;
    }
}
