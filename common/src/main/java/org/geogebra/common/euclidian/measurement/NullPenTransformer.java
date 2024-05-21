package org.geogebra.common.euclidian.measurement;

import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;

/**
 * No pen transformer.
 */
public final class NullPenTransformer implements PenTransformer {

	private static NullPenTransformer instance;

	private NullPenTransformer() {
		// singleton constructor
	}

	/**
	 *
	 * @return the null transformer.
	 */
	public static PenTransformer get() {
		if (instance == null) {
			instance = new NullPenTransformer();
		}
		return instance;
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
