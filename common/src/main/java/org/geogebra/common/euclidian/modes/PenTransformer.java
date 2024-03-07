package org.geogebra.common.euclidian.modes;

import org.geogebra.common.awt.GPoint;

public interface PenTransformer {
	boolean isActive();

	void reset();

	void updatePreview(GPoint newPoint);
}
