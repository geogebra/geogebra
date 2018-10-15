package org.geogebra.keyboard.base;

/**
 * Resource types that can be used.
 */
public enum ResourceType {

    /**
     * The resource is a label.
     */
    TEXT,

    /**
     * The resource is a translation key (menu table).
     */
    TRANSLATION_MENU_KEY,

    /**
     * The resource is a translation key (command table).
     */
    TRANSLATION_COMMAND_KEY,

    /**
     * The resource is specified in the {@link Resource} class.
     */
    DEFINED_CONSTANT,

}
