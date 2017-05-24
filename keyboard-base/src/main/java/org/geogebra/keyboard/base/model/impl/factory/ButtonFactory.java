package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.WeightedButtonImpl;

public class ButtonFactory {

    private KeyModifier[] modifiers;

    public ButtonFactory(KeyModifier[] modifiers) {
        this.modifiers = modifiers;
    }

    WeightedButton createLatexInputButton(String formula, String action, float weight) {
        return new WeightedButtonImpl(formula, ResourceType.LATEX, action, ActionType.INPUT, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createInputButton(String name, String action, float weight) {
        return new WeightedButtonImpl(name, ResourceType.TEXT, action, ActionType.INPUT, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createInputButton(String name, String action) {
        return createInputButton(name, action, 1.0f);
    }

    WeightedButton createConstantInputButton(String input, String action, float weight) {
        return new WeightedButtonImpl(input, ResourceType.DEFINED_CONSTANT, action, ActionType.INPUT, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createTranslateInputButton(String translate, String input, float weight) {
        return new WeightedButtonImpl(translate, ResourceType.TRANSLATION_MENU_KEY, input, ActionType.INPUT, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createTranslateInputTranslateButton(String translate, String input, float weight) {
        return new WeightedButtonImpl(translate, ResourceType.TRANSLATION_MENU_KEY, input, ActionType.INPUT_TRANSLATE_MENU, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createInputTranslateButton(String name, String translateInput, float weight) {
        return new WeightedButtonImpl(name, ResourceType.TEXT, translateInput, ActionType.INPUT_TRANSLATE_COMMAND, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createCustomConstantButton(String resource, String action, float weight) {
        return new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT, action, ActionType.CUSTOM, Background.FUNCTIONAL, modifiers, weight);
    }

    WeightedButton createConstantInputTranslateButton(String resource, String translateInput, float weight) {
        return new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT, translateInput, ActionType.INPUT_TRANSLATE_COMMAND, Background.STANDARD, modifiers, weight);
    }

    WeightedButton createCustomButton(String resource, String action) {
        return new WeightedButtonImpl(resource, ResourceType.TEXT, action, ActionType.CUSTOM, Background.FUNCTIONAL, modifiers, 1.0f);
    }

    WeightedButton createCustomButton(String resource, String action, Background background) {
        return new WeightedButtonImpl(resource, ResourceType.TEXT, action, ActionType.CUSTOM, background, modifiers, 1.0f);
    }

    WeightedButton createEmptySpace(float weight) {
        return new WeightedButtonImpl(Resource.EMPTY_IMAGE.name(), ResourceType.DEFINED_CONSTANT, Action.NONE.name(), ActionType.CUSTOM, Background.INVISIBLE, null, weight);
    }
}
