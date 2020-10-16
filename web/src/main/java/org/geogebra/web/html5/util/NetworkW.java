package org.geogebra.web.html5.util;

import org.geogebra.common.move.events.BaseEventPool;
import org.geogebra.common.move.operations.Network;

import elemental2.dom.DomGlobal;

/**
 * @author gabor some convenience method for checking how the app network state.
 */
public class NetworkW implements Network {

	@Override
	public boolean onLine() {
		return DomGlobal.navigator == null || DomGlobal.navigator.onLine;
	}

	private static native void nativeAttach(String t, BaseEventPool ep) /*-{
		$wnd.addEventListener(t, function() {
			ep.@org.geogebra.common.move.events.BaseEventPool::trigger()();
		});
		$doc.addEventListener(t, function() {
			ep.@org.geogebra.common.move.events.BaseEventPool::trigger()();
		});
	}-*/;

	public static void attach(String type, BaseEventPool eventPool) {
		nativeAttach(type, eventPool);
	}

}
