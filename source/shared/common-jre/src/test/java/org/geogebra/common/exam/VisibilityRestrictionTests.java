package org.geogebra.common.exam;

import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.ALLOW;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.HIDE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.Effect.IGNORE;
import static org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction.isVisibilityRestricted;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.exam.restrictions.visibility.HiddenInequalityVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.HiddenVectorVisibilityRestriction;
import org.geogebra.common.exam.restrictions.visibility.VisibilityRestriction;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class VisibilityRestrictionTests {
	private App app;
	private AlgebraProcessor algebraProcessor;

	@BeforeEach
	public void setup() {
		app = AppCommonFactory.create(new AppConfigGraphing());
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		app.getSettingsUpdater().resetSettingsOnAppStart();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f(x) = x",
			"x > 2",
			"y = 2x",
			"x^2 + y^2 = 5",
			"(1, 2)",
	})
	public void testEmptySetOfVisibilityRestrictions(String expression) {
		assertFalse(isVisibilityRestricted(evaluateGeoElement(expression),
				Set.of()));
	}

	@Test
	public void testIgnoredRestrictionEffects() {
		Set<VisibilityRestriction> visibilityRestrictions = Set.of(
				geoElement -> geoElement.isInequality() ? HIDE : IGNORE,
				geoElement -> geoElement.isAngle() ? HIDE : IGNORE);
		assertFalse(isVisibilityRestricted(evaluateGeoElement("x = 2"),
				visibilityRestrictions));
	}

	@Test
	public void testIgnoredAndHiddenRestrictionEffects() {
		Set<VisibilityRestriction> visibilityRestrictions = Set.of(
				geoElement -> geoElement.isInequality() ? HIDE : IGNORE,
				geoElement -> geoElement.isAngle() ? HIDE : IGNORE);
		assertTrue(isVisibilityRestricted(evaluateGeoElement("x > 2"),
				visibilityRestrictions));
	}

	@Test
	public void testOverlappingConflictingRestrictionsWithHiddenAndAllowedEffects() {
		GeoElement linearEquation = evaluateGeoElement("x = 2");
		GeoElement quadraticEquation = evaluateGeoElement("x^2 = 2");
		assertAll(() -> assertTrue(isEquation(linearEquation)),
				() -> assertTrue(isEquation(quadraticEquation)),
				() -> assertTrue(isLinearEquation(linearEquation)),
				() -> assertFalse(isLinearEquation(quadraticEquation)));

		Set<VisibilityRestriction> visibilityRestrictions = Set.of(
				geoElement -> isEquation(geoElement) ? HIDE : IGNORE,
				geoElement -> isLinearEquation(geoElement) ? ALLOW : IGNORE);

		assertAll(
				() -> assertFalse(isVisibilityRestricted(linearEquation, visibilityRestrictions)),
				() -> assertTrue(isVisibilityRestricted(quadraticEquation, visibilityRestrictions))
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"a = (1, 2)",
			"b = (1, 2) + 0"
	})
	public void testVectorRestrictions(String expression) {
		assertTrue(isVisibilityRestricted(evaluateGeoElement(expression),
				Set.of(new HiddenVectorVisibilityRestriction())));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"x > 0",
			"y <= 1",
			"x < y",
			"x - y > 2",
			"x^2 + 2y^2 < 1",
			"f: x > 0",
			"f(x) = x > 2",
	})
	public void testInequalityRestrictions(String expression) {
		assertTrue(isVisibilityRestricted(evaluateGeoElement(expression),
				Set.of(new HiddenInequalityVisibilityRestriction())));
	}

	private GeoElement evaluateGeoElement(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return (GeoElement) algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, new ErrorAccumulator(), evalInfo, null)[0];
	}

	private static boolean isEquation(GeoElement geoElement) {
		ExpressionNode definition = geoElement.getDefinition();
		return (definition != null && definition.unwrap() instanceof Equation)
				|| geoElement instanceof EquationValue;
	}

	private static boolean isLinearEquation(GeoElement geoElement) {
		return geoElement instanceof GeoLine || geoElement instanceof GeoPlaneND;
	}
}
