package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.model.KeyboardModel;

/**
 * This class can create {@link KeyboardModel}s of different types.
 * It is not thread safe.
 */
public class KeyboardModelFactory {

    private MathKeyboardFactory mathKeyboardFactory;
    private GreekKeyboardFactory greekKeyboardFactory;
    private FunctionKeyboardFactory functionKeyboardFactory;
    private LetterKeyboardFactory letterKeyboardFactory;

    public KeyboardModel createMathKeyboard(ButtonFactory buttonFactory) {
        if (mathKeyboardFactory == null) {
            mathKeyboardFactory = new MathKeyboardFactory();
        }
        return mathKeyboardFactory.createMathKeyboard(buttonFactory);
    }

    public KeyboardModel createGreekKeyboard(ButtonFactory buttonFactory) {
        if (greekKeyboardFactory == null) {
            greekKeyboardFactory = new GreekKeyboardFactory();
        }
        return greekKeyboardFactory.createGreekKeyboard(buttonFactory);
    }

    public KeyboardModel createFunctionKeyboard(ButtonFactory buttonFactory) {
        if (functionKeyboardFactory == null) {
            functionKeyboardFactory = new FunctionKeyboardFactory();
        }
        return functionKeyboardFactory.createFunctionKeyboard(buttonFactory);
    }

    public KeyboardModel createLetterKeyboard(ButtonFactory buttonFactory, String topRow, String middleRow, String bottomRow) {
        if (letterKeyboardFactory == null) {
            letterKeyboardFactory = new LetterKeyboardFactory();
        }
        return letterKeyboardFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
    }
}
