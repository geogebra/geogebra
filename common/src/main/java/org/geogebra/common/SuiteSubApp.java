package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.*;

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
}
