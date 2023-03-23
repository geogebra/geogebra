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
public class CompositionUpdateEvent extends DomEvent<CompositionHandler> {

	/**
	 * Event type for focus events. Represents the meta-data associated with
	 * this event.
	 */
	private static final Type<CompositionHandler> TYPE = new Type<>(
			"compositionupdate", new CompositionUpdateEvent());

	/**
	 * Gets the event type associated with focus events.
	 * 
	 * @return the handler type
	 */
	public static Type<CompositionHandler> getType() {
		return TYPE;
	}

	/**
	 * Protected constructor, use
	 * {@link DomEvent#fireNativeEvent(org.gwtproject.dom.client.NativeEvent, org.gwtproject.event.shared.HasHandlers)}
	 * to fire focus events.
	 */
	protected CompositionUpdateEvent() {
	}

	@Override
	public final Type<CompositionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompositionHandler handler) {
		handler.onCompositionUpdate(this);
	}

	/**
	 * @return event data
	 */
	public String getData() {
		CompositionEvent ce = Js.uncheckedCast(getNativeEvent());
		return ce.data;
	}

}
