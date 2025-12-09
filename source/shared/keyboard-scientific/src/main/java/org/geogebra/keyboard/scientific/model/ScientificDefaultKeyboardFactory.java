/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.keyboard.scientific.model;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.CURLY_PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.DIVISION;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.MULTIPLICATION;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.PI;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.ROOT;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.SUP2;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

public class ScientificDefaultKeyboardFactory implements KeyboardModelFactory {

    private final boolean showAnsButton;

    public ScientificDefaultKeyboardFactory(boolean showAnsButton) {
        this.showAnsButton = showAnsButton;
    }

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl mathKeyboard = new KeyboardModelImpl();

        RowImpl row = mathKeyboard.nextRow();
        addConstantInputButton(row, buttonFactory, Resource.POWA2, SUP2);
        addConstantInputButton(row, buttonFactory, Resource.POWAB, "^");
        addConstantInputButton(row, buttonFactory, Resource.INVERSE, "x^(-1)");
        addConstantInputButton(row, buttonFactory, Resource.ROOT, ROOT);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "7");
        addInputButton(row, buttonFactory, "8");
        addInputButton(row, buttonFactory, "9");
        addInputButton(row, buttonFactory, MULTIPLICATION, "*");
        addInputButton(row, buttonFactory, DIVISION, DIVISION);

        row = mathKeyboard.nextRow();
        addTranslateInputCommandButton(row, buttonFactory, "sin", "sin", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "cos", "cos", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "tan", "tan", 1.0f);
        addInputButton(row, buttonFactory, CURLY_PI, PI);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "4");
        addInputButton(row, buttonFactory, "5");
        addInputButton(row, buttonFactory, "6");
        addInputButton(row, buttonFactory, "+");
        addInputButton(row, buttonFactory, "-");

        row = mathKeyboard.nextRow();
        addInputButton(row, buttonFactory, "ln", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}");
        addConstantInputButton(row, buttonFactory, Resource.FRACTION, "/");
        addConstantInputButton(row, buttonFactory, Resource.MIXED_NUMBER, "mixedNumber");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "1");
        addInputButton(row, buttonFactory, "2");
        addInputButton(row, buttonFactory, "3");
        addConstantInputButton(row, buttonFactory, Resource.RECURRING_DECIMAL, "recurringDecimal");
        addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
                Action.BACKSPACE_DELETE);

        row = mathKeyboard.nextRow();
        if (showAnsButton) {
            addCustomButton(row, buttonFactory, "ans", Action.ANS);
        } else {
            addConstantInputButton(row, buttonFactory, Resource.MIXED_NUMBER, "mixedNumber");
        }
        addInputButton(row, buttonFactory, ",");
        addInputButton(row, buttonFactory, "(");
        addInputButton(row, buttonFactory, ")");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "0");
        addInputButton(row, buttonFactory, ".");
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return mathKeyboard;
    }
}
