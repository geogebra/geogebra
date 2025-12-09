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

package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

/**
 * Utility class to handle events in a platform specific way
 */
public class MouseEventUtil {
	private final static MouseEventPrototype prototype = AppD.MAC_OS
			? new MacOSMouseEventPrototype()
			: new DefaultMouseEventPrototype();

	/**
	 *
	 * @param event to check.
	 * @return if event is a right click.
	 */
	public static boolean isRightClick(MouseEvent event) {
		return prototype.isRightClick(event);
	}

	/**
	 *
	 * @param event to check.
	 * @return if control/option key is down.
	 */
	public static boolean isControlDown(MouseEvent event) {
		return prototype.isControlDown(event);
	}

	/**
	 * @param event to check.
	 * @return if the meta key is down.
	 */
	public static boolean isMetaDown(MouseEvent event) {
		return prototype.isMetaDown(event);
	}

	/**
	 *
	 * @param event to check.
	 * @return if key for multiple select is down.
	 */
	public static boolean hasMultipleSelectModifier(MouseEventD event) {
		return prototype.hasMultipleSelectModifier(event);
	}
}