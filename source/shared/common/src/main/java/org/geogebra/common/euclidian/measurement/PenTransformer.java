package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * Transformer for points created by pen tool.
 */
public interface PenTransformer {
	boolean isActive();

	void reset(EuclidianView view, List<GPoint> previewPoints);

	void updatePreview(GPoint newPoint);

	/**
	 * @return maximal distance for snapping to edge
	 */
	default int snapThreshold() {
		return 24;
	}
}
