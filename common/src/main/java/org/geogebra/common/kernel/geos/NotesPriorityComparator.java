package org.geogebra.common.kernel.geos;

public class NotesPriorityComparator implements GeoPriorityComparator {

	@Override
	public int compare(GeoElement a, GeoElement b, boolean checkLastHitType) {
		return (int)a.getOrdering() - (int)b.getOrdering(); //TODO: hi
	}
}
