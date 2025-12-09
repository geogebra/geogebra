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

package org.geogebra.keyboard.base;

/**
 * The type of the actions that a button can do.
 */
public enum ActionType {

    /**
     * The action is a string that the button inputs.
     */
    INPUT,

    /**
     * The action is a menu translation key, whose value is the input of the button.
     */
    INPUT_TRANSLATE_MENU,

    /**
     * The action is a command translation key, whose value is the input of the button.
     */
    INPUT_TRANSLATE_COMMAND,

    /**
     * The action is specified in the {@link Action} class.
     */
    CUSTOM
}
