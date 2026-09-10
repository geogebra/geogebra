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
 
package org.geogebra.common.properties.impl.objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StepPropertyTest extends BaseAppTestSetup {

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testConstructorSucceeds() {
		GeoNumeric slider = evaluateGeoElement("1");
		slider.setEuclidianVisible(true);
		try {
			new AnimationStepProperty(getKernel().getAlgebraProcessor(),
					getLocalization(), slider, true);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {"(1,1)", "Point(x=y)"})
	@Issue("APPS-7875")
	void testApplicable(String definition) {
		GeoElement point = evaluateGeoElement(definition);
		assertDoesNotThrow(() ->
				new AnimationStepProperty(getAlgebraProcessor(), getLocalization(), point, false));
	}
}
