package org.geogebra.keyboard.base.impl;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Button;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;

public class ButtonImpl implements Button {

    private String resourceName;
    private ResourceType resourceType;

    private String actionName;
    private ActionType actionType;

    public ButtonImpl(Resource resourceName, ResourceType resourceType, Action actionName, ActionType actionType) {
        this(resourceName.name(), resourceType, actionName.name(), actionType);
    }

    public ButtonImpl(String resourceName, ResourceType resourceType, String actionName, ActionType actionType) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.actionName = actionName;
        this.actionType = actionType;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }
}
