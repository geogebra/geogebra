package org.geogebra.web.html5.gui.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * Use this class when you ONLY need stopPropagation() & preventDefault(), being
 * static, this might spare creations of small no-name inline classes
 * 
 * @author Arpad
 */
public class CancelEvents implements MouseDownHandler,
		MouseUpHandler, MouseOverHandler, MouseOutHandler, MouseMoveHandler,
		ClickHandler, DoubleClickHandler, TouchStartHandler, TouchEndHandler,
		TouchMoveHandler, TouchCancelHandler {

	public static CancelEvents instance = new CancelEvents();

	private CancelEvents() {
		super();
	}

	public void onClick(ClickEvent ce) {
		ce.preventDefault();
		ce.stopPropagation();
	}

	public void onDoubleClick(DoubleClickEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	public void onMouseDown(MouseDownEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	public void onMouseUp(MouseUpEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	public void onMouseOver(MouseOverEvent me) {
		me.preventDefault();
		me.stopPropagation();
	}

	public void onMouseOut(MouseOutEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	public void onMouseMove(MouseMoveEvent mue) {
		mue.preventDefault();
		mue.stopPropagation();
	}

	public void onTouchStart(TouchStartEvent tse) {
		tse.preventDefault();
		tse.stopPropagation();
	}

	public void onTouchEnd(TouchEndEvent tee) {
		tee.preventDefault();
		tee.stopPropagation();
	}

	public void onTouchMove(TouchMoveEvent tee) {
		tee.preventDefault();
		tee.stopPropagation();
	}

	public void onTouchCancel(TouchCancelEvent tce) {
		tce.preventDefault();
		tce.stopPropagation();
	}
}
