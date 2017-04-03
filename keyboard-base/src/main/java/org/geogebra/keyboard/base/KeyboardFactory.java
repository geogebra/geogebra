package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.impl.KeyboardImpl;
import org.geogebra.keyboard.base.linear.KeyModifier;
import org.geogebra.keyboard.base.linear.LinearKeyboard;
import org.geogebra.keyboard.base.linear.impl.AccentModifier;
import org.geogebra.keyboard.base.linear.impl.CapsLockModifier;
import org.geogebra.keyboard.base.linear.impl.factory.ButtonFactory;
import org.geogebra.keyboard.base.linear.impl.factory.LinearKeyboardFactory;

public class KeyboardFactory {

    private LinearKeyboardFactory linearKeyboardFactory = new LinearKeyboardFactory();
    private ButtonFactory defaultButtonFactory = new ButtonFactory(null);

    public Keyboard createMathKeyboard() {
        LinearKeyboard model = linearKeyboardFactory.createMathKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }

    public Keyboard createFunctionsKeyboard() {
        LinearKeyboard model = linearKeyboardFactory.createFunctionKeyboard(defaultButtonFactory);
        return new KeyboardImpl(model, null, null);
    }

    public Keyboard createGreekKeyboard() {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(new KeyModifier[] {accentModifier, capsLockModifier});
        LinearKeyboard model = linearKeyboardFactory.createGreekKeyboard(buttonFactory);
        return new KeyboardImpl(model, capsLockModifier, accentModifier);
    }

    public Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow) {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(new KeyModifier[] {accentModifier, capsLockModifier});
        LinearKeyboard model = linearKeyboardFactory.createLetterKeyboard(buttonFactory, topRow, middleRow, bottomRow);
        return new KeyboardImpl(model, capsLockModifier, accentModifier);
    }
}
