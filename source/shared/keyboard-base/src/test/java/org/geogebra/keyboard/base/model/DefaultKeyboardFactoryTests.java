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

package org.geogebra.keyboard.base.model;

import static org.junit.Assert.assertEquals;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.junit.Before;
import org.junit.Test;

public class DefaultKeyboardFactoryTests {
    private double precision = 1.0E-6;

    private KeyboardFactory keyboardFactory;

    @Before
    public void setup() {
        keyboardFactory = new DefaultKeyboardFactory();
    }

    @Test
    public void testMathKeyboardRowWeights() {
        Keyboard mathKeyboard = keyboardFactory.createMathKeyboard();

        for (Row row : mathKeyboard.getModel().getRows()) {
            assertEquals(9.2, row.getRowWeightSum(), precision);
        }
    }

    @Test
    public void testFunctionsKeyboardRowWeights() {
        Keyboard functionsKeyboard = keyboardFactory.createFunctionsKeyboard();

        for (Row row : functionsKeyboard.getModel().getRows()) {
            assertEquals(9.2, row.getRowWeightSum(), precision);
        }
    }

    @Test
    public void testLettersKeyboardRowWeights() {
        Keyboard lettersKeyboard = keyboardFactory.createLettersKeyboard(
                "qwertyuiop", "asdfghjkl", "zxcvbnm"
        );

        for (Row row : lettersKeyboard.getModel().getRows()) {
            assertEquals(10.0, row.getRowWeightSum(), precision);
        }
    }

    @Test
    public void testSpecialSymbolsKeyboardRowWeights() {
        Keyboard specialSymbolsKeyboard = keyboardFactory.createSpecialSymbolsKeyboard();

        for (Row row : specialSymbolsKeyboard.getModel().getRows()) {
            assertEquals(8.0, row.getRowWeightSum(), precision);
        }
    }
}
