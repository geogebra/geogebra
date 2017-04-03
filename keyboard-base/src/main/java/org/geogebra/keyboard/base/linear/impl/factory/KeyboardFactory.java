package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;

/**
 * This class can create {@link LinearKeyboard}s of different types.
 * It is not thread safe.
 */
public class KeyboardFactory {

    private MathKeyboardFactory mathKeyboardFactory;
    private GreekKeyboardFactory greekKeyboardFactory;
    private FunctionKeyboardFactory functionKeyboardFactory;
    private LetterKeyboardFactory letterKeyboardFactory;

    /**
     * Creates a math keyboard with numbers and operators.
     *
     * @return math keyboard
     */
    public LinearKeyboard createMathKeyboard() {
        if (mathKeyboardFactory == null) {
            mathKeyboardFactory = new MathKeyboardFactory();
        }
        return mathKeyboardFactory.createMathKeyboard();
    }

    /**
     * Creates a greek keyboard with the greek letters and control buttons.
     *
     * @return greek keyboard
     */
    public LinearKeyboard createGreekKeyboard() {
        if (greekKeyboardFactory == null) {
            greekKeyboardFactory = new GreekKeyboardFactory();
        }
        return greekKeyboardFactory.createGreekKeyboard();
    }

    /**
     * Creates a function keyboard with the function buttons.
     *
     * @return function keyboard
     */
    public LinearKeyboard createFunctionKeyboard() {
        if (functionKeyboardFactory == null) {
            functionKeyboardFactory = new FunctionKeyboardFactory();
        }
        return functionKeyboardFactory.createFunctionKeyboard();
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
    public LinearKeyboard createLetterKeyboard(String topRow, String middleRow, String bottomRow) {
        if (letterKeyboardFactory == null) {
            letterKeyboardFactory = new LetterKeyboardFactory();
        }
        return letterKeyboardFactory.createLetterKeyboard(topRow, middleRow, bottomRow);
    }
}
