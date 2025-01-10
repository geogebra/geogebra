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
