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

    /**
     * Creates a math keyboard with numbers and operators.
     *
     * @return math keyboard
     */
    public LinearKeyboard createMathKeyboard(ButtonFactory buttonFactory) {
        if (mathKeyboardFactory == null) {
            mathKeyboardFactory = new MathKeyboardFactory();
        }
        return mathKeyboardFactory.createMathKeyboard(buttonFactory);
    }

    /**
     * Creates a greek keyboard with the greek letters and control buttons.
     *
     * @return greek keyboard
     */
    public LinearKeyboard createGreekKeyboard(ButtonFactory buttonFactory) {
        if (greekKeyboardFactory == null) {
            greekKeyboardFactory = new GreekKeyboardFactory();
        }
        return greekKeyboardFactory.createGreekKeyboard(buttonFactory);
    }

    /**
     * Creates a function keyboard with the function buttons.
     *
     * @return function keyboard
     */
    public LinearKeyboard createFunctionKeyboard(ButtonFactory buttonFactory) {
        if (functionKeyboardFactory == null) {
            functionKeyboardFactory = new FunctionKeyboardFactory();
        }
        return functionKeyboardFactory.createFunctionKeyboard(buttonFactory);
    }

    /**
     * Creates a letter (or ABC) keyboard with letters on it. There is a restriction on the
     * row definitions that are passed as a String, namely
     * he bottom row has to be shorter than the top or middle row.
     * If the restrictions are not met, a {@link RuntimeException} is thrown.
     *
     * @param topRow    a list of characters that will be the buttons of the top row
     * @param middleRow a list of characters that will the buttons of the middle row
     * @param bottomRow a list of characters that will be the buttons of the last row
     * @return
     */
    public LinearKeyboard createLetterKeyboard(ButtonFactory buttonFactory, String topRow, String middleRow, String bottomRow) {
        if (letterKeyboardFactory == null) {
            letterKeyboardFactory = new LetterKeyboardFactory();
        }
        return letterKeyboardFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
    }
}
