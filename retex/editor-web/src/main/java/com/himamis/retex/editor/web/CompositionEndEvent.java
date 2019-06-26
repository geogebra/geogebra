/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra. It is based on FocusEvent by Google Inc.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package com.himamis.retex.editor.web;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;

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
	 * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
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

	public String getData() {
		return getData(getNativeEvent());
	}

	public native String getData(NativeEvent evt) /*-{
		return evt.data;
	}-*/;

}
