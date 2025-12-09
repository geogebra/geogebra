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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseSimplifyTestSetup extends BaseAppTestSetup {
	protected SimplifyUtils utils;

	protected final SimplifyNode getSimplifier() {
        try {
			Class<? extends SimplifyNode> simplifierClass = getSimplifierClass();
			Constructor<?> const1 = simplifierClass.getConstructor(SimplifyUtils.class);
			return (SimplifyNode) const1.newInstance(utils);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				 | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	@BeforeEach
	public void setUpUtils() {
		setupApp(SuiteSubApp.GRAPHING);
		utils = new SimplifyUtils(getKernel());
	}

	protected final void shouldSimplify(String from, String to) {
		shouldSimplify(from, to, getSimplifier());
	}

	protected final void shouldSimplify(String actualDef, String expectedDef,
			SimplifyNode... simplifiers) {
		GeoNumeric actual = newSymbolicNumeric(actualDef);
		GeoNumeric expected = newSymbolicNumeric(expectedDef);
		ExpressionNode applied = actual.getDefinition();
		for (SimplifyNode simplifier: simplifiers) {
			assertTrue(simplifier.isAccepted(applied),
					applied + " is not accepted by " + simplifier.name());
			applied = simplifier.apply(applied);
		}
		assertEquals(expected.getDefinition().evaluateDouble(), applied.evaluateDouble(),
				Kernel.MAX_PRECISION, "Values do not equal! \n\nDefinitions:\n Expected: "
								+ expectedDef + "\n Actual: " + applied);
		shouldSerialize(expected.getDefinition(), applied);
	}

	protected static void shouldSerialize(ExpressionValue expected, ExpressionValue actual) {
		assertEquals(expected.toString(StringTemplate.defaultTemplate)
						.replaceAll("\\s+", ""), actual.toString(StringTemplate.defaultTemplate)
								.replaceAll("\\s+", ""));
	}

	protected final GeoNumeric newSymbolicNumeric(String actualDef) {
		GeoNumeric actual = evaluateGeoElement(actualDef);
		actual.setSymbolicMode(true, true);
		return actual;
	}

	protected final void shouldAccept(String def) {
		assertTrue(isAccepted(def));
	}

	protected final void shouldNotAccept(String def) {
		assertFalse(isAccepted(def));
	}

	private boolean isAccepted(String def) {
		return getSimplifier().isAccepted(evaluateGeoElement(def).getDefinition());
	}

	protected abstract Class<? extends SimplifyNode> getSimplifierClass();
}

