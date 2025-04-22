package org.geogebra.common.exam;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
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
	public void testCommandArgumentFilter(String command, String expectedError) {
		if (expectedError.isEmpty()) {
			assertNotNull(evaluate(command));
		} else {
			assertNull(evaluate(command));
			assertThat(errorAccumulator.getErrorsSinceReset(), containsString(expectedError));
			errorAccumulator.resetError();
		}
	}

	private static List<Arguments> commandArgumentFilterTestProvider() {
		return List.of(
				// Integral
				arguments("Integral(x^2, 3, 5)", ""),
				arguments("Integral(x^2)", "Illegal number of arguments"),
				arguments("Integral(x^3, x)", "Illegal number of arguments"),
				arguments("Integral(x^3, 1, 3, true)", "Illegal number of arguments"),

				// Invert
				arguments("Invert({{1,2},{3,4}})", ""),
				arguments("Invert(sqrt(x))", "Illegal argument"),

				// Tangent
				arguments("Tangent((6, 3), x^2+y^2=1)", "Illegal argument"),
				arguments("Tangent(x^2+y^2=1, (6, 3))", "Illegal argument"),
				arguments("Tangent(x^2+y^2=1, (x-4)^2+y^2=1)", "Illegal argument"),
				arguments("Tangent(x+y=1, x^2+y^2=1)", "Illegal argument"),
				arguments("Tangent((1,2), Curve(t,t^2,t,1,2))", ""),
				arguments("Tangent(Curve(t,t^2,t,1,2), (1,2))", "Illegal argument"),
				arguments("Tangent((1,2), x^4+y^4=1)", "Illegal argument"),
				arguments("Tangent(x^4+y^4=1, (1,2))", "Illegal argument")
		);
	}
}
