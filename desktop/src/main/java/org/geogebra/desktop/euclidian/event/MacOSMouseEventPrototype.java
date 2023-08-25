package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

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
}
