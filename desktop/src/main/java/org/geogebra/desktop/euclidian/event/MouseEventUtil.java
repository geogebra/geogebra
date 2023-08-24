package org.geogebra.desktop.euclidian.event;

import java.awt.event.MouseEvent;

import org.geogebra.desktop.main.AppD;

public class MouseEventUtil {
	private static MouseEventPrototype prototype;
	public static boolean isRightClick(MouseEvent event) {
		return prototype.isRightClick(event);
	}

	public static void setApp(AppD app) {
		prototype = app.isMacOS()
			? new MacOSMouseEventPrototype()
			: new DefaultMouseEventPrototype();
	}
}
