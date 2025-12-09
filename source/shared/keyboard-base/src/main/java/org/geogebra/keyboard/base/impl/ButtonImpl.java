/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.keyboard.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Button;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;

public class ButtonImpl implements Button {

	private String resourceName;
	private ResourceType resourceType;

	private List<String> actionName;
	private List<ActionType> actionType;

	private Background background;

	private KeyModifier[] modifiers;
	private String altText = null;

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
	 */
	public ButtonImpl(String resourceName, ResourceType resourceType,
			String actionName, ActionType actionType, Background background,
			KeyModifier[] modifiers, String altText) {
		this.resourceName = resourceName;
		this.resourceType = resourceType;
		this.background = background;
		this.modifiers = modifiers;
		this.actionName = new ArrayList<>();
		this.actionName.add(actionName);
		this.actionType = new ArrayList<>();
		this.actionType.add(actionType);
		this.altText = altText;
	}

	@Override
	public String getResourceName() {
		if (modifiers != null) {
			String modifiedResourceName = resourceName;
			for (KeyModifier modifier : modifiers) {
				modifiedResourceName = modifier
						.modifyResourceName(modifiedResourceName, resourceType);
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
		return getPrimaryActionName();
	}

	@Override
	public ActionType getActionType() {
		return getPrimaryActionType();
	}

	@Override
	public String getPrimaryActionName() {
		return getActionName(0);
	}

	@Override
	public ActionType getPrimaryActionType() {
		return getActionType(0);
	}

	@Override
	public String getActionName(int index) {
		String name = actionName.get(index);
		ActionType type = actionType.get(index);
		if (modifiers != null) {
			String modifiedActionName = name;
			for (KeyModifier modifier : modifiers) {
				modifiedActionName = modifier
						.modifyActionName(modifiedActionName, type);
			}
			return modifiedActionName;
		}
		return name;
	}

	@Override
	public ActionType getActionType(int index) {
		return actionType.get(index);
	}

	@Override
	public int getActionsSize() {
		return actionName.size();
	}

	@Override
	public void addAction(String actionName, ActionType actionType) {
		this.actionName.add(actionName);
		this.actionType.add(actionType);
	}

	@Override
	public Background getBackground() {
		if (modifiers != null) {
			String name = getPrimaryActionName();
			ActionType type = getPrimaryActionType();
			Background modifiedBackground = background;
			for (KeyModifier modifier : modifiers) {
				modifiedBackground = modifier.modifyBackground(
						modifiedBackground, type, name);
			}
			return modifiedBackground;
		}
		return background;
	}

	public String getAltText() {
		return altText == null ? getPrimaryActionName() : altText;
	}

	public void setAltText(String s) {
		altText = s;
	}
}
