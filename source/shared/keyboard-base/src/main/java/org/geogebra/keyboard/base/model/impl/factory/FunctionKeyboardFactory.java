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

package org.geogebra.keyboard.base.model.impl.factory;

import static org.geogebra.keyboard.base.model.impl.factory.Characters.DEGREE;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.EULER;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.GEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.LEQ;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantInputCommandButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addTranslateInputCommandButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.KeyboardModelImpl;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class FunctionKeyboardFactory implements KeyboardModelFactory {

    @Override
    public KeyboardModel createKeyboardModel(ButtonFactory buttonFactory) {
        KeyboardModelImpl functionKeyboard = new KeyboardModelImpl();
        float width = 5.0f / 3;
        RowImpl row = functionKeyboard.nextRow();
        addTranslateInputCommandButton(row, buttonFactory, "sin", "sin", width);
        addTranslateInputCommandButton(row, buttonFactory, "cos", "cos", width);
        addTranslateInputCommandButton(row, buttonFactory, "tan", "tan", width);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "%");
        addInputButton(row, buttonFactory, "!");
        addInputButton(row, buttonFactory, "$");
        addInputButton(row, buttonFactory, DEGREE);

        row = functionKeyboard.nextRow();
        FunctionKeyUtil.addInverseSinCosTan(row, buttonFactory, width);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addInputButton(row, buttonFactory, "{");
        addInputButton(row, buttonFactory, "}");
        addInputButton(row, buttonFactory, LEQ);
        addInputButton(row, buttonFactory, GEQ);

        row = functionKeyboard.nextRow();
        addInputButton(row, buttonFactory, "ln", width);
        addConstantInputButton(row, buttonFactory, Resource.LOG_10, "log_{10}", width);
        addConstantInputButton(row, buttonFactory, Resource.LOG_B, "logb", width);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
		addConstantInputCommandButton(row, buttonFactory, Resource.DERIVATIVE, "Derivative", 1.0f);
        addConstantInputCommandButton(row, buttonFactory, Resource.INTEGRAL, "Integral", 1.0f);

		addInputButton(row, buttonFactory, Characters.imaginaryI, "\u03af", "altText.Imaginaryi");
		addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
				Action.BACKSPACE_DELETE);

        row = functionKeyboard.nextRow();
        addConstantInputButton(row, buttonFactory, Resource.POWE_X, EULER + "^", width);
        addConstantInputButton(row, buttonFactory, Resource.POW10_X, "10^", width);
        addConstantInputButton(row, buttonFactory, Resource.N_ROOT, "nroot", width);
        addButton(row, buttonFactory.createEmptySpace(0.2f));
        addConstantInputButton(row, buttonFactory, Resource.A_N, "a_n");
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);

        return functionKeyboard;
    }
}
