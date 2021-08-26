package org.geogebra.common.properties;

public interface ActionsEnumerableProperty extends Property {

    /**
     * Returns an array with the string values used for representing the actions
     *
     * @return array of captions
     */
    String[] getValues();

    /**
     * Returns an array with the icon resources. The identifiers are usually
     * tied to a specific property.
     *
     * @return an array of identifiers
     */
    PropertyResource[] getIcons();

    /**
     * Returns the array of actions to be executed for the options in the property
     *
     * @return array of callbacks
     */
    Runnable[] getActions();

}
