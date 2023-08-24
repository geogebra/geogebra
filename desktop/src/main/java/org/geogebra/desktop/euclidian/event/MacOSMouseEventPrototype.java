package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

public class MacOSMouseEventPrototype implements MouseEventPrototype {
	@Override
	public boolean isRightClick(MouseEvent event) {
		boolean rightPressed = event.getButton() == 3;
		return (event.isPopupTrigger() && rightPressed)
				|| event.isControlDown();
	}
}
