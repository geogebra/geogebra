package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.gui.view.algebra.AlgebraController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.gwtproject.core.client.JsArray;
import org.gwtproject.dom.client.Touch;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchEndHandler;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchMoveHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;

/**
 * Algebra controller for web;
 *
 */
public class AlgebraControllerW extends AlgebraController
		implements MouseMoveHandler, MouseDownHandler, TouchStartHandler,
		TouchEndHandler, TouchMoveHandler {

	/**
	 * @param kernel
	 *            kernel
	 */
	public AlgebraControllerW(Kernel kernel) {
		super(kernel);
	}

	private void mousePressed(AbstractEvent e) {
		getView().cancelEditItem();
		boolean rightClick = app.isRightClickEnabled() && e.isRightClick();

		// RIGHT CLICK: no global menu; per-item menu handled elsewhere
		if (!rightClick) {
			// hide dialogs if they are open
			((GuiManagerW) app.getGuiManager()).removePopup();
		}
	}

	// =====================================================
	// Drag and Drop
	// =====================================================

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();
		event.preventDefault();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mousePressed(
				PointerEvent.wrapEventAbsolute(event, ZeroOffset.INSTANCE));
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		CancelEventTimer.touchEventOccurred();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		CancelEventTimer.touchEventOccurred();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent e = PointerEvent.wrapEvent(targets.get(0),
				ZeroOffset.INSTANCE);

		mousePressed(e);
		CancelEventTimer.touchEventOccurred();
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		event.preventDefault();
	}

}
