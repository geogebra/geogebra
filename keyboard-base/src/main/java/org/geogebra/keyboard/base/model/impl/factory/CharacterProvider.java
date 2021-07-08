package org.geogebra.keyboard.base.model.impl.factory;

/**
 * Provides different characters for keyboard buttons and inputs.
 */
public interface CharacterProvider {

    String xForButton();

    String xAsInput();

    String yForButton();

    String yAsInput();

    String zForButton();

    String zAsInput();

    String eulerForButton();

    String eulerAsInput();

    String piForButton();

    String piAsInput();
}
