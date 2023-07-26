package org.geogebra.keyboard.base.model.impl.factory;

import java.util.Map;
import java.util.function.Supplier;

import org.geogebra.keyboard.base.Keyboard;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.keyboard.base.impl.KeyboardImpl;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.KeyboardModelFactory;
import org.geogebra.keyboard.base.model.impl.AccentModifier;
import org.geogebra.keyboard.base.model.impl.CapsLockModifier;

public class CommonKeyboardFactory implements KeyboardFactory {
    public static final KeyboardFactory INSTANCE = new CommonKeyboardFactory();
    ButtonFactory defaultButtonFactory = new ButtonFactory(null);
    protected KeyboardModelFactory mathKeyboardFactory;
    protected KeyboardModelFactory defaultKeyboardModelFactory;
    protected KeyboardModelFactory greekKeyboardFactory;
    protected KeyboardModelFactory functionKeyboardFactory;
    protected LetterKeyboardFactory letterKeyboardFactory;
    protected KeyboardModelFactory specialSymbolsKeyboardFactory;

    /**
     * Creates a CommonKeyboardFactory with default implementations
     * for keyboard model factories.
     */
    public CommonKeyboardFactory() {
        this(new DefaultCharProvider());
    }

    public CommonKeyboardFactory(CharacterProvider characterProvider) {
        defaultKeyboardModelFactory = new DefaultKeyboardModelFactory(characterProvider);
        mathKeyboardFactory = new MathKeyboardFactory(characterProvider);
        functionKeyboardFactory = new FunctionKeyboardFactory();
        letterKeyboardFactory = new LetterKeyboardFactory();
        greekKeyboardFactory = new GreekKeyboardFactory();
        specialSymbolsKeyboardFactory = new SpecialSymbolsKeyboardFactory();
    }

    @Override
    public Keyboard createMathKeyboard() {
        return getImpl(mathKeyboardFactory, KeyboardType.NUMBERS);
    }

    @Override
    public Keyboard createDefaultKeyboard() {
        return getImpl(defaultKeyboardModelFactory, KeyboardType.NUMBERS_DEFAULT);
    }

    @Override
    public Keyboard getImpl(KeyboardModelFactory modelFactory, KeyboardType type) {
        return new KeyboardImpl(
                type,
                () -> modelFactory.createKeyboardModel(defaultButtonFactory), null,
                null);
    }

    @Override
    public Keyboard createFunctionsKeyboard() {
        return getImpl(functionKeyboardFactory, KeyboardType.OPERATORS);
    }

    @Override
    public Keyboard createGreekKeyboard() {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier();
        ButtonFactory buttonFactory = new ButtonFactory(
                new KeyModifier[]{accentModifier, capsLockModifier});
        Supplier<KeyboardModel> model = () -> greekKeyboardFactory
                .createKeyboardModel(buttonFactory);
        return new KeyboardImpl(KeyboardType.GREEK, model, capsLockModifier, accentModifier);
    }

    @Override
    public Keyboard createLettersKeyboard(String topRow, String middleRow, String bottomRow,
                                          Map<String, String> upperKeys, boolean withGreekSwitch) {
        AccentModifier accentModifier = new AccentModifier();
        CapsLockModifier capsLockModifier = new CapsLockModifier(upperKeys);
        Supplier<KeyboardModel> model = () -> {
            ButtonFactory buttonFactory = new ButtonFactory(
                    new KeyModifier[]{accentModifier, capsLockModifier});
            letterKeyboardFactory.setUpperKeys(upperKeys);
            letterKeyboardFactory.setKeyboardDefinition(topRow, middleRow, bottomRow,
                    withGreekSwitch);
            return letterKeyboardFactory.createKeyboardModel(buttonFactory);
        };
        return new KeyboardImpl(KeyboardType.ABC, model, capsLockModifier, accentModifier);
    }

    @Override
    public Keyboard createLettersKeyboard(String topRow, String middleRow,
                                          String bottomRow, Map<String, String> upperKeys) {
        return createLettersKeyboard(topRow, middleRow, bottomRow, upperKeys, true);
    }

    @Override
    public Keyboard createLettersKeyboard(String topRow, String middleRow,
                                          String bottomRow) {
        return createLettersKeyboard(topRow, middleRow, bottomRow, null);
    }

    @Override
    public Keyboard createSpecialSymbolsKeyboard() {
        return getImpl(specialSymbolsKeyboardFactory, KeyboardType.SPECIAL);
    }
}
