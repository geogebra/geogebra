package org.geogebra.keyboard.base.model.impl;

import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ButtonConstants;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;

public class AccentModifier implements KeyModifier {

    private boolean graveAccent;
    private boolean circumflexAccent;
    private boolean caronAccent;
    private boolean acuteAccent;

    private Accents accents = new Accents();

    public boolean setAccent(String accent) {
        if (graveAccent || acuteAccent || caronAccent || circumflexAccent) {
            graveAccent = acuteAccent = caronAccent = circumflexAccent = false;
        }
        if (accent != null) {
            switch (accent) {
                case ButtonConstants.ACCENT_ACUTE:
                    acuteAccent = true;
                    break;
                case ButtonConstants.ACCENT_CARON:
                    caronAccent = true;
                    break;
                case ButtonConstants.ACCENT_CIRCUMFLEX:
                    circumflexAccent = true;
                    break;
                case ButtonConstants.ACCENT_GRAVE:
                    graveAccent = true;
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public String modifyResourceName(String resourceName, ResourceType resourceType) {
        if (resourceType == ResourceType.TEXT && resourceName.length() == 1) {
            return getAccent(resourceName);
        }
        return resourceName;
    }

    @Override
    public String modifyActionName(String actionName, ActionType actionType) {
        if (actionType == ActionType.INPUT && actionName.length() == 1) {
            return getAccent(actionName);
        }
        return actionName;
    }

    @Override
    public Background modifyBackground(Background background, ActionType actionType, String actionName) {
        if (actionType == ActionType.CUSTOM && (
                (actionName.equals(Action.TOGGLE_ACCENT_ACUTE.name()) && acuteAccent) ||
                (actionName.equals(Action.TOGGLE_ACCENT_CARON.name()) && caronAccent) ||
                (actionName.equals(Action.TOGGLE_ACCENT_GRAVE.name()) && graveAccent) ||
                (actionName.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name()) && circumflexAccent))) {
            return Background.STANDARD_PRESSED;
        }
        return background;
    }

    private String getAccent(String letter) {
        String returnValue = null;
        if (graveAccent) {
            returnValue = accents.getGraveAccent(letter);
        } else if (acuteAccent) {
            returnValue = accents.getAcuteLetter(letter);
        } else if (caronAccent) {
            returnValue = accents.getCaronLetter(letter);
        } else if (circumflexAccent) {
            returnValue = accents.getCircumflexLetter(letter);
        }
        return returnValue != null ? returnValue : letter;
    }
}
