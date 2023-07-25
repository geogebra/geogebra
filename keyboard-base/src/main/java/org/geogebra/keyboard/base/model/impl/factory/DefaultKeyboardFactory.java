package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.KeyboardFactory;

public class DefaultKeyboardFactory extends CommonKeyboardFactory {

    public static final KeyboardFactory INSTANCE = new DefaultKeyboardFactory();

    /**
     * Creates a KeyboardFactory with default implementations
     * for keyboard model factories.
     */
    private DefaultKeyboardFactory() {
        this(new DefaultCharProvider());
    }

    public DefaultKeyboardFactory(CharacterProvider characterProvider) {
        mathKeyboardFactory = new MathKeyboardFactory(characterProvider);
        defaultKeyboardModelFactory = new DefaultKeyboardModelFactory(characterProvider);
        greekKeyboardFactory = new GreekKeyboardFactory();
        functionKeyboardFactory = new FunctionKeyboardFactory();
        letterKeyboardFactory = new LetterKeyboardFactory();
        specialSymbolsKeyboardFactory = new SpecialSymbolsKeyboardFactory();
    }
}
