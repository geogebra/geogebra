/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.himamis.retex.editor.web;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * Represents a native focus event.
 */
public class CompositionUpdateEvent extends DomEvent<CompositionHandler> {

	/**
	 * Event type for focus events. Represents the meta-data associated with
	 * this event.
	 */
	private static final Type<CompositionHandler> TYPE = new Type<CompositionHandler>(
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
	 * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
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

	public String getData() {
		return getData(getNativeEvent());
	}

	public native String getData(NativeEvent evt) /*-{
		return evt.data;
	}-*/;

}
