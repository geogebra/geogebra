package com.himamis.retex.editor.web;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.renderer.share.SelectionBox;

public class ClickAdapterW
		implements MouseUpHandler, MouseDownHandler, MouseMoveHandler,
		TouchStartHandler, TouchMoveHandler, TouchEndHandler,
		DoubleClickHandler {
	private ClickListener handler;
	private boolean pointerIsDown = false;
	private Widget widget;
	private MathFieldW mf;

	public ClickAdapterW(ClickListener handler, MathFieldW mf) {
		this.handler = handler;
		this.mf = mf;
	}

	public void onMouseDown(MouseDownEvent event) {
		SelectionBox.touchSelection = false;
		mf.startBlink();
		handler.onPointerDown(event.getX(), event.getY());
		Event.setCapture(widget.getElement());
		this.pointerIsDown = true;
	}

	public void onTouchStart(TouchStartEvent event) {
		SelectionBox.touchSelection = true;

		handler.onPointerDown(getX(event), getY(event));
		Event.setCapture(widget.getElement());
		this.pointerIsDown = true;
	}

	private int getX(TouchEvent event) {
		Touch touch = (Touch) event.getTouches().get(0);
		return touch.getClientX();
	}

	private int getY(TouchEvent event) {
		Touch touch = (Touch) event.getTouches().get(0);
		return touch.getClientY();
	}

	public void onMouseUp(MouseUpEvent event) {
		Event.releaseCapture(widget.getElement());
		this.pointerIsDown = false;
		handler.onPointerUp(event.getX(), event.getY());

	}
	public void onTouchEnd(TouchEndEvent event) {
		Event.releaseCapture(widget.getElement());
		this.pointerIsDown = false;
		handler.onPointerUp(getX(event), getY(event));
	}

	public void onTouchMove(TouchMoveEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(getX(event), getY(event));
		}
	}

	public void onMouseMove(MouseMoveEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(event.getX(), event.getY());
		}

	}



	public void listenTo(Widget html) {
		widget = html;
		html.addDomHandler(this, MouseDownEvent.getType());
		html.addDomHandler(this, MouseMoveEvent.getType());
		html.addDomHandler(this, MouseUpEvent.getType());
		html.addDomHandler(this, TouchStartEvent.getType());
		html.addDomHandler(this, TouchMoveEvent.getType());
		html.addDomHandler(this, TouchEndEvent.getType());

		html.addDomHandler(this, DoubleClickEvent.getType());
	}

	public void onDoubleClick(DoubleClickEvent event) {
		handler.onLongPress(event.getX(), event.getY());
		
	}

}
