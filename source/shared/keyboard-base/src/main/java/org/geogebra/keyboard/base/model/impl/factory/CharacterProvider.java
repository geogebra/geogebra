/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
