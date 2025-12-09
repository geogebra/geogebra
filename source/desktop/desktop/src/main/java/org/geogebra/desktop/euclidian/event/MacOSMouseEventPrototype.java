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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.geogebra.common.euclidian.event.AbstractEvent;

public class MacOSMouseEventPrototype implements MouseEventPrototype {

	public static final int RIGHT_BUTTON = 3;

	@Override
	public boolean isRightClick(MouseEvent event) {
		return event.getButton() == RIGHT_BUTTON || event.isControlDown();
	}

	@Override
	public boolean isControlDown(MouseEvent event) {
		return (event.getModifiersEx() & 128) != 0 ;

	}

	@Override
	public boolean isMetaDown(MouseEvent event) {
		return (event.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0 ;

	}

	@Override
	public boolean hasMultipleSelectModifier(AbstractEvent event) {
		return event.isMetaDown();
	}
}
