package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

public class MouseEventUtil {
	private static final MouseEventPrototype prototype = AppD.MAC_OS
			? new MacOSMouseEventPrototype()
			: new DefaultMouseEventPrototype();

	public static boolean isRightClick(MouseEvent event) {
		return prototype.isRightClick(event);
	}
}
