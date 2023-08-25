package org.geogebra.desktop.euclidian.event;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class DefaultMouseEventPrototype implements MouseEventPrototype {
	@Override
	public boolean isRightClick(MouseEvent event) {
		return event.getButton() == 3 || ((event.getModifiersEx()
				& InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK);
	}

	@Override
	public boolean isControlDown(MouseEvent event) {
		return event.isControlDown();
	}
}
