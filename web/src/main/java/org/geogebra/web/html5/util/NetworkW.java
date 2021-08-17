package org.geogebra.web.html5.util;

import elemental2.dom.DomGlobal;

/**
 * @author gabor some convenience method for checking how the app network state.
 */
public class NetworkW {

	public static boolean isOnline() {
		return DomGlobal.navigator == null || DomGlobal.navigator.onLine;
	}

}
