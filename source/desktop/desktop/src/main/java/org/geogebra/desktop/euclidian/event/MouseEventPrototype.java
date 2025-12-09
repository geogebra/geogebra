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

import org.geogebra.common.euclidian.event.AbstractEvent;

public interface MouseEventPrototype {

	/**
	 *
	 * @param event to check.
	 * @return if event is a right click.
	 */
	boolean isRightClick(MouseEvent event);

	/**
	 *
	 * @param event to check.
	 * @return if control/option key is down.
	 */
	boolean isControlDown(MouseEvent event);

	/**
	 *
	 * @param event to check.
	 * @return if meta key is down.
	 */
	boolean isMetaDown(MouseEvent event);

	/**
	 *
	 * @param event to check.
	 * @return if key for multiple select is down.
	 */
	boolean hasMultipleSelectModifier(AbstractEvent event);
}
