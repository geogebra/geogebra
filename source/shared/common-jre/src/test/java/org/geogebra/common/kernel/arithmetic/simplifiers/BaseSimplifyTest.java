package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.junit.Before;

public abstract class BaseSimplifyTest extends BaseUnitTest {

	SimplifyUtils utils;

	final SimplifyNode getSimplifier() {
        try {
		Class<? extends SimplifyNode> simplifierClass = getSimplifierClass();
        Constructor<?> const1 = simplifierClass.getConstructor(SimplifyUtils.class);
			return (SimplifyNode) const1.newInstance(utils);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				 | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}

	@Before
	public void setUp() {
		getKernel().setPrintDecimals(15);
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
			assertTrue(applied + " is not accepted by " + simplifier.name(),
					simplifier.isAccepted(applied));
			applied = simplifier.apply(applied);
		}
		assertEquals("Values do not equal! \n\nDefinitions:\n Expected: "
						+ expectedDef + "\n Actual: " + applied,
				expected.getDefinition().evaluateDouble(), applied.evaluateDouble(),
				Kernel.MAX_PRECISION);
		shouldSerialize(expected.getDefinition(), applied);
	}

	protected static void shouldSerialize(ExpressionValue expected, ExpressionValue actual) {
		assertEquals(expected.toString(StringTemplate.defaultTemplate)
						.replaceAll("\\s+", ""),
				actual.toString(StringTemplate.defaultTemplate)
						.replaceAll("\\s+", ""));
	}

	protected GeoNumeric newSymbolicNumeric(String actualDef) {
		GeoNumeric actual = add(actualDef);
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
		return getSimplifier().isAccepted(add(def).getDefinition());
	}

	protected abstract Class<? extends SimplifyNode> getSimplifierClass();
}

