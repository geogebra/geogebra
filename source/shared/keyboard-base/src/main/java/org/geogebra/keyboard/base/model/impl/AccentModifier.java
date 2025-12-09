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

package org.geogebra.keyboard.base.model.impl;

import org.geogebra.keyboard.base.Accents;
import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;

public class AccentModifier implements KeyModifier {

	private static final byte GRAVE_ACCENT = 0b0001;
	private static final byte CIRCUMFLEX_ACCENT = 0b0010;
	private static final byte CARON_ACCENT = 0b0100;
	private static final byte ACUTE_ACCENT = 0b1000;

	private int currentAccent = 0;

	private Accents accents = new Accents();

	/**
	 * @param accent
	 *            accent modifier
	 * @return whether accent changed
	 */
	public boolean toggleAccent(String accent) {
		boolean changed = true;
		if (accent != null) {
			switch (accent) {
			case Accents.ACCENT_ACUTE:
				currentAccent = ~currentAccent & ACUTE_ACCENT;
				break;
			case Accents.ACCENT_CARON:
				currentAccent = ~currentAccent & CARON_ACCENT;
				break;
			case Accents.ACCENT_CIRCUMFLEX:
				currentAccent = ~currentAccent & CIRCUMFLEX_ACCENT;
				break;
			case Accents.ACCENT_GRAVE:
				currentAccent = ~currentAccent & GRAVE_ACCENT;
				break;
			default:
				changed = false;
			}
		} else {
			changed = currentAccent != (currentAccent = 0);
		}
		return changed;
	}

	@Override
	public String modifyResourceName(String resourceName,
			ResourceType resourceType) {
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
	public Background modifyBackground(Background background,
			ActionType actionType, String actionName) {
		if (actionType == ActionType.CUSTOM && ((actionName.equals(
				Action.TOGGLE_ACCENT_ACUTE.name()) && hasAccent(ACUTE_ACCENT))
				|| (actionName.equals(Action.TOGGLE_ACCENT_CARON.name())
						&& hasAccent(CARON_ACCENT))
				|| (actionName.equals(Action.TOGGLE_ACCENT_GRAVE.name())
						&& hasAccent(GRAVE_ACCENT))
				|| (actionName.equals(Action.TOGGLE_ACCENT_CIRCUMFLEX.name())
						&& hasAccent(CIRCUMFLEX_ACCENT)))) {
			return Background.STANDARD_PRESSED;
		}
		return background;
	}

	private String getAccent(String letter) {
		String returnValue = null;
		if (hasAccent(GRAVE_ACCENT)) {
			returnValue = accents.getGraveAccent(letter);
		} else if (hasAccent(ACUTE_ACCENT)) {
			returnValue = accents.getAcuteLetter(letter);
		} else if (hasAccent(CARON_ACCENT)) {
			returnValue = accents.getCaronLetter(letter);
		} else if (hasAccent(CIRCUMFLEX_ACCENT)) {
			returnValue = accents.getCircumflexLetter(letter);
		}
		return returnValue != null ? returnValue : letter;
	}

	private boolean hasAccent(byte accent) {
		return (currentAccent & accent) != 0;
	}
}
