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

package org.geogebra.common.gui.view.probcalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.common.properties.impl.distribution.DistributionParameterProperty;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DistributionParameterPropertyTests extends BaseAppTestSetup {
	private ProbabilityCalculatorView probabilityCalculatorView;

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.PROBABILITY);
		probabilityCalculatorView = new HeadlessProbabilityCalculatorView(getApp());
	}

	@Test
	public void testDistributionWithOneAvailableParameter() {
		probabilityCalculatorView.setProbabilityCalculator(Dist.POISSON, null, false);
		assertTrue(parameterPropertyWithIndex(0).isAvailable());
		assertFalse(parameterPropertyWithIndex(1).isAvailable());
		assertFalse(parameterPropertyWithIndex(2).isAvailable());
	}

	@Test
	public void testDistributionWithTwoAvailableParameter() {
		probabilityCalculatorView.setProbabilityCalculator(Dist.NORMAL, null, false);
		assertTrue(parameterPropertyWithIndex(0).isAvailable());
		assertTrue(parameterPropertyWithIndex(1).isAvailable());
		assertFalse(parameterPropertyWithIndex(2).isAvailable());
	}

	@Test
	public void testDistributionWithThreeAvailableParameter() {
		probabilityCalculatorView.setProbabilityCalculator(Dist.HYPERGEOMETRIC, null, false);
		assertTrue(parameterPropertyWithIndex(0).isAvailable());
		assertTrue(parameterPropertyWithIndex(1).isAvailable());
		assertTrue(parameterPropertyWithIndex(2).isAvailable());
	}

	@Test
	@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
	public void testChangingDistributionChangesParameterNames() {
		DistributionParameterProperty parameterProperty1 = parameterPropertyWithIndex(0);
		DistributionParameterProperty parameterProperty2 = parameterPropertyWithIndex(1);
		DistributionParameterProperty parameterProperty3 = parameterPropertyWithIndex(2);

		probabilityCalculatorView.setProbabilityCalculator(Dist.NORMAL, null, false);
		assertEquals("Parameter μ", parameterProperty1.getName());
		assertEquals("Parameter σ", parameterProperty2.getName());

		probabilityCalculatorView.setProbabilityCalculator(Dist.CAUCHY, null, false);
		assertEquals("Median", parameterProperty1.getName());
		assertEquals("Scale", parameterProperty2.getName());

		probabilityCalculatorView.setProbabilityCalculator(Dist.HYPERGEOMETRIC, null, false);
		assertEquals("Population", parameterProperty1.getName());
		assertEquals("Parameter n", parameterProperty2.getName());
		assertEquals("Sample", parameterProperty3.getName());
	}

	private DistributionParameterProperty parameterPropertyWithIndex(int parameterIndex) {
		return new DistributionParameterProperty(getAlgebraProcessor(), probabilityCalculatorView,
				getLocalization(), parameterIndex);
	}
}
