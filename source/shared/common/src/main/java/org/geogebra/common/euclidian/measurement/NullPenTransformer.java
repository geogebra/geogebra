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
