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

package org.geogebra.common.move.ggtapi;

import org.geogebra.common.move.events.BaseEvent;

/**
 * Event for geogebra.org availability check
 * @author gabor
 */
public class TubeAvailabilityCheckEvent extends BaseEvent {
	private boolean available;

	/**
	 * @param available
	 *            Whether Tube is available
	 */
	public TubeAvailabilityCheckEvent(boolean available) {
		super("tube check");
		this.available = available;
	}

	/**
	 * @return Whether Tube is available
	 */
	public boolean isAvailable() {
		return available;
	}

}
