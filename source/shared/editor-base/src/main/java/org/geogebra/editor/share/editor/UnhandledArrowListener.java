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

package org.geogebra.editor.share.editor;

import org.geogebra.editor.share.event.KeyEvent;

/**
 * Listener for arrow keys that were not handled by editor.
 */
@FunctionalInterface
public interface UnhandledArrowListener {
	/**
	 * Runs when arrow key is pressed and not handled by the editor.
	 * @param keyCode key code from {@link org.geogebra.editor.share.util.JavaKeyCodes}
	 * @param keyboardType keyboard type
	 */
	void onArrow(int keyCode, KeyEvent.KeyboardType keyboardType);
}
