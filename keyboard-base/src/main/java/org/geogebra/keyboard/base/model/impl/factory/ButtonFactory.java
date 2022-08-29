package org.geogebra.keyboard.base.model.impl.factory;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.geogebra.keyboard.base.model.impl.WeightedButtonImpl;

/**
 * Helper class for creating buttons with modifiers.
 */
public class ButtonFactory {

	private KeyModifier[] modifiers;

	/**
	 * Creates a ButtonFactory with key modifiers.
	 *
	 * @param modifiers modifiers of the keys. Can be null.
	 */
	public ButtonFactory(KeyModifier[] modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Creates an input button with string as resource.
	 *
	 * @param name
	 *            name of the button
	 * @param altText
	 *            description for screen reader
	 * @param input
	 *            the input from the button
	 * @param weight
	 *            weight of the button
	 *
	 * @return a button
	 */
	public WeightedButton createInputButton(String name, String altText,
			String input, float weight, boolean modify) {
		return new WeightedButtonImpl(name, ResourceType.TEXT, input,
				ActionType.INPUT, Background.STANDARD, modify ? modifiers : null, weight,
				altText);
	}

	/**
	 * Calls
	 * {@link ButtonFactory#createConstantInputButton(String, String, float)}
	 * with parameter {@code weight = 1.0f}
	 * 
	 * @param name
	 *            the name of the resource
	 * @param input
	 *            the input from the button
	 * @param altText
	 *            description of the button
	 *
	 * @return a button
	 */
	public WeightedButton createInputButton(String name, String altText,
			String input) {
		return createInputButton(name, altText, input, 1.0f, true);
	}

	/**
	 * Creates an input button with a defined constant a resource,
	 * see {@link Resource}.
	 *
	 * @param constant the name of the resource
	 * @param input the input from the button
	 * @param weight weight of the button
	 *
	 * @return a button
	 */
	public WeightedButton createConstantInputButton(String constant, String input,
			float weight) {
		return new WeightedButtonImpl(constant, ResourceType.DEFINED_CONSTANT,
				input, ActionType.INPUT, Background.STANDARD, modifiers,
				weight);
	}

	/**
	 * Creates a button that has a translated resource.
	 *
	 * @param translate the key for the translation for the resource
	 * @param input the input from the button
	 * @param weight weight of the button
	 *
	 * @return a button
	 */
	public WeightedButton createTranslateInputButton(String translate, String input,
			float weight) {
		return new WeightedButtonImpl(translate,
				ResourceType.TRANSLATION_MENU_KEY, input, ActionType.INPUT,
				Background.STANDARD, modifiers, weight);
	}

	/**
	 * Creates a button that has a translated resource and a translated input.
	 *
	 * @param translate the key for the translation for the resource
	 * @param input the key for the translation for the input
	 * @param weight weight of the button
	 *
	 * @return a button
	 */
	public WeightedButton createTranslateInputTranslateButton(String translate,
			String input, float weight) {
		return new WeightedButtonImpl(translate,
				ResourceType.TRANSLATION_MENU_KEY, input,
				ActionType.INPUT_TRANSLATE_MENU, Background.STANDARD, modifiers,
				weight);
	}

	/**
	 * Creates a button that has a translated resource and a translated input.
	 * 
	 * @param translate
	 *            the key for the translation for the resource
	 * @param altText
	 *            text for the screen reader
	 * @param input
	 *            the key for the translation for the input
	 * @param weight
	 *            weight of the button
	 * 
	 * @return a button
	 */
	public WeightedButton createTranslateInputTranslateButton(
			String translate, String altText, String input,
			float weight) {
		return new WeightedButtonImpl(translate,
				ResourceType.TRANSLATION_MENU_KEY, input, ActionType.INPUT_TRANSLATE_MENU,
				Background.STANDARD, modifiers, weight, altText);
	}

    /**
     * Creates a button that has a translated input.
     *
     * @param name name of the button
     * @param input the key for the translation for the input
     * @param weight weight of the button
     *
     * @return a button
     */
	public WeightedButton createInputTranslateButton(String name,
			String input, float weight) {
		return new WeightedButtonImpl(name, ResourceType.TEXT, input,
				ActionType.INPUT_TRANSLATE_COMMAND, Background.STANDARD,
				modifiers, weight);
	}

	/**
	 * Creates a button that has a custom resource and custom action.
	 * See {@link Action} and {@link Resource}
	 *
	 * @param resource name of the resource
	 * @param action the action of the key
	 * @param weight weight of the button
	 *
	 * @return a button
	 */
	public WeightedButton createCustomConstantButton(
			String resource, String action, float weight) {
		return new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT,
				action, ActionType.CUSTOM, Background.FUNCTIONAL, modifiers,
				weight);
	}

    /**
     * Creates a button that has a custom resource and translated input.
     * See {@link Resource}.
     *
     * @param resource name of the resource
     * @param input the key for the translation for the input
     * @param weight weight of the button
     *
     * @return a button
     */
	public WeightedButton createConstantInputTranslateButton(String resource,
			String input, float weight) {
		return new WeightedButtonImpl(resource, ResourceType.DEFINED_CONSTANT,
                input, ActionType.INPUT_TRANSLATE_COMMAND,
				Background.STANDARD, modifiers, weight);
	}

    /**
	 * Calls
	 * {@link ButtonFactory#createCustomButton(String, String, Background)} with
	 * parameter {@code background = }{@link Background#FUNCTIONAL}.
	 * 
	 * @param resource
	 *            resource name
	 * @param action
	 *            action
	 * @return button
	 */
	public WeightedButton createCustomButton(String resource, String action) {
	    return createCustomButton(resource, action, Background.FUNCTIONAL);
	}

    /**
     * Creates a button with a custom action and custom resource,
     * and with a background color.
     * See {@link Action}, {@link Resource} and {@link Background}.
     *
     * @param resource the name of the resource
     * @param action the name of the action
     * @param background the background
	 *
     * @return a button
     */
	public WeightedButton createCustomButton(String resource, String action,
			Background background) {
		return new WeightedButtonImpl(resource, ResourceType.TEXT, action,
				ActionType.CUSTOM, background, modifiers, 1.0f);
	}

    /**
     * Creates an empty space between buttons.
     *
     * @param weight the weight of the space
     *
     * @return a button that is invisible
     */
	public WeightedButton createEmptySpace(float weight) {
		return new WeightedButtonImpl(Resource.EMPTY_IMAGE.name(),
				ResourceType.DEFINED_CONSTANT, Action.NONE.name(),
				ActionType.CUSTOM, Background.INVISIBLE, null, weight);
	}

}
