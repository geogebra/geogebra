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
