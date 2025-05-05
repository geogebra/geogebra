package org.geogebra.keyboard.base.model.impl.factory;

/**
 * Provides different characters for keyboard buttons and inputs.
 */
public interface CharacterProvider {

    /**
     * @return string for "x" button on the math keyboard
     */
    String xForButton();

    /**
     * @return input feedback for "x" button on the math keyboard
     */
    String xAsInput();

    /**
     * @return string for "x" button on the math keyboard
     */
    String yForButton();

    /**
     * @return input feedback for "y" button on the math keyboard
     */
    String yAsInput();

    /**
     * @return string for "x" button on the math keyboard
     */
    String zForButton();

    /**
     * @return input feedback for "z" button on the math keyboard
     */
    String zAsInput();

    /**
     * @return string for Euler "e" button on the math keyboard
     */
    String eulerForButton();

    /**
     * @return input feedback for "e" button on the math keyboard
     */
    String eulerAsInput();

    /**
     * @return string for "pi" button on the math keyboard
     */
    String piForButton();

    /**
     * @return input feedback for "pi" button on the math keyboard
     */
    String piAsInput();
}
