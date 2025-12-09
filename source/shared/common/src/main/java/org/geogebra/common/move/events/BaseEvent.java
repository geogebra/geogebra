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

package org.geogebra.common.move.events;

import org.geogebra.common.move.views.EventRenderable;

/**
 * Base of all Events
 * 
 * @author gabor
 */
public abstract class BaseEvent implements GenericEvent<EventRenderable> {

	/**
	 * Needed for identify the event, otherwise it it will be like anonymous
	 * functions, can't be removed individually
	 */
	protected final String name;

	/**
	 * @param name
	 *            event name
	 */
	public BaseEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the event, or null needed for identify it
	 */

	public String getName() {
		return name;
	}

	@Override
	public void fire(EventRenderable target) {
		target.renderEvent(this);
	}

}
