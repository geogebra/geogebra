package org.geogebra.common.properties;

public interface ActionsEnumerableProperty extends IconsEnumerableProperty {

    /**
     * Returns the list of actions for the options in the property
     * @return array of callbacks
     */
    Runnable[] getActions();

}
