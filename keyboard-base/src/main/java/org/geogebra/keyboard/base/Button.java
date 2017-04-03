package org.geogebra.keyboard.base;

import org.geogebra.keyboard.base.linear.KeyModifier;

/**
 * Describes a button that has an action and a look.
 */
public interface Button {

    /**
     * Name of the resources, depends on the resource type. See {@link #getResourceType()}.
     *
     * @return name of the resource
     */
    String getResourceName();

    /**
     * Type of the resource, specified by {@link #getResourceName()}.
     *
     * @return type of the resource
     */
    ResourceType getResourceType();

    /**
     * Name of the action, depends on the action type. See {@link #getActionType()}.
     *
     * @return name of the action
     */
    String getActionName();

    /**
     * Get the type of the action, specified by {@link #getActionName()}.
     *
     * @return type of the action
     */
    ActionType getActionType();

    /**
     * Get the type of background used on this button.
     *
     * @return the type of the background.
     */
    Background getBackground();
}
