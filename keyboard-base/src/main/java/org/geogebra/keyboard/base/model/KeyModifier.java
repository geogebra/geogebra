package org.geogebra.keyboard.base.model;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ResourceType;

public interface KeyModifier {

    String modifyResourceName(String resourceName, ResourceType resourceType);

    String modifyActionName(String actionName, ActionType actionType);

    Background modifyBackground(Background background, ActionType actionType, String actionName);
}
