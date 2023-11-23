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

        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            assertEquals(
                    9.2,
                    mathKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }

    @Test
    public void testFunctionsKeyboardRowWeights() {
        Keyboard functionsKeyboard = keyboardFactory.createFunctionsKeyboard();

        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            assertEquals(
                    9.2,
                    functionsKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }

    @Test
    public void testLettersKeyboardRowWeights() {
        Keyboard lettersKeyboard = keyboardFactory.createLettersKeyboard(
                "qwertyuiop", "asdfghjkl", "zxcvbnm"
        );

        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            assertEquals(
                    10.0,
                    lettersKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }

    @Test
    public void testSpecialSymbolsKeyboardRowWeights() {
        Keyboard specialSymbolsKeyboard = keyboardFactory.createSpecialSymbolsKeyboard();

        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            assertEquals(
                    8.0,
                    specialSymbolsKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }
}
