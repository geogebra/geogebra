package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * Transformer for points created by pen tool.
 */
public interface PenTransformer {
	/**
	 * @return whether the transform is active
	 */
	boolean isActive();

	/**
	 * Reset internal state after a point is added.
	 * @param view view
	 * @param previewPoints pen preview points
	 */
	void reset(EuclidianView view, List<GPoint> previewPoints);

	/**
	 * Update single preview point.
	 * @param newPoint pen preview point
	 */
	void updatePreview(GPoint newPoint);

	/**
	 * @return maximal distance for snapping to edge
	 */
	default int snapThreshold() {
		return 24;
	}
}
