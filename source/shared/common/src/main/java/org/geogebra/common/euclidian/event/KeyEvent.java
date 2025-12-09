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

package org.geogebra.common.euclidian.event;

/**
 * Common class for key events.
 */
public abstract class KeyEvent {

	/**
	 * @return true iff enter was pressed.
	 */
	public abstract boolean isEnterKey();

	/**
	 * @return true iff Ctrl was pressed.
	 */
	public abstract boolean isCtrlDown();

	/**
	 * @return true iff Alt was pressed.
	 */
	public abstract boolean isAltDown();

	/**
	 * @return the char code of the pressed key.
	 */
	public abstract char getCharCode();

	/**
	 * Prevents the wrapped native event's default action.
	 */
	public abstract void preventDefault();

}
