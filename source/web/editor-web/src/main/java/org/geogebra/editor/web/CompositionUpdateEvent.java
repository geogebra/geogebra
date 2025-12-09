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

import org.gwtproject.event.dom.client.DomEvent;

import elemental2.dom.CompositionEvent;
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
