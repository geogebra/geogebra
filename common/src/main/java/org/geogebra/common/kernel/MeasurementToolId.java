package org.geogebra.common.kernel;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

public enum MeasurementToolId {
	NONE(-1), RULER(MODE_RULER),
	PROTRACTOR(MODE_PROTRACTOR, true),
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR, true);

	private final int mode;
	private final boolean protactor;

	MeasurementToolId(int mode) {
		this(mode, false);
	}

	MeasurementToolId(int mode, boolean protactor) {
		this.mode = mode;
		this.protactor = protactor;
	}

	public int getMode() {
		return mode;
	}

	public boolean isProtactor() {
		return protactor;
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
