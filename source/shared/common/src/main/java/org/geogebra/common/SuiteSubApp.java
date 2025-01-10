package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.PROBABILITY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;

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

	/**
	 * @param code app code used e.g. in XML
	 * @return subapp with given code
	 */
	public static SuiteSubApp forCode(String code) {
		return Arrays.stream(values()).filter(subApp -> subApp.appCode.equals(code))
				.findFirst().orElse(null);
	}
}
