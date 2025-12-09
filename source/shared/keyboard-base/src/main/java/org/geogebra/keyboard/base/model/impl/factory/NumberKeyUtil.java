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

import static org.geogebra.keyboard.base.model.impl.factory.Characters.DIVISION;
import static org.geogebra.keyboard.base.model.impl.factory.Characters.MULTIPLICATION;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addConstantCustomButton;
import static org.geogebra.keyboard.base.model.impl.factory.Util.addInputButton;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class NumberKeyUtil {

    /**
     * first row of number keyboard
     * @param row row
     * @param buttonFactory factory
     */
    public static void addFirstRow(RowImpl row, ButtonFactory buttonFactory) {
        addInputButton(row, buttonFactory, "7");
        addInputButton(row, buttonFactory, "8");
        addInputButton(row, buttonFactory, "9");
        addInputButton(row, buttonFactory, MULTIPLICATION, "*");
        addInputButton(row, buttonFactory, DIVISION, "/");
    }

    /**
     * second row of number keyboard
     * @param row row
     * @param buttonFactory factory
     */
    public static void addSecondRow(RowImpl row, ButtonFactory buttonFactory) {
        addInputButton(row, buttonFactory, "4");
        addInputButton(row, buttonFactory, "5");
        addInputButton(row, buttonFactory, "6");
        addInputButton(row, buttonFactory, "+");
        addInputButton(row, buttonFactory, "-");
    }

    /**
     * third row of number keyboard
     * @param row row
     * @param buttonFactory factory
     */
    public static void addThirdRow(RowImpl row, ButtonFactory buttonFactory) {
        addInputButton(row, buttonFactory, "1");
        addInputButton(row, buttonFactory, "2");
        addInputButton(row, buttonFactory, "3");
        addInputButton(row, buttonFactory, "=");
        addConstantCustomButton(row, buttonFactory, Resource.BACKSPACE_DELETE,
                Action.BACKSPACE_DELETE);
    }

    /**
     * fourth row of number keyboard
     * @param row row
     * @param buttonFactory factory
     */
    public static void addFourthRow(RowImpl row, ButtonFactory buttonFactory) {
        addInputButton(row, buttonFactory, "0");
        addInputButton(row, buttonFactory, ".");
        addConstantCustomButton(row, buttonFactory, Resource.LEFT_ARROW, Action.LEFT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RIGHT_ARROW, Action.RIGHT_CURSOR);
        addConstantCustomButton(row, buttonFactory, Resource.RETURN_ENTER, Action.RETURN_ENTER);
    }
}
