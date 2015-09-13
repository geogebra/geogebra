package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.web.gui.GuiManagerW;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;


public class AlgebraControllerW extends org.geogebra.common.gui.view.algebra.AlgebraController
 implements
        MouseDownHandler, TouchStartHandler, TouchEndHandler, TouchMoveHandler,
        LongTouchHandler, DragStartHandler {

	//FIXME: make e.isControlDown like Application.isControlDown etc.
	//FIXME: make something instead of the outcommented things, etc.
	//FIXME: make event handling
	
	private LongTouchManager longTouchManager;

	public AlgebraControllerW(Kernel kernel) {
		super(kernel);
		longTouchManager = LongTouchManager.getInstance();
	}

	public void handleLongTouch(int x, int y) {
		PointerEvent event = new PointerEvent(x, y, PointerEventType.TOUCH, ZeroOffset.instance);
		event.setIsRightClick(true);
		mousePressed(event);
	}

	/*
	 * MouseListener implementation for popup menus
	 */

	public void mouseClicked(AbstractEvent e) {	
		// right click is consumed in mousePressed, but in GeoGebra 3D,
		// where heavyweight popup menus are enabled this doesn't work
		// so make sure that this is no right click as well (ticket #302)
		if (/*e.isConsumed() FIXME||*/ e.isRightClick()) {
			return;
		}

		// get GeoElement at mouse location		
		GeoElement geo = null;


		// check double click
		int clicks = e.getClickCount();
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		if (clicks == 2) {										
			selection.clearSelectedGeos();
			ev.resetMode();

			return;
		} 	

		int mode = ev.getMode();
		if (!skipSelection && mode == EuclidianConstants.MODE_MOVE) {
			// update selection	

				selection.clearSelectedGeos();

		} 
		else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			ev.clickedGeo(geo, app.isControlDown(e));
		} else 
			// tell selection listener about click
			app.geoElementSelected(geo, false);


		ev.mouseMovedOver(null);		
	}

	public void mousePressed(AbstractEvent e) {
		view.cancelEditing();
		
		boolean rightClick = app.isRightClickEnabled() && e.isRightClick();

		// RIGHT CLICK
		if (rightClick) {
			// The default algebra menu will be created here (not for GeoElements).
			/*e.consume(); FIXME*/
			AlgebraContextMenuW contextMenu = ((GuiManagerW)app.getGuiManager()).getAlgebraContextMenu();

			// Window.getScrollLeft and .getScrollTop are necessary; see ticket #4049
			contextMenu.show(view, e.getPoint().x + Window.getScrollLeft(), e.getPoint().y + Window.getScrollTop());

			// LEFT CLICK	
		} else {
			
			//hide dialogs if they are open
			((GuiManagerW)app.getGuiManager()).removePopup();

			// When a single, new selection is made with no key modifiers
			// we need to handle selection in mousePressed, not mouseClicked.
			// By doing this selection early, a DnD drag will come afterwards
			// and grab the new selection. 
			// All other selection types must be handled later in mouseClicked. 
			// In this case a DnD drag starts first and grabs the previously selected 
			// geos (e.g. cntrl-selected or EV selected) as the user expects.

			skipSelection = false; // flag to prevent duplicate selection in MouseClicked

			// view.getPathForLocation is not yet implemented, but if it will be, note Window.getScrollLeft() (ticket #4049)

		}
	}

	public void mouseReleased(AbstractEvent e) {
	}

	public void mouseEntered(AbstractEvent p1) {
	}

	public void mouseExited(AbstractEvent p1) {		
	}

	// MOUSE MOTION LISTENER
	public void mouseDragged(AbstractEvent arg0) {}

	// tell EuclidianView
	public void mouseMoved(AbstractEvent e) {		
		if (view.isEditing())
			return;


		GeoElement geo = null;

		// tell EuclidianView to handle mouse over
		//EuclidianView ev = app.getEuclidianView();
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		ev.mouseMovedOver(geo);								

	}


	//=====================================================
	// Drag and Drop 
	//=====================================================

	public void dragDropEnd(AbstractEvent e) {}
	public void dragEnter(AbstractEvent e) {}
	public void dragExit(AbstractEvent e) {}
	public void dragOver(AbstractEvent e) {}
	public void dropActionChanged(AbstractEvent e) {}

	public void onMouseDown(MouseDownEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		// event.stopPropagation();
		App.debug("mouseDown");
		// event.preventDefault();
		mousePressed(PointerEvent.wrapEventAbsolute(event, ZeroOffset.instance));
	}

	public void onMouseUp(MouseUpEvent event) {
		// TODO: make it care for mouse down too
		// currently, this event is not used
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseClicked(PointerEvent.wrapEvent(event, ZeroOffset.instance));
	}

	public void onMouseMove(MouseMoveEvent event) {
		// currently, this event is not used
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		mouseMoved(PointerEvent.wrapEvent(event, ZeroOffset.instance));
	}

	public void onTouchMove(TouchMoveEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent e = PointerEvent.wrapEvent(targets.get(targets.length()-1), ZeroOffset.instance);
		Element el = Element.as(event.getNativeEvent().getEventTarget());
		
		if (el == ((AlgebraViewW) view).getElement()) {
			longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY());
		}
		CancelEventTimer.touchEventOccured();
    }

	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		CancelEventTimer.touchEventOccured();
    }

	public void onTouchStart(TouchStartEvent event) {
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent e = PointerEvent.wrapEvent(targets.get(0), ZeroOffset.instance);
		Element el = Element.as(event.getNativeEvent().getEventTarget());
		
		if (el == ((AlgebraViewW) view).getElement()) {
			longTouchManager.scheduleTimer(this, e.getX(), e.getY());
		}
		mousePressed(e);
		CancelEventTimer.touchEventOccured();
    }

	public void onDragStart(DragStartEvent event) {
	}

}
