package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.linear.LinearKeyboard;

public class KeyboardFactory {

    private MathKeyboardFactory mathKeyboardFactory;
    private GreekKeyboardFactory greekKeyboardFactory;
    private FunctionKeyboardFactory functionKeyboardFactory;
    private LetterKeyboardFactory letterKeyboardFactory;

    public LinearKeyboard createMathKeyboard() {
        if (mathKeyboardFactory == null) {
            mathKeyboardFactory = new MathKeyboardFactory();
        }
        return mathKeyboardFactory.createMathKeyboard();
    }

    public LinearKeyboard createGreekKeyboard() {
        if (greekKeyboardFactory == null) {
            greekKeyboardFactory = new GreekKeyboardFactory();
        }
        return greekKeyboardFactory.createGreekKeyboard();
    }

    public LinearKeyboard createFunctionKeyboard() {
        if (functionKeyboardFactory == null) {
            functionKeyboardFactory = new FunctionKeyboardFactory();
        }
        return functionKeyboardFactory.createFunctionKeyboard();
    }

    public LinearKeyboard createLetterKeyboard(String topRow, String middleRow, String bottomRow) {
        if (letterKeyboardFactory == null) {
            letterKeyboardFactory = new LetterKeyboardFactory();
        }
        return letterKeyboardFactory.createLetterKeyboard(topRow, middleRow, bottomRow);
    }
}
