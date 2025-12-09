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

package org.geogebra.common.move.views;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.events.GenericEvent;

/**
 * Simple event bus
 * @param <T>
 *            type of handlers this view contains
 * @author gabor
 */
public class BaseView<T> {

	/**
	 * Views that need to be notified about events.
	 */
	protected List<T> viewComponents;

	/**
	 * Protected constructor.
	 */
	public BaseView() {
		viewComponents = new ArrayList<>();
	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Removes a view from the views list
	 */
	public void remove(T view) {
		viewComponents.remove(view);
	}

	/**
	 * @param view
	 *            Renderable view
	 * 
	 *            Adds new view to the view's list
	 */
	public final void add(T view) {
		if (!viewComponents.contains(view)) {
			viewComponents.add(view);
		}
	}

	/**
	 * Notifies all view components of an event
	 *
	 * @param event
	 *            The event that occurred.
	 */
	public void onEvent(GenericEvent<T> event) {
		for (T view : viewComponents) {
			event.fire(view);
		}
	}
}
