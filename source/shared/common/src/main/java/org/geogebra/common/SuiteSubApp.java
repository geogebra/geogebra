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

package org.geogebra.common;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.PROBABILITY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.SCIENTIFIC_APPCODE;

import java.util.Arrays;
import java.util.List;

/**
 * Suite subapp.
 */
public enum SuiteSubApp {
	GRAPHING(GRAPHING_APPCODE),
	GEOMETRY(GEOMETRY_APPCODE),
	G3D(G3D_APPCODE),
	CAS(CAS_APPCODE),
	PROBABILITY(PROBABILITY_APPCODE),
	SCIENTIFIC(SCIENTIFIC_APPCODE);

	public final String appCode;

	/**
	 * Returns the available sub-apps for Suite.
	 * @return available sub-apps
	 */
	public static List<SuiteSubApp> availableValues() {
		return List.of(values());
	}

	// for ObjC
	public String getAppCode() {
		return appCode;
	}

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
