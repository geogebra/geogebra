package org.geogebra.web.geogebra3D.web.input3D;

/**
 * Robot to dispatch synthetic mouse events
 * 
 * @author mathieu
 *
 */
public class MouseRobot {

	// static public void dispatchMouseMoveEvent(int x, int y) {
	// Log.debug("dispatchMouseMoveEvent: " + x + "," + y + " (start)");
	// RootPanel rootPanel = RootPanel.get();
	// // create native event with detail = 1 (TODO check this)
	// NativeEvent event = Document.get().createMouseMoveEvent(1, x, y, x, y,
	// false, false, false, false, NativeEvent.BUTTON_LEFT);
	// DomEvent.fireNativeEvent(event, rootPanel);
	// Log.debug("dispatchMouseMoveEvent: " + x + "," + y + " (end)");
	// }

	static native public void dispatchMouseMoveEvent(int x, int y) /*-{

		var event = new MouseEvent('mousemove', {
			'view' : $wnd,
			'bubbles' : true,
			'cancelable' : true,
			'screenX' : x,
			'screenY' : y,
			'clientX' : x,
			'clientY' : y
		});
		$doc.body.dispatchEvent(event);

	}-*/;

}
