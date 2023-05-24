package org.geogebra.common.kernel.geos;

public class NotesPriorityComparator implements GeoPriorityComparator {

	@Override
	public float compare(GeoElement a, GeoElement b, boolean checkLastHitType) {
		return a.getOrdering() - b.getOrdering(); //TODO: hi
	}
}
