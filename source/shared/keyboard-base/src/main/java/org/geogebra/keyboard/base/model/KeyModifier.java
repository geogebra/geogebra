package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ResourceType;

/**
 * Modifies properties (resource, action, visual style) of a key.
 */
public interface KeyModifier {

    /**
     * @param resourceName original resource name
     * @param resourceType original resource type
     * @return new resource name
     */
    String modifyResourceName(String resourceName, ResourceType resourceType);

    /**
     * @param actionName original action name
     * @param actionType original action type
     * @return new action name
     */
    String modifyActionName(String actionName, ActionType actionType);

    /**
     * @param background original background
     * @param actionType original action type
     * @param actionName original action name
     * @return new background
     */
    Background modifyBackground(Background background, ActionType actionType, String actionName);
}
