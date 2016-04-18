package com.himamis.retex.editor.web;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.himamis.retex.editor.share.event.ClickListener;

public class ClickAdapterW
		implements MouseUpHandler, MouseDownHandler, MouseMoveHandler {
	private ClickListener handler;
	private boolean pointerIsDown = false;
	private HTML widget;

	public ClickAdapterW(ClickListener handler) {
		this.handler = handler;
	}

	public void onMouseDown(MouseDownEvent event) {
		handler.onPointerDown(event.getX(), event.getY());
		Event.setCapture(widget.getElement());
		this.pointerIsDown = true;
	}

	public void onMouseMove(MouseMoveEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(event.getX(), event.getY());
		}

	}

	public void onMouseUp(MouseUpEvent event) {
		Event.releaseCapture(widget.getElement());
		this.pointerIsDown = false;
		handler.onPointerUp(event.getX(), event.getY());

	}

	public void listenTo(HTML html) {
		widget = html;
		html.addMouseDownHandler(this);
		html.addMouseMoveHandler(this);
		html.addMouseUpHandler(this);

	}

}
