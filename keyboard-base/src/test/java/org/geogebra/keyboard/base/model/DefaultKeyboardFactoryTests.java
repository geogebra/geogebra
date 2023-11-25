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
