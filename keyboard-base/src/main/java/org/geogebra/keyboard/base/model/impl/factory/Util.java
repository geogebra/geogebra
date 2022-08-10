package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.RowImpl;

/**
 * Helper class for adding buttons to rows.
 */
public class Util {

	// Add buttons to rows

	/**
	 * Add the button to the row.
	 *
	 * @param row the row
	 * @param button the button to be added
	 */
	public static void addButton(RowImpl row, WeightedButton button) {
		row.addButton(button);
	}

	/**
	 * Calls {@link Util#addCustomButton(RowImpl, ButtonFactory, String, String)}
	 * with parameter {@code action = action.name()}.
	 */
	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, Action action) {
		addCustomButton(row, buttonFactory, resource, action.name());
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createCustomButton(String, String)}.
	 */
	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, String action) {
		addButton(row, buttonFactory.createCustomButton(resource, action));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createCustomButton(String, String, Background)}.
	 */
	public static void addCustomButton(RowImpl row, ButtonFactory buttonFactory,
			String resource, String action, Background background) {
		addButton(row,
				buttonFactory.createCustomButton(resource, action, background));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createInputButton(String, String, String, float, boolean)}.
	 */
	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, float weight) {
		addButton(row,
				buttonFactory.createInputButton(name, name, name, weight, true));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createInputButton(String, String, String)}.
	 */
	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String input) {
		addButton(row, buttonFactory.createInputButton(name, name, input));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createInputButton(String, String)}.
	 */
	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String input, String altText) {
		addButton(row, buttonFactory.createInputButton(name, altText, input));
	}

	/**
	 * Calls {@link Util#addInputButton(RowImpl, ButtonFactory, String, float)}
	 * with parameter {@code weight = 1.0f}.
	 */
	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name) {
		addInputButton(row, buttonFactory, name, 1.0f);
	}

	/**
	 * Calls {@link Util#addInputButton(RowImpl, ButtonFactory, String)}
	 * with parameter {@code name = String.valueOf(name)}.
	 */
	public static void addInputButton(RowImpl row, ButtonFactory buttonFactory,
			char name) {
		addInputButton(row, buttonFactory, String.valueOf(name));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createConstantInputButton(String, String, float)}.
	 */
	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String action, float weight) {
		addButton(row,
				buttonFactory.createConstantInputButton(name, action, weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createConstantInputButton(String, String, float)}.
	 */
	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			Resource resource, String action, float weight) {
		addButton(row, buttonFactory.createConstantInputButton(resource.name(),
				action, weight));
	}

	/**
	 * Calls {@link Util#addConstantInputButton(RowImpl, ButtonFactory, String, String, float)}
	 * with parameter {@code weight = 1.0f}.
	 */
	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String action) {
		addConstantInputButton(row, buttonFactory, name, action, 1.0f);
	}

	/**
	 * Calls {@link Util#addConstantInputButton(RowImpl, ButtonFactory, String, String, float)}
	 * with parameter {@code name = resource.name()} and {@code weight = 1.0f}.
	 */
	public static void addConstantInputButton(RowImpl row, ButtonFactory buttonFactory,
			Resource resource, String action) {
		addConstantInputButton(row, buttonFactory, resource.name(), action,
				1.0f);
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createCustomConstantButton(String, String, float)}.
	 */
	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, String resourceName, String action,
			float weight) {
		row.addButton(buttonFactory.createCustomConstantButton(resourceName,
				action, weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createCustomConstantButton(String, String, float)}.
	 */
	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource, Action action,
			float weight) {
		row.addButton(buttonFactory.createCustomConstantButton(resource.name(),
				action.name(), weight));
	}

	/**
	 * Calls {@link Util#addConstantCustomButton(RowImpl, ButtonFactory, String, String, float)}
	 * with parameter {@code weight = 1.0f}.
	 */
	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, String resourceName, String action) {
		addConstantCustomButton(row, buttonFactory, resourceName, action, 1.0f);
	}

	/**
	 * Calls {@link Util#addConstantCustomButton(RowImpl, ButtonFactory, String, String, float)}
	 * with parameter {@code resourceName = resource.name()},
	 * {@code action = action.name()} and {@code weight = 1.0f}.
	 */
	public static void addConstantCustomButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource, Action action) {
		addConstantCustomButton(row, buttonFactory, resource.name(),
				action.name(), 1.0f);
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createTranslateInputButton(String, String, float)}.
	 */
	public static void addTranslateInputButton(RowImpl row,
			ButtonFactory buttonFactory, String translate, String input,
			float weight) {
		row.addButton(buttonFactory.createTranslateInputButton(translate, input,
				weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createTranslateInputTranslateButton(String, String, float)}.
	 */
	public static void addTranslateInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, String translateName,
			String translateInput, float weight) {
		row.addButton(buttonFactory.createTranslateInputTranslateButton(
				translateName, translateInput, weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createTranslateInputTranslateButton(String, String, float)}
	 * .
	 */
	public static void addTranslateInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, String translateName, String altText,
			String translateInput, float weight) {
		row.addButton(buttonFactory.createTranslateInputTranslateButton(
				translateName, altText, translateInput, weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createInputTranslateButton(String, String, float)}.
	 */
	public static void addInputCommandButton(RowImpl row, ButtonFactory buttonFactory,
			String name, String translateInput, float weight) {
		row.addButton(buttonFactory.createInputTranslateButton(name,
				translateInput, weight));
	}

	/**
	 * Adds a button to the row created by
	 * {@link ButtonFactory#createConstantInputTranslateButton(String, String, float)}.
	 */
	public static void addConstantInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, String resource, String translateInput,
			float weight) {
		row.addButton(buttonFactory.createConstantInputTranslateButton(resource,
				translateInput, weight));
	}

	/**
	 * Calls {@link Util#addConstantInputCommandButton(
	 * RowImpl, ButtonFactory, String, String, float)}
	 * with parameter {@code resourceName = resource.name()}.
	 */
	public static void addConstantInputCommandButton(RowImpl row,
			ButtonFactory buttonFactory, Resource resource,
			String translateInput, float weight) {
		addConstantInputCommandButton(row, buttonFactory, resource.name(),
				translateInput, weight);
	}
}
