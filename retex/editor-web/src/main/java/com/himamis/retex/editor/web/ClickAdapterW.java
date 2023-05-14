package com.himamis.retex.editor.web;

import org.geogebra.gwtutil.NativePointerEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DoubleClickHandler;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.renderer.share.SelectionBox;

import elemental2.core.Function;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

/**
 * Connects cross-platform click handler to HTML5 editor.
 */
public class ClickAdapterW
		implements DoubleClickHandler {
	private final ClickListener handler;
	private boolean pointerIsDown = false;
	private final MathFieldW field;

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

	private void onPointerDown(NativePointerEvent event) {
		if (!field.isEnabled()) {
			return;
		}

		event.preventDefault();
		SelectionBox.touchSelection = "touch".equals(event.getPointerType());
		handler.onPointerDown((int) event.getOffsetX(), (int) event.getOffsetY());
		this.pointerIsDown = true;
	}

	private void onPointerUp(NativePointerEvent event) {
		if (!field.isEnabled()) {
			return;
		}
		this.pointerIsDown = false;
		handler.onPointerUp((int) event.getOffsetX(), (int) event.getOffsetY());
	}

	private void onPointerMove(NativePointerEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove((int) event.getOffsetX(), (int) event.getOffsetY());
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
					Function capture = Js.uncheckedCast(Js.asPropertyMap(event.target)
							.get("setPointerCapture"));
					if (Js.isTruthy(capture)) {
						NativePointerEvent ptr = Js.uncheckedCast(event);
						capture.call(event.target, ptr.getPointerId());
					}
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
}
