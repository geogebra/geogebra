package org.geogebra.common.kernel.geos;

public class NotesPriorityComparator implements GeoPriorityComparator {

	@Override
	public int compare(GeoElement a, GeoElement b, boolean checkLastHitType) {
		if (a.isSpotlight()) {
			return 1;
		}

		if (b.isSpotlight()) {
			return -1;
		}

		return Double.compare(a.getOrdering(), b.getOrdering());
	}
}
