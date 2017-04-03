package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.LinearKeyboardImpl;
import org.geogebra.keyboard.base.linear.impl.RowImpl;

import static org.geogebra.keyboard.base.ButtonConstants.DIVISION;
import static org.geogebra.keyboard.base.ButtonConstants.EULER;
import static org.geogebra.keyboard.base.ButtonConstants.GEQ;
import static org.geogebra.keyboard.base.ButtonConstants.LEQ;
import static org.geogebra.keyboard.base.ButtonConstants.MULTIPLICATION;
import static org.geogebra.keyboard.base.ButtonConstants.PI;
import static org.geogebra.keyboard.base.ButtonConstants.ROOT;
import static org.geogebra.keyboard.base.ButtonConstants.SUP2;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.linear.impl.factory.Util.createEmptySpace;

class MathKeyboardFactory {

    LinearKeyboard createMathKeyboard() {
        LinearKeyboardImpl mathKeyboard = new LinearKeyboardImpl();

        RowImpl row = mathKeyboard.nextRow(9.2f);
        addInputButton(row, "x");
        addInputButton(row, "y");
        addInputButton(row, "z");
        addInputButton(row, PI);
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "7");
        addInputButton(row, "8");
        addInputButton(row, "9");
        addInputButton(row, MULTIPLICATION, "*");
        addInputButton(row, DIVISION, "/");

        row = mathKeyboard.nextRow(9.2f);
        addConstantInputButton(row, Resource.POWA2, SUP2);
        addConstantInputButton(row, Resource.POWAB, "^");
        addConstantInputButton(row, Resource.ROOT, ROOT);
        addInputButton(row, EULER);
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "4");
        addInputButton(row, "5");
        addInputButton(row, "6");
        addInputButton(row, "+");
        addInputButton(row, "-");

        row = mathKeyboard.nextRow(9.2f);
        addInputButton(row, "<");
        addInputButton(row, ">");
        addInputButton(row, LEQ);
        addInputButton(row, GEQ);
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "1");
        addInputButton(row, "2");
        addInputButton(row, "3");
        addInputButton(row, "=");
        addConstantCustomButton(row, Resource.BACKSPACE_DELETE, Action.BACKSPACE_DELETE);

        row = mathKeyboard.nextRow(9.2f);
        addInputButton(row, "(");
        addInputButton(row, ")");
        addInputButton(row, "|a|", "|");
        addInputButton(row, ",");
        addButton(row, createEmptySpace(0.2f));
        addInputButton(row, "0");
        addInputButton(row, ".");
        addConstantCustomButton(row, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return mathKeyboard;
    }
}
