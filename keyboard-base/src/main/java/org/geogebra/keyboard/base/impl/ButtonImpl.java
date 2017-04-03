package org.geogebra.keyboard.base.impl;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Button;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.linear.KeyModifier;

public class ButtonImpl implements Button {

    private String resourceName;
    private ResourceType resourceType;

    private String actionName;
    private ActionType actionType;

    private Background background;

    private KeyModifier[] modifiers;

    public ButtonImpl(String resourceName, ResourceType resourceType,
                      String actionName, ActionType actionType,
                      Background background, KeyModifier[] modifiers) {
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.actionName = actionName;
        this.actionType = actionType;
        this.background = background;
        this.modifiers = modifiers;
    }

    @Override
    public String getResourceName() {
        if (modifiers != null) {
            String modifiedResourceName = resourceName;
            for (KeyModifier modifier : modifiers) {
                modifiedResourceName = modifier.modifyResourceName(modifiedResourceName, resourceType);
            }
            return modifiedResourceName;
        }
        return resourceName;
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public String getActionName() {
        if (modifiers != null) {
            String modifiedActionName = actionName;
            for (KeyModifier modifier : modifiers) {
                modifiedActionName = modifier.modifyActionName(modifiedActionName, actionType);
            }
            return modifiedActionName;
        }
        return actionName;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    public Background getBackground() {
        if (modifiers != null) {
            Background modifiedBackground = background;
            for (KeyModifier modifier : modifiers) {
                modifiedBackground = modifier.modifyBackground(modifiedBackground, actionType, actionName);
            }
            return modifiedBackground;
        }
        return background;
    }
}
