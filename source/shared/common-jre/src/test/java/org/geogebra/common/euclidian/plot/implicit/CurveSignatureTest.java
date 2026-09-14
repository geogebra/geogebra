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

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurveSignatureTest extends BaseAppTestSetup {
	@BeforeEach
	void setUp() {
		super.setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void signatureMustChangeWithVariablesFromIneq() {
		evaluateGeoElement("a = 2");
		evaluateGeoElement("b = 1");
		GeoFunctionNVar fun = evaluateGeoElement("a*x^3+b*y^4 <= 0");
		shouldSignatureChange(new CurveSignature(fun.getIneqs().get(0)));
	}

	@Test
	void signatureMustChangeWithVariablesFromCurve() {
		evaluateGeoElement("a = 2");
		evaluateGeoElement("b = 1");
		GeoImplicitCurve curve = evaluateGeoElement("a*x^3+b*y^4 = 0");
		shouldSignatureChange(new CurveSignature(curve));
	}

	private void shouldSignatureChange(CurveSignature signature) {
		evaluate("SetValue(a, 20)");
		assertTrue(signature.isOutdated(), "Signature is the same ");
	}
}
