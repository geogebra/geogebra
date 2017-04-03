package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;

/**
 * This class can create {@link LinearKeyboard}s of different types.
 * It is not thread safe.
 */
public class LinearKeyboardFactory {

    private MathKeyboardFactory mathKeyboardFactory;
    private GreekKeyboardFactory greekKeyboardFactory;
    private FunctionKeyboardFactory functionKeyboardFactory;
    private LetterKeyboardFactory letterKeyboardFactory;

    public LinearKeyboard createMathKeyboard(ButtonFactory buttonFactory) {
        if (mathKeyboardFactory == null) {
            mathKeyboardFactory = new MathKeyboardFactory();
        }
        return mathKeyboardFactory.createMathKeyboard(buttonFactory);
    }

    public LinearKeyboard createGreekKeyboard(ButtonFactory buttonFactory) {
        if (greekKeyboardFactory == null) {
            greekKeyboardFactory = new GreekKeyboardFactory();
        }
        return greekKeyboardFactory.createGreekKeyboard(buttonFactory);
    }

    public LinearKeyboard createFunctionKeyboard(ButtonFactory buttonFactory) {
        if (functionKeyboardFactory == null) {
            functionKeyboardFactory = new FunctionKeyboardFactory();
        }
        return functionKeyboardFactory.createFunctionKeyboard(buttonFactory);
    }

    public LinearKeyboard createLetterKeyboard(ButtonFactory buttonFactory, String topRow, String middleRow, String bottomRow) {
        if (letterKeyboardFactory == null) {
            letterKeyboardFactory = new LetterKeyboardFactory();
        }
        return letterKeyboardFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
    }
}
