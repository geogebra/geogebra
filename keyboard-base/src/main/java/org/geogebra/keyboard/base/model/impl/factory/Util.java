package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.RowImpl;

public class Util {

	// Add buttons to rows

	public static void addButton(RowImpl row, WeightedButton button) {
		row.addButton(button);
	}

	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, Action action) {
		addCustomButton(row, buttonFactory, resource, action.name());
	}

	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, String action) {
		addButton(row, buttonFactory.createCustomButton(resource, action));
	}

	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, String action, Background background) {
		addButton(row,
				buttonFactory.createCustomButton(resource, action, background));
	}

	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, float weight) {
		addButton(row, buttonFactory.createInputButton(name, name, weight));
	}

	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String input) {
		addButton(row, buttonFactory.createInputButton(name, input));
	}

	public static void addLatexInputButton(RowImpl row, ButtonFactory buttonFactory,
			String formula, String input) {
		addButton(row,
				buttonFactory.createLatexInputButton(formula, input, 1.0f));
	}

	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name) {
		addInputButton(row, buttonFactory, name, 1.0f);
	}

	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			char name) {
		addInputButton(row, buttonFactory, String.valueOf(name));
	}

	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String action, float weight) {
		addButton(row,
				buttonFactory.createConstantInputButton(name, action, weight));
	}

	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			Resource resource, String action, float weight) {
		addButton(row, buttonFactory.createConstantInputButton(resource.name(),
				action, weight));
	}

	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String action) {
		addConstantInputButton(row, buttonFactory, name, action, 1.0f);
	}

	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			Resource resource, String action) {
		addConstantInputButton(row, buttonFactory, resource.name(), action,
				1.0f);
	}

	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, String resourceName, String action,
			float weight) {
		row.addButton(buttonFactory.createCustomConstantButton(resourceName,
				action, weight));
	}

	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource, Action action,
			float weight) {
		row.addButton(buttonFactory.createCustomConstantButton(resource.name(),
				action.name(), weight));
	}

	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, String resourceName, String action) {
		addConstantCustomButton(row, buttonFactory, resourceName, action, 1.0f);
	}

	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource, Action action) {
		addConstantCustomButton(row, buttonFactory, resource.name(),
				action.name(), 1.0f);
	}

	public static void addTranslateInputButton(RowImpl row,
			ButtonFactory buttonFactory, String translate, String input,
			float weight) {
		row.addButton(buttonFactory.createTranslateInputButton(translate, input,
				weight));
	}

	public static void addTranslateInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, String translateName,
			String translateInput, float weight) {
		row.addButton(buttonFactory.createTranslateInputTranslateButton(
				translateName, translateInput, weight));
	}

	public static void addInputCommandButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String translateInput, float weight) {
		row.addButton(buttonFactory.createInputTranslateButton(name,
				translateInput, weight));
	}

	public static void addConstantInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, String resource, String translateInput,
			float weight) {
		row.addButton(buttonFactory.createConstantInputTranslateButton(resource,
				translateInput, weight));
	}

	public static void addConstantInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource,
			String translateInput, float weight) {
		addConstantInputCommandButton(row, buttonFactory, resource.name(),
				translateInput, weight);
	}
}
