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

package org.geogebra.common.plugin;

/**
 * @see EventDispatcher
 * @author arno
 * 
 */
public interface EventListener {

	/**
	 * This method is called by the event dispatcher every time an event is
	 * triggered
	 * 
	 * @param evt
	 *            the event
	 */
	void sendEvent(Event evt);

	/**
	 * This method is called every time we have a new construction. The event
	 * listener should get rid of all the scripts
	 * 
	 * <p>At the moment this is triggered when View.clearView() is called TODO
	 * check that this only really happens when a new file is created or opened.
	 */
	default void reset() {
		// not needed in most cases
	}
}
