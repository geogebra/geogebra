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
