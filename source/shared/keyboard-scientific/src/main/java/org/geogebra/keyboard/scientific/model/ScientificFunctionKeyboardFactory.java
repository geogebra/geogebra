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

import static org.geogebra.keyboard.base.model.impl.factory.Characters.CURLY_EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;

public class ScientificFunctionKeyboardFactory implements KeyboardModelFactory {

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
        RowImpl row = functionKeyboard.nextRow();
        addInputButton(row, buttonFactory, "\u00B0");
        addInputButton(row, buttonFactory, "'");
        addInputButton(row, buttonFactory, "\u2033");
        addConstantInputButton(row, buttonFactory, Resource.A_N, "a_n");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addTranslateInputCommandButton(row, buttonFactory, "mean", "mean", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "stdev", "stdev", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "stdevp", "stdevp", 1.0f);

        row = functionKeyboard.nextRow();
        addTranslateInputCommandButton(row, buttonFactory, "asin", "asin", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "acos", "acos", 1.0f);
        addTranslateInputCommandButton(row, buttonFactory, "atan", "atan", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputCommandButton(row, buttonFactory, "\u207FP\u1D63", "nPr", 1.0f);
        addInputCommandButton(row, buttonFactory, "\u207FC\u1D63", "nCr", 1.0f);
        addInputButton(row, buttonFactory, "!");

        row = functionKeyboard.nextRow();
        addInputButton(row, buttonFactory, CURLY_EULER, EULER);
        addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^", 1.0f);
        addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot", 1.0f);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputCommandButton(row, buttonFactory, "rand", "random", 1.0f);
        addInputCommandButton(row, buttonFactory, "round", "round", 1.0f);
        addInputCommandButton(row, buttonFactory, "mad", "mad", 1.0f);

        row = functionKeyboard.nextRow();
        addInputButton(row, buttonFactory, "{");
        addInputButton(row, buttonFactory, "}");
        addConstantInputButton(row, buttonFactory, Resource.ABS, "|");
        addInputButton(row, buttonFactory, "%");
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        addSecondaryAction(functionKeyboard);

        return functionKeyboard;
    }

    private void addSecondaryAction(KeyboardModelImpl keyboardModel) {
        for (Row row : keyboardModel.getRows()) {
            for (WeightedButton button : row.getButtons()) {
                ActionType primaryAction = button.getPrimaryActionType();
                if (primaryAction == ActionType.INPUT_TRANSLATE_MENU
                        || primaryAction == ActionType.INPUT_TRANSLATE_COMMAND
                        || primaryAction == ActionType.INPUT
                        || (primaryAction == ActionType.CUSTOM
                            && button.getPrimaryActionName().equals(Action.RETURN_ENTER.name()))) {
                    button.addAction(Action.SWITCH_TO_123.toString(), ActionType.CUSTOM);
                }
            }
        }
    }
}
