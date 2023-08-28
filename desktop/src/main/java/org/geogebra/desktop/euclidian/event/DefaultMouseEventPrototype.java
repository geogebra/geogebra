package org.geogebra.desktop.euclidian.event;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.geogebra.common.euclidian.event.AbstractEvent;

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

	@Override
	public boolean isMetaDown(MouseEvent event) {
		return false;
	}

	@Override
	public boolean hasMultipleSelectModifier(AbstractEvent event) {
		return event.isControlDown();
	}
}
