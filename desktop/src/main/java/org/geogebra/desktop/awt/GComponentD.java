package org.geogebra.desktop.awt;

import java.awt.Component;

import org.geogebra.common.awt.GComponent;
import org.geogebra.common.util.debug.Log;

public class GComponentD implements GComponent {
	private Component impl;

	public GComponentD(Object component) {
		if (component instanceof Component) {
			impl = (Component) component;
		} else {
			Log.warn("Function called with not the right type.");
		}
	}

}
