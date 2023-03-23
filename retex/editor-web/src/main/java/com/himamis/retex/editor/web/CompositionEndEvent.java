/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra. It is based on FocusEvent by Google Inc.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package com.himamis.retex.editor.web;

import org.gwtproject.event.dom.client.DomEvent;

import jsinterop.base.Js;

/**
 * Represents a native focus event.
 */
public class CompositionEndEvent extends DomEvent<CompositionEndHandler> {

	/**
	 * Event type for composition events. Represents the meta-data associated
	 * with this event.
	 */
	private static final Type<CompositionEndHandler> TYPE = new Type<>(
			"compositionend", new CompositionEndEvent());

	/**
	 * Gets the event type associated with focus events.
	 * 
	 * @return the handler type
	 */
	public static Type<CompositionEndHandler> getType() {
		return TYPE;
	}

	/**
	 * Protected constructor, use
	 * {@link DomEvent#fireNativeEvent(org.gwtproject.dom.client.NativeEvent, org.gwtproject.event.shared.HasHandlers)}
	 * to fire focus events.
	 */
	protected CompositionEndEvent() {
	}

	@Override
	public final Type<CompositionEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompositionEndHandler handler) {
		handler.onCompositionEnd(this);
	}

	/**
	 * @return event data
	 */
	public String getData() {
		CompositionEvent ce = Js.uncheckedCast(getNativeEvent());
		return ce.data;
	}

}
