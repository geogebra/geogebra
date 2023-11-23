package org.geogebra.keyboard.scientific.model;

import static org.junit.Assert.assertEquals;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.scientific.factory.ScientificKeyboardFactory;
import org.junit.Before;
import org.junit.Test;

public class ScientificKeyboardFactoryTests {
    private final double precision = 1.0E-6;

    private KeyboardFactory keyboardFactory;

    @Before
    public void setup() {
        keyboardFactory = new ScientificKeyboardFactory();
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
                    7.2,
                    functionsKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }

    @Test
    public void testLettersKeyboardRowWeights() {
        Keyboard lettersKeyboard = keyboardFactory.createLettersKeyboard(
                "abcdefghi", "jklmnopqr", "stuvwxyz"
        );

        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            assertEquals(
                    9.0,
                    lettersKeyboard.getModel().getRows().get(rowIndex).getRowWeightSum(),
                    precision
            );
        }
    }
}
