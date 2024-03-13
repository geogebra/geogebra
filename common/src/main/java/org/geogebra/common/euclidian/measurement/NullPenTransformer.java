package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * No pen transformer.
 */
public class NullPenTransformer implements PenTransformer {

	private static NullPenTransformer istance;

	public static PenTransformer get() {
		if (istance == null) {
			istance = new NullPenTransformer();
		}
		return istance;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void reset(EuclidianView view, List<GPoint> previewPoints) {
	    // stub
	}

	@Override
	public void updatePreview(GPoint newPoint) {
	    // stub
	}
}
