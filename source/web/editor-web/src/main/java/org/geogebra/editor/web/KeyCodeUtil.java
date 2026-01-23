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

package org.geogebra.editor.web;

import static org.geogebra.gwtutil.NavigatorUtil.isMacOS;

import org.geogebra.editor.share.util.KeyCodes;

public class KeyCodeUtil {
	/**
	 * @param gwtKeyCode native key code
	 * @return KeyCodes wrapper
	 */
	public static KeyCodes translateGWTCode(int gwtKeyCode) {
		// Special case for Mac: Translate Context Menu Key (93) to Meta key
		if (gwtKeyCode == 93 && isMacOS()) {
			return KeyCodes.META;
		}
		for (KeyCodes l : KeyCodes.values()) {
			if (l.getGWTKeyCode() == gwtKeyCode) {
				return l;
			}
		}
		return KeyCodes.UNKNOWN;
	}
}
