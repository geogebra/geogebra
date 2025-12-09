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

package org.geogebra.editor.share.event;

/** TODO Try to document this collection of random-ish methods (what is their meaning?
 * when are they called?; what does the return value mean?)
 * Also,
 * - can we align the return types?
 * - try to find a better (more descriptive) name for onInsertString()
 */
public interface MathFieldListener {

	/**
	 * Called when Enter is pressed.
	 */
	void onEnter();

	/**
	 * Called when a key is typed
	 * @param key key name (may be null)
	 */
	void onKeyTyped(String key);

	/**
	 * Called when arrow key is pressed
	 * @param keyCode key code
	 * @return whether key was handled
	 */
	boolean onArrowKeyPressed(int keyCode);

	/**
	 * Celled when string inserted.
	 */
	default void onInsertString() {
		// rarely needed
	}

	/**
	 * Called when Escape key is pressed.
	 * @return whether key was handled
	 */
	boolean onEscape();

	/**
	 * Called when tab pressed
	 * @param shiftDown whether Shift key is pressed
	 * @return whether the key was handled
	 */
	boolean onTab(boolean shiftDown);
}
