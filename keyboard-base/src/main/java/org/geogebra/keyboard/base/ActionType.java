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
     * The action is a translation key, whose value is the input of the button.
     */
    INPUT_TRANSLATE_COMMAND,

    /**
     * The action is specified in the {@link ButtonConstants} class,
     * with the constants starting with <i><b>ACTION_</b></i>.
     */
    CUSTOM
}
