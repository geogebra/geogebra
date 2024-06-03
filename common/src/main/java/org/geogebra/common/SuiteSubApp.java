package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.*;

import java.util.Arrays;

public enum SuiteSubApp {
	GRAPHING(GRAPHING_APPCODE),
	GEOMETRY(GEOMETRY_APPCODE),
	G3D(G3D_APPCODE),
	CAS(CAS_APPCODE),
	PROBABILITY(PROBABILITY_APPCODE),
	SCIENTIFIC(SCIENTIFIC_APPCODE);

	public final String appCode;

	SuiteSubApp(String appCode) {
		this.appCode = appCode;
	}

	public static SuiteSubApp forCode(String code) {
		return Arrays.stream(values()).filter(subApp -> subApp.appCode.equals(code))
				.findFirst().orElse(null);
	}
}
