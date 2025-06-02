package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface HasCorners extends GeoElementND {
	/**
	 * Calculate coordinates of a corner.
	 * @param corner output coordinates
	 * @param index corner index
	 */
	void calculateCornerPoint(GeoPoint corner, int index);

	/**
	 * Mark this for re-computation of bounding box.
	 * @param needsUpdate whether update is needed
	 */
	void setNeedsUpdatedBoundingBox(boolean needsUpdate);

	/**
	 * @return whether bounding box update is needed
	 */
	boolean needsUpdatedBoundingBox();
}
