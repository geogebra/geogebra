package org.geogebra.web.geogebra3D.web.input3D;

/**
 * Robot to dispatch synthetic mouse events
 * 
 * @author mathieu
 *
 */
public class MouseRobot {

	/**
	 * @param x
	 *            event x-coord
	 * @param y
	 *            event y-coord
	 */
	static native public void dispatchMouseMoveEvent(int x, int y) /*-{

		var target = $doc.elementFromPoint(x, y);
		var x1 = x - target.offsetLeft;
		var y1 = y - target.offsetTop;
		var event = new MouseEvent('mouseover', {
			'view' : $wnd,
			'bubbles' : true,
			'cancelable' : true,
			'screenX' : x,
			'screenY' : y,
			'clientX' : x1,
			'clientY' : y1
		});
		target.dispatchEvent(event);

	}-*/;

}
