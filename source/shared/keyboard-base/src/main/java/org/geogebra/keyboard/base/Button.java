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
     * Deprecated. Please use getPrimaryActionName()
     *
     * @return name of the action
     */
    @Deprecated
    String getActionName();

    /**
     * Get the type of the action, specified by {@link #getActionName()}.
     * Deprecated. Please use getPrimaryActionType()
     *
     * @return type of the action
     */
    @Deprecated
    ActionType getActionType();

    /**
     * Same as {@link #getActionName(int)} with index 0.
     *
     * @return first action name
     */
    String getPrimaryActionName();

    /**
     * Same as {@link #getActionType(int)} with index 0.
     *
     * @return first action type
     */
    ActionType getPrimaryActionType();

    /**
     * Name of the action, depends on the action type,
     * specified by {@link #getActionType(int)},
     * ordered by priority by index.
     *
     * @param index the order of the action, see {@link #getActionsSize()}
     * @return the action name for the specified index
     */
    String getActionName(int index);

    /**
     * Get the type of the action, specified by {@link #getActionName()},
     * ordered by priority by index.
     *
     * @param index the order of the action, see {@link #getActionsSize()}
     * @return the action name for index
     */
    ActionType getActionType(int index);

    /**
     * Get the number of actions for this button.
     * This is at least 1.
     *
     * @return the number of actions
     */
    int getActionsSize();

    /**
     * Adds another action for this button.
     *
     * @param actionName the name of the action
     * @param actionType the type of the action
     */
    void addAction(String actionName, ActionType actionType);

    /**
     * Get the type of background used on this button.
     *
     * @return the type of the background.
     */
    Background getBackground();
}
