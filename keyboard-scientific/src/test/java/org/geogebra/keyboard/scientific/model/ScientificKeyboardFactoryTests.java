package org.geogebra.keyboard.scientific.model;

import static org.junit.Assert.assertEquals;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.Row;
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

        for (Row row : mathKeyboard.getModel().getRows()) {
            assertEquals(9.2, row.getRowWeightSum(), precision);
        }
    }

    @Test
    public void testFunctionsKeyboardRowWeights() {
        Keyboard functionsKeyboard = keyboardFactory.createFunctionsKeyboard();

        for (Row row : functionsKeyboard.getModel().getRows()) {
            assertEquals(7.2, row.getRowWeightSum(), precision);
        }
    }

    @Test
    public void testLettersKeyboardRowWeights() {
        Keyboard lettersKeyboard = keyboardFactory.createLettersKeyboard(
                "abcdefghi", "jklmnopqr", "stuvwxyz"
        );

        for (Row row : lettersKeyboard.getModel().getRows()) {
            assertEquals(9.0, row.getRowWeightSum(), precision);
        }
    }
}
