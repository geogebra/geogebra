package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.linear.WeightedButton;
import org.geogebra.keyboard.base.linear.impl.RowImpl;
import org.geogebra.keyboard.base.linear.impl.WeightedButtonImpl;

import static org.geogebra.keyboard.base.ButtonConstants.ACTION_NONE;
import static org.geogebra.keyboard.base.ButtonConstants.RESOURCE_EMPTY;

class Util {

    // Create Button

    static WeightedButton createInputButton(String name, String action, float weight) {
        return new WeightedButtonImpl(name, ResourceType.TEXT, action, ActionType.INPUT, weight);
    }

    static WeightedButton createInputButton(String name, String action) {
        return createInputButton(name, action, 1.0f);
    }

    static WeightedButton createInputButton(String name) {
        return createInputButton(name, name);
    }

    static WeightedButton createConstantInputButton(String input, String action, float weight) {
        return new WeightedButtonImpl(input, ResourceType.DEFINED_CONSTANT, action, ActionType.INPUT, weight);
    }

    static WeightedButton createTranslateInputButton(String translate, String input, float weight) {
        return new WeightedButtonImpl(translate, ResourceType.TRANSLATION_KEY, input, ActionType.INPUT, weight);
    }

    static WeightedButton createCustomConstantButton(String resource, String action, float weight) {
        return new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT, action, ActionType.CUSTOM, weight);
    }

    static WeightedButton createCustomButton(String resource, String action) {
        return new WeightedButtonImpl(resource, ResourceType.TEXT, action, ActionType.CUSTOM, 1.0f);
    }

    static WeightedButton createEmptySpace(float weight) {
        return new WeightedButtonImpl(RESOURCE_EMPTY, ResourceType.DEFINED_CONSTANT, ACTION_NONE, ActionType.CUSTOM, weight);
    }

    // Add buttons to rows

    static void addButton(RowImpl row, WeightedButton button) {
        row.addButton(button);
    }

    static void addCustomButton(RowImpl row, String resource, String action) {
        addButton(row, createCustomButton(resource, action));
    }

    static void addInputButton(RowImpl row, String name, float weight) {
        addButton(row, createInputButton(name, name, weight));
    }

    static void addInputButton(RowImpl row, String name, String input) {
        addButton(row, createInputButton(name, input));
    }

    static void addInputButton(RowImpl row, String name) {
        addInputButton(row, name, 1.0f);
    }

    static void addConstantInputButton(RowImpl row, String name, String action, float weight) {
        addButton(row, createConstantInputButton(name, action, weight));
    }

    static void addConstantInputButton(RowImpl row, String name, String action) {
        addConstantInputButton(row, name, action, 1.0f);
    }

    static void addConstantCustomButton(RowImpl row, String resourceName, String action) {
        row.addButton(createCustomConstantButton(resourceName, action, 1.0f));
    }

    static void addTranslateInputButton(RowImpl row, String translate, String input, float weight) {
        row.addButton(createTranslateInputButton(translate, input, weight));
    }

    static void addTranslateInputCommandButton(RowImpl row, String translateName, String translateInput, float weight) {
        row.addButton(new WeightedButtonImpl(translateName, ResourceType.TRANSLATION_KEY, translateInput, ActionType.INPUT_TRANSLATE_COMMAND, weight));
    }

    static void addInputCommandButton(RowImpl row, String name, String translateInput, float weight) {
        row.addButton(new WeightedButtonImpl(name, ResourceType.TEXT, translateInput, ActionType.INPUT_TRANSLATE_COMMAND, weight));
    }

    static void addConstantInputCommandButton(RowImpl row, String resource, String translateInput, float weight) {
        row.addButton(new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT, translateInput, ActionType.INPUT_TRANSLATE_COMMAND, weight));
    }
}
