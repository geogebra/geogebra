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
