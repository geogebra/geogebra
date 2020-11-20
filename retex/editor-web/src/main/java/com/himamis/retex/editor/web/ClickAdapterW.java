package com.himamis.retex.editor.web;

import com.google.gwt.core.client.JsArray;
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

/**
 * Connects cross-platform click handler to HTML5 editor.
 */
public class ClickAdapterW
		implements MouseUpHandler, MouseDownHandler, MouseMoveHandler,
		TouchStartHandler, TouchMoveHandler, TouchEndHandler,
		DoubleClickHandler {
	private ClickListener handler;
	private boolean pointerIsDown = false;
	private Widget widget;
	private MathFieldW field;
	private long lastTouchDown;

	/**
	 * @param handler
	 *            cross-platform click handler
	 * @param field
	 *            editor
	 */
	public ClickAdapterW(ClickListener handler, MathFieldW field) {
		this.handler = handler;
		this.field = field;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (!field.isEnabled()
				|| lastTouchDown > System.currentTimeMillis() - 200) {
			return;
		}
		SelectionBox.touchSelection = false;
		handler.onPointerDown(field.mouseX(event.getX()),
				field.mouseY(event.getY()));
		Event.setCapture(widget.getElement());
		this.pointerIsDown = true;
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		if (!field.isEnabled()) {
			return;
		}
		SelectionBox.touchSelection = true;
		event.preventDefault();

		handler.onPointerDown(getX(event), getY(event));
		Event.setCapture(widget.getElement());
		this.pointerIsDown = true;
		lastTouchDown = System.currentTimeMillis();
	}

	private int getX(TouchEvent<?> event) {
		Touch touch = getRelevantTouch(event);
		return touch == null ? 0
				: (touch.getClientX() - widget.getAbsoluteLeft());
	}

	private int getY(TouchEvent<?> event) {
		Touch touch = getRelevantTouch(event);
		return touch == null ? 0
				: (touch.getClientY() - widget.getAbsoluteTop());
	}

	/**
	 * @param event
	 *            touch event
	 * @return first changed / added touch
	 */
	public static Touch getRelevantTouch(TouchEvent<?> event) {
		JsArray<?> touches = event.getTouches().length() == 0
				? event.getChangedTouches() : event.getTouches();
		return (Touch) touches.get(0);
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (!field.isEnabled() || SelectionBox.touchSelection) {
			return;
		}
		Event.releaseCapture(widget.getElement());
		this.pointerIsDown = false;
		handler.onPointerUp(field.mouseX(event.getX()),
				field.mouseY(event.getY()));
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		if (!field.isEnabled()) {
			return;
		}
		Event.releaseCapture(widget.getElement());
		this.pointerIsDown = false;
		handler.onPointerUp(getX(event), getY(event));
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(getX(event), getY(event));
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(event.getX(), event.getY());
		}
	}

	/**
	 * Register this as pointer event handler for a widget.
	 * 
	 * @param html
	 *            widget
	 */
	public void listenTo(Widget html) {
		if (html == null) {
			return;
		}
		widget = html;
		html.addDomHandler(this, MouseDownEvent.getType());
		html.addDomHandler(this, MouseMoveEvent.getType());
		html.addDomHandler(this, MouseUpEvent.getType());
		html.addBitlessDomHandler(this, TouchStartEvent.getType());
		html.addBitlessDomHandler(this, TouchMoveEvent.getType());
		html.addBitlessDomHandler(this, TouchEndEvent.getType());

		html.addDomHandler(this, DoubleClickEvent.getType());
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if (field.isEnabled()) {
			handler.onLongPress(event.getX(), event.getY());
		}
	}

}
