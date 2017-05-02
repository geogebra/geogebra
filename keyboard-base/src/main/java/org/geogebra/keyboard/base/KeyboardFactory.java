package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.impl.KeyboardImpl;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;
import org.geogebra.keyboard.base.model.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.model.impl.factory.KeyboardModelFactory;

/**
 * Creates {@link Keyboard} classes.
 */
public class KeyboardFactory {

    private KeyboardModelFactory keyboardModelFactory = new KeyboardModelFactory();
    private ButtonFactory defaultButtonFactory = new ButtonFactory(null);

    /**
     * Creates a math keyboard with numbers and operators.
     *
     * @return math keyboard
     */
    public Keyboard createMathKeyboard() {
        KeyboardModel model = keyboardModelFactory.createMathKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }

    /**
     * Creates a function keyboard with the function buttons.
     *
     * @return function keyboard
     */
    public Keyboard createFunctionsKeyboard() {
        KeyboardModel model = keyboardModelFactory.createFunctionKeyboard(defaultButtonFactory);
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
        KeyboardModel model = keyboardModelFactory.createGreekKeyboard(buttonFactory);
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
     * @return letter keyboard
     */
    public Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow) {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(new KeyModifier[] {accentModifier, capsLockModifier});
        KeyboardModel model = keyboardModelFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
        return new KeyboardImpl(model, capsLockModifier, accentModifier);
    }

    /**
     * Creates a special symbols keyboard with symbols control buttons,
     * and a button to switch to the letters keyboard.
     *
     * @return special symbols keyboard
     */
    public Keyboard createSpecialSymbolsKeyboard() {
        KeyboardModel model = keyboardModelFactory.createSpecialSymbolsKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }
}
