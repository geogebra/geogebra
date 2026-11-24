/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.web;

import org.geogebra.editor.share.event.ClickListener;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DoubleClickHandler;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.renderer.share.SelectionBox;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.PointerEvent;
import jsinterop.base.Js;

/**
 * Connects cross-platform click handler to HTML5 editor.
 */
public class ClickAdapterW implements DoubleClickHandler {
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
		HTMLElement target = Js.uncheckedCast(event.target);
		target.setPointerCapture(event.pointerId);
		event.preventDefault();
		SelectionBox.setTouchSelection("touch".equals(event.pointerType));
		if (!canHover(event)) {
			// a mouse that was used before must not leave its hover state behind
			handler.onHoverEnd();
		}
		handler.onPointerDown(toFormulaPx(event.offsetX), field.toFormulaY(event.offsetY));
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
		handler.onPointerUp(toFormulaPx(event.offsetX), field.toFormulaY(event.offsetY));
		onHover(event);
	}

	private void onPointerMove(PointerEvent event) {
		if (this.pointerIsDown) {
			handler.onPointerMove(toFormulaPx(event.offsetX), field.toFormulaY(event.offsetY));
		} else {
			// hovering changes nothing in the formula, so it works even while the field
			// leaves pointer input to its owner
			onHover(event);
		}
	}

	private void onHover(PointerEvent event) {
		if (canHover(event)) {
			handler.onHover(toFormulaPx(event.offsetX), field.toFormulaY(event.offsetY));
		}
	}

	private void onPointerLeave(PointerEvent event) {
		Element related = Js.uncheckedCast(event.relatedTarget);
		// elements laid over the formula belong to it: the pointer has not really left
		if (related == null || related.closest("." + MathFieldW.OVERLAY_STYLE_NAME) == null) {
			handler.onHoverEnd();
		}
	}

	/*
	 * Touch input does not hover: its pointer only exists while pressed.
	 */
	private static boolean canHover(PointerEvent event) {
		return !"touch".equals(event.pointerType);
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

		element.addEventListener("pointerdown", (event) -> onPointerDown(Js.uncheckedCast(event)));
		element.addEventListener("pointerenter", (event) -> {
			PointerEvent ptr = Js.uncheckedCast(event);
			if (ptr.buttons > 0) {
				onPointerDown(ptr);
			}
		});
		element.addEventListener("pointerup", (event) -> onPointerUp(Js.uncheckedCast(event)));
		element.addEventListener("pointermove", (event) -> onPointerMove(Js.uncheckedCast(event)));
		element.addEventListener("pointerleave", (event) -> onPointerLeave(Js.uncheckedCast(event)));

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
