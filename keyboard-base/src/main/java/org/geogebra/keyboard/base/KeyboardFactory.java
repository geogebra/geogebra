package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.impl.KeyboardImpl;
import org.geogebra.keyboard.base.linear.KeyModifier;
import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.AccentModifier;
import org.geogebra.keyboard.base.linear.impl.CapsLockModifier;
import org.geogebra.keyboard.base.linear.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.linear.impl.factory.LinearKeyboardFactory;

/**
 * Creates {@link Keyboard} classes.
 */
public class KeyboardFactory {

    private LinearKeyboardFactory linearKeyboardFactory = new LinearKeyboardFactory();
    private ButtonFactory defaultButtonFactory = new ButtonFactory(null);

    /**
     * Creates a math keyboard with numbers and operators.
     *
     * @return math keyboard
     */
    public Keyboard createMathKeyboard() {
        LinearKeyboard model = linearKeyboardFactory.createMathKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }

    /**
     * Creates a function keyboard with the function buttons.
     *
     * @return function keyboard
     */
    public Keyboard createFunctionsKeyboard() {
        LinearKeyboard model = linearKeyboardFactory.createFunctionKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }

    /**
     * Creates a greek keyboard with the greek letters and control buttons.
     *
     * @return greek keyboard
     */
    public Keyboard createGreekKeyboard() {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(new KeyModifier[] {accentModifier, capsLockModifier});
        LinearKeyboard model = linearKeyboardFactory.createGreekKeyboard(buttonFactory);
        return new KeyboardImpl(model, capsLockModifier, accentModifier);
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
    public Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow) {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(new KeyModifier[] {accentModifier, capsLockModifier});
        LinearKeyboard model = linearKeyboardFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
        return new KeyboardImpl(model, capsLockModifier, accentModifier);
    }
}
