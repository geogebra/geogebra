package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class IbExamTests extends BaseExamTests {

	@BeforeEach
	public void setupIbExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.IB, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f'(1)",
			"f'(p)"
	})
	public void testUnrestrictedDerivatives(String expression) {
		evaluate("f(x) = x^2");
		evaluate("p = 2");

		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"f'",
			"f'(x)",
			"g = f'",
			"g(x) = f'",
			"g(x) = f'(x)"
	})
	public void testRestrictedDerivatives(String expression) {
		evaluate("f(x) = x^2");

		assertNull(evaluate(expression));
	}

	@Test
	public void testRestrictedOperationsAreFreeFromSideEffect() {
		assertAll(
				() -> assertNotNull(evaluate("f(x) = x^3")),
				() -> assertNotNull(evaluate("l1 = {x}")),
				() -> assertNull(evaluate("SetValue(l1, 1, f')")),
				() -> assertEquals(app.getKernel().getConstruction().lookupLabel("l1")
						.toString(StringTemplate.defaultTemplate), "l1 = {x}"));
	}

	@Test
	public void testAutoCompleteProvider() {
		assertEquals(7, autocompleteProvider.getCompletions("Sol").count());
	}

	@ParameterizedTest
	@MethodSource(value = "commandArgumentFilterTestProvider")
	public void testCommandArgumentFilter(String command, boolean valid) {
		if (valid) {
			assertNotNull(evaluate(command));
		} else {
			assertNull(evaluate(command));
		}
	}

	private static List<Arguments> commandArgumentFilterTestProvider() {
		return List.of(
				// Integral
				arguments("Integral(x^2, 3, 5)", true),
				arguments("Integral(x^2)", false),
				arguments("Integral(x^3, x)", false),
				arguments("Integral(x^3, 1, 3, true)", false),

				// Invert
				arguments("Invert({{1,2},{3,4}})", true),
				arguments("Invert(sqrt(x))", false),

				// Tangent
				arguments("Tangent((6, 3), Circle((0, 0), 5))", false),
				arguments("Tangent(Circle((0, 0), 5), (6, 3))", false),
				arguments("Tangent(Circle((1, 1), 3), Circle((2, 2), 1))", false),
				arguments("Tangent(Line((2, 3), (5, 7)), Circle((1, 2), 1))", false),
				arguments("Tangent((1,2), Curve(x^3 + y^3 - 6xy = 0))", false),
				arguments("Tangent(Curve(x^3 + y^3 - 6xy = 0), (1,2))", false)
		);
	}
}
