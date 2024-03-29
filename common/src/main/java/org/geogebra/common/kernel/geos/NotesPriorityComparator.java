package org.geogebra.common.kernel.geos;

public class NotesPriorityComparator implements GeoPriorityComparator {

	@Override
	public int compare(GeoElement a, GeoElement b, boolean checkLastHitType) {
		return Double.compare(a.getOrdering(), b.getOrdering());
	}
}
