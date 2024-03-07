package org.geogebra.common.kernel;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.modes.PenTransformer;

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
