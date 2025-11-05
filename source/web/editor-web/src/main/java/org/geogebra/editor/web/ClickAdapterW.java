package org.geogebra.editor.web;

import org.geogebra.editor.share.event.ClickListener;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DoubleClickHandler;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.renderer.share.SelectionBox;

import elemental2.dom.HTMLElement;
import elemental2.dom.PointerEvent;
import jsinterop.base.Js;

/**
 * Connects cross-platform click handler to HTML5 editor.
 */
public class ClickAdapterW
		implements DoubleClickHandler {
	private final ClickListener handler;
	private boolean pointerIsDown = false;
	private final MathFieldW field;
	private double scale = 1;

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

	private void onPointerDown(PointerEvent event) {
		if (!field.isEnabled()) {
			return;
		}

		event.preventDefault();
		SelectionBox.setTouchSelection("touch".equals(event.pointerType));
		handler.onPointerDown(toFormulaPx(event.offsetX), toFormulaPx(event.offsetY));
		this.pointerIsDown = true;
	}

	/*
	 * Converts from event pixels to formula pixels. Does *not* depend on device pixel ratio.
	 */
	private int toFormulaPx(double eventPx) {
		return (int) (eventPx / scale);
	}

	private void onPointerUp(PointerEvent event) {
		if (!field.isEnabled()) {
			return;
		}
		this.pointerIsDown = false;
		handler.onPointerUp(toFormulaPx(event.offsetX), toFormulaPx(event.offsetY));
	}

	private void onPointerMove(PointerEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(toFormulaPx(event.offsetX), toFormulaPx(event.offsetY));
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

		HTMLElement element = Js.uncheckedCast(html.getElement());

		element.addEventListener("pointerdown",
				(event) -> {
					onPointerDown(Js.uncheckedCast(event));
					PointerEvent ptr = Js.uncheckedCast(event);
					HTMLElement target = Js.uncheckedCast(event.target);
					target.setPointerCapture(ptr.pointerId);
				});
		element.addEventListener("pointerup",
				(event) -> onPointerUp(Js.uncheckedCast(event)));
		element.addEventListener("pointermove",
				(event) -> onPointerMove(Js.uncheckedCast(event)));

		html.addDomHandler(this, DoubleClickEvent.getType());
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		if (field.isEnabled()) {
			handler.onLongPress(event.getX(), event.getY());
		}
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
