package org.geogebra.keyboard.base.model.impl;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.impl.ButtonImpl;
import org.geogebra.keyboard.base.model.KeyModifier;
import org.geogebra.keyboard.base.model.WeightedButton;

public class WeightedButtonImpl extends ButtonImpl implements WeightedButton {

	private float weight;

	/**
	 * @param resourceName
	 *            resource name
	 * @param resourceType
	 *            resource type
	 * @param actionName
	 *            action name
	 * @param actionType
	 *            action type
	 * @param background
	 *            background
	 * @param modifiers
	 *            modifiers
	 * @param weight
	 *            relative width
	 */
	public WeightedButtonImpl(String resourceName, ResourceType resourceType,
			String actionName, ActionType actionType, Background background,
			KeyModifier[] modifiers, float weight, String altText) {
		super(resourceName, resourceType, actionName, actionType, background,
				modifiers, altText);
		this.weight = weight;
	}

	/**
	 * @param resourceName
	 *            resource name
	 * @param resourceType
	 *            resource type
	 * @param actionName
	 *            action name
	 * @param actionType
	 *            action type
	 * @param background
	 *            background
	 * @param modifiers
	 *            modifiers
	 * @param weight
	 *            relative width
	 */
	public WeightedButtonImpl(String resourceName, ResourceType resourceType,
			String actionName, ActionType actionType, Background background,
			KeyModifier[] modifiers, float weight) {
		super(resourceName, resourceType, actionName, actionType, background,
				modifiers, null);
		this.weight = weight;
	}

	@Override
	public float getWeight() {
		return weight;
	}

}
