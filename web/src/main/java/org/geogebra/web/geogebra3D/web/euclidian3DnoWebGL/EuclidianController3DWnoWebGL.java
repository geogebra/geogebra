package org.geogebra.web.geogebra3D.web.euclidian3DnoWebGL;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianController3DW;
import org.geogebra.web.html5.event.PointerEvent;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

/**
 * (dummy) controller for 3D view, for browsers that don't support webGL
 * 
 * @author mathieu
 *
 */
public class EuclidianController3DWnoWebGL extends EuclidianController3DW {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public EuclidianController3DWnoWebGL(Kernel kernel) {
		super(kernel);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		// nothing to do here
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// nothing to do here
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// nothing to do here
	}

	@Override
	public void onMouseMoveNow(PointerEvent event, long time,
	        boolean startCapture) {
		// nothing to do here
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		// nothing to do here
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		// nothing to do here
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		// nothing to do here
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		// nothing to do here
	}

	@Override
	public void calculateEnvironment() {
		if (view instanceof EuclidianView3DWnoWebGL) {
			((EuclidianView3DWnoWebGL) view).onResize();
			view.repaint();
		}
	}

}
