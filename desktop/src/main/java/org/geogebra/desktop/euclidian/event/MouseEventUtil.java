package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

public class MouseEventUtil {
	private final static MouseEventPrototype prototype = AppD.MAC_OS
			? new MacOSMouseEventPrototype()
			: new DefaultMouseEventPrototype();

	public static boolean isRightClick(MouseEvent event) {
		return prototype.isRightClick(event);
	}

	public static boolean isControlDown(MouseEvent event) {
		return prototype.isControlDown(event);
	}

	public static boolean isMetaDown(MouseEvent event) {
		return prototype.isMetaDown(event);
	}

	public static boolean hasMultipleSelectModifier(MouseEventD event) {
		return prototype.hasMultipleSelectModifier(event);
	}
}
