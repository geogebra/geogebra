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

package org.geogebra.keyboard.base;

/**
 * The actions correspond to {@link ActionType#CUSTOM}.
 */
public enum Action {
	BACKSPACE_DELETE,
	CAPS_LOCK,
	RETURN_ENTER,
	LEFT_CURSOR,
	RIGHT_CURSOR,
	UP_CURSOR,
	DOWN_CURSOR,
	NONE,
	TOGGLE_ACCENT_ACUTE,
	TOGGLE_ACCENT_GRAVE,
	TOGGLE_ACCENT_CARON,
	TOGGLE_ACCENT_CIRCUMFLEX,
	SWITCH_TO_ABC,
	SWITCH_TO_SPECIAL_SYMBOLS,
	SWITCH_TO_LATIN_CHARACTERS,
	SWITCH_TO_GREEK_CHARACTERS,
	SWITCH_TO_123,
	ANS,
	SHOW_MATRIX_INPUT_DIALOG
}