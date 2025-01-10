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
