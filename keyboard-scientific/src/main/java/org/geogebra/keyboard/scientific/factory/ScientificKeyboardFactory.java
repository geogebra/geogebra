package org.geogebra.keyboard.scientific.factory;

import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.model.impl.factory.DefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificDefaultKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificFunctionKeyboardFactory;
import org.geogebra.keyboard.scientific.model.ScientificLettersKeyboardFactory;

public final class ScientificKeyboardFactory extends DefaultKeyboardFactory {

    public ScientificKeyboardFactory() {
        mathKeyboardFactory = new ScientificDefaultKeyboardFactory(true);
        defaultKeyboardModelFactory = new ScientificDefaultKeyboardFactory(false);
        functionKeyboardFactory = new ScientificFunctionKeyboardFactory();
        letterKeyboardFactory = new ScientificLettersKeyboardFactory();
    }
}
