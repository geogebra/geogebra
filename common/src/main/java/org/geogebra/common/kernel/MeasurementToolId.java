package org.geogebra.common.kernel;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

public enum MeasurementToolId {
	NONE(-1), RULER(MODE_RULER),
	PROTRACTOR(MODE_PROTRACTOR),
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR);

	private final int mode;

	MeasurementToolId(int mode) {

		this.mode = mode;
	}

	public static MeasurementToolId byMode(int mode) {
		for (MeasurementToolId type: values()) {
			if (type.mode == mode) {
				return type;
			}
		}
		return NONE;
	}

	public static MeasurementToolId byOrder(int order) {
		for (MeasurementToolId type: values()) {
			if (type.ordinal() == order) {
				return type;
			}
		}
		return NONE;
	}
}
