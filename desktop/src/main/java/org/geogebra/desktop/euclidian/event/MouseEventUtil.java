package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

public class MouseEventUtil {
	private static MouseEventPrototype prototype = null;
	public static boolean isRightClick(MouseEvent event) {
		return prototype().isRightClick(event);
	}

	private static MouseEventPrototype prototype() {
		if (prototype == null) {
			prototype = AppD.MAC_OS
					? new MacOSMouseEventPrototype()
					: new DefaultMouseEventPrototype();
		}
		return prototype;
	}
}
