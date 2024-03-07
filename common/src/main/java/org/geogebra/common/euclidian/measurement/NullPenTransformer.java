package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;

public class NullPenTransformer implements PenTransformer {
	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void reset(EuclidianView view, List<GPoint> previewPoints) {

	}

	@Override
	public void updatePreview(GPoint newPoint) {

	}
}
