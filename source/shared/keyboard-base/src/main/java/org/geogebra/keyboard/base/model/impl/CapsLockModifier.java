package org.geogebra.keyboard.base.model.impl;

import java.util.Locale;
import java.util.Map;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;

public class CapsLockModifier implements KeyModifier {

	private boolean capsLock = false;
	private Map<String, String> upperKeys;

	public CapsLockModifier() {
		this(null);
	}

	public CapsLockModifier(Map<String, String> upperKeys) {
		this.upperKeys = upperKeys;
	}

	/**
	 * Toggle capslock.
	 */
	public void toggleCapsLock() {
		capsLock = !capsLock;
	}

	/**
	 * Set capslock to false.
	 * 
	 * @return whether capslock was enabled before
	 */
	public boolean disableCapsLock() {
		boolean capsLockWasEnabled = capsLock;
		capsLock = false;
		return capsLockWasEnabled;
	}

	@Override
	public String modifyResourceName(String resourceName,
			ResourceType resourceType) {
		if (resourceType == ResourceType.TEXT && resourceName.length() == 1) {
			if (capsLock) {
				return getUpperCase(resourceName);
			}
		} else if (resourceType == ResourceType.DEFINED_CONSTANT
				&& resourceName.equals(Resource.CAPS_LOCK.name())) {
			if (capsLock) {
				return Resource.CAPS_LOCK_ENABLED.name();
			}
		}
		return resourceName;
	}

	@Override
	public String modifyActionName(String actionName, ActionType actionType) {
		if (actionType == ActionType.INPUT && actionName.length() == 1) {
			if (capsLock) {
				return getUpperCase(actionName);
			}
		}
		return actionName;
	}

	private String getUpperCase(String name) {
		if (upperKeys != null) {
			String upper = upperKeys.get(name);
			if (upper != null) {
				return upper;
			}
		}

		return name.toUpperCase(Locale.ROOT);
	}

	@Override
	public Background modifyBackground(Background background,
			ActionType actionType, String actionName) {
		if (actionType == ActionType.CUSTOM
				&& actionName.equals(Action.CAPS_LOCK.name())) {
			if (capsLock) {
				return Background.FUNCTIONAL_PRESSED;
			}
		}
		return background;
	}
}
