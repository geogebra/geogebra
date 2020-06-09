package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.algos.AlgoMacroInterface;

public class DefaultGeoPriorityComparator implements GeoPriorityComparator {

	@Override
	public int compare(GeoElement a, GeoElement b, boolean checkLastHitType) {
		if (a.getLayer() - b.getLayer() != 0) {
			return a.getLayer() - b.getLayer();
		}

		if (checkLastHitType) {
			if (a.getLastHitType() == GeoElement.HitType.ON_BOUNDARY
					&& b.getLastHitType() != GeoElement.HitType.ON_BOUNDARY) {
				return 1;
			}

			if (a.getLastHitType() != GeoElement.HitType.ON_BOUNDARY
					&& b.getLastHitType() == GeoElement.HitType.ON_BOUNDARY) {
				return -1;
			}
		}

		int typePriorityDifference = typePriority(a) - typePriority(b);
		if (typePriorityDifference != 0) {
			return typePriorityDifference;
		}

		int constructionOrderDifference = a.getConstructionIndex() - b.getConstructionIndex();
		if (constructionOrderDifference != 0) {
			return constructionOrderDifference;
		}

		if (a.getParentAlgorithm() instanceof AlgoMacroInterface) {
			return ((AlgoMacroInterface) a.getParentAlgorithm())
					.drawBefore(a, b);
		}

		return (int) (a.getID() - b.getID());
	}

	/**
	 * @return drawing priority (lower = drawn first)
	 */
	private int typePriority(GeoElement geo) {
		return geo.getGeoClassType().getPriority(geo.isIndependent());
	}
}
