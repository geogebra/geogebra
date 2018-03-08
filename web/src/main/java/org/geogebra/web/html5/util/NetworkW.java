package org.geogebra.web.html5.util;

import org.geogebra.common.move.events.BaseEventPool;
import org.geogebra.common.move.operations.Network;

/**
 * @author gabor some convenience method for checking how the app network state.
 */
public class NetworkW implements Network {

	private native boolean checkOnlineState() /*-{
		return $wnd.navigator.onLine;
	}-*/;

	@Override
	public boolean onLine() {
		return checkOnlineState();
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
