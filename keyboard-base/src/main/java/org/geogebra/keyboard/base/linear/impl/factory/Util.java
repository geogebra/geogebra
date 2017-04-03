package org.geogebra.keyboard.base.linear.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.linear.WeightedButton;
import org.geogebra.keyboard.base.linear.impl.RowImpl;
import org.geogebra.keyboard.base.linear.impl.WeightedButtonImpl;

class Util {

    // Add buttons to rows

    static void addButton(RowImpl row, WeightedButton button) {
        row.addButton(button);
    }

    static void addCustomButton(RowImpl row, ButtonFactory buttonFactory, String resource, String action) {
        addButton(row, buttonFactory.createCustomButton(resource, action));
    }

    static void addCustomButton(RowImpl row, ButtonFactory buttonFactory, String resource, String action, Background background) {
        addButton(row, buttonFactory.createCustomButton(resource, action, background));
    }

    static void addInputButton(RowImpl row, ButtonFactory buttonFactory, String name, float weight) {
        addButton(row, buttonFactory.createInputButton(name, name, weight));
    }

    static void addInputButton(RowImpl row, ButtonFactory buttonFactory, String name, String input) {
        addButton(row, buttonFactory.createInputButton(name, input));
    }

    static void addInputButton(RowImpl row, ButtonFactory buttonFactory, String name) {
        addInputButton(row, buttonFactory, name, 1.0f);
    }

    static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory, String name, String action, float weight) {
        addButton(row, buttonFactory.createConstantInputButton(name, action, weight));
    }

    static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory, Resource resource, String action, float weight) {
        addButton(row, buttonFactory.createConstantInputButton(resource.name(), action, weight));
    }

    static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory, String name, String action) {
        addConstantInputButton(row, buttonFactory, name, action, 1.0f);
    }

    static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory, Resource resource, String action) {
        addConstantInputButton(row, buttonFactory, resource.name(), action, 1.0f);
    }

    static void addConstantCustomButton(RowImpl row, ButtonFactory buttonFactory, String resourceName, String action, float weight) {
        row.addButton(buttonFactory.createCustomConstantButton(resourceName, action, weight));
    }

    static void addConstantCustomButton(RowImpl row, ButtonFactory buttonFactory, Resource resource, Action action, float weight) {
        row.addButton(buttonFactory.createCustomConstantButton(resource.name(), action.name(), weight));
    }

    static void addConstantCustomButton(RowImpl row, ButtonFactory buttonFactory, String resourceName, String action) {
        addConstantCustomButton(row, buttonFactory, resourceName, action, 1.0f);
    }

    static void addConstantCustomButton(RowImpl row, ButtonFactory buttonFactory, Resource resource, Action action) {
        addConstantCustomButton(row, buttonFactory, resource.name(), action.name(), 1.0f);
    }

    static void addTranslateInputButton(RowImpl row, ButtonFactory buttonFactory, String translate, String input, float weight) {
        row.addButton(buttonFactory.createTranslateInputButton(translate, input, weight));
    }

    static void addTranslateInputCommandButton(RowImpl row, ButtonFactory buttonFactory, String translateName, String translateInput, float weight) {
        row.addButton(buttonFactory.createTranslateInputTranslateButton(translateName, translateInput, weight));
    }

    static void addInputCommandButton(RowImpl row, ButtonFactory buttonFactory, String name, String translateInput, float weight) {
        row.addButton(buttonFactory.createInputTranslateButton(name, translateInput, weight));
    }

    static void addConstantInputCommandButton(RowImpl row, ButtonFactory buttonFactory, String resource, String translateInput, float weight) {
        row.addButton(buttonFactory.createConstantInputTranslateButton(resource, translateInput, weight));
    }

    static void addConstantInputCommandButton(RowImpl row, ButtonFactory buttonFactory, Resource resource, String translateInput, float weight) {
        addConstantInputCommandButton(row, buttonFactory, resource.name(), translateInput, weight);
    }
}
