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

package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ExpressionReducerTest extends BaseAppTestSetup {
	private ExpressionReducer productReducer;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		productReducer = new ExpressionReducer(new SimplifyUtils(getKernel()), Operation.MULTIPLY);
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(2)*2*sqrt(6)*5*7, 70sqrt(2) sqrt(6)",
			"sqrt(6)*2*sqrt(2)*5*7, 70sqrt(6) sqrt(2)",
			"sqrt(2)*2*-sqrt(6)*5*7, -70sqrt(2) sqrt(6)",
			"sqrt(2)*2*sqrt(6)*-5*7, -70sqrt(2) sqrt(6)",
			"sqrt(2)*2*sqrt(6)*-5*-7, 70sqrt(2) sqrt(6)"
	})
	void testReduce(String definition, String expected) {
		GeoElementND product = evaluateGeoElement(definition);
		assertEquals(expected, productReducer.apply(product.getDefinition())
				.toString(StringTemplate.defaultTemplate));
	}

}
