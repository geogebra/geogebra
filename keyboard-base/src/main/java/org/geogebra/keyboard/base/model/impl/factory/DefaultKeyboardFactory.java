package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.KeyboardFactory;

public final class DefaultKeyboardFactory extends CommonKeyboardFactory {

    public static final KeyboardFactory INSTANCE = new DefaultKeyboardFactory();

    /**
     * Creates a DefaultKeyboardFactory with default implementations
     * for keyboard model factories.
     */
    private DefaultKeyboardFactory() {
        super();
    }
}
