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

package org.geogebra.common.kernel;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;

public class ElementCollector implements EventListener {
	private final ArrayList<GeoElement> geos = new ArrayList<>();

	@Override
	public void sendEvent(Event evt) {
		if (evt.type == EventType.ADD) {
			geos.add(evt.target);
		}
	}

	@Override
	public void reset() {
		geos.clear();
	}

	/**
	 * Remove all geos from construction
	 */
	public void removeAll() {
		for (GeoElement geo : geos) {
			geo.remove();
		}
		geos.clear();
	}
}
