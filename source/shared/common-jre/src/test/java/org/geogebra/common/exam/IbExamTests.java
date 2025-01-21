package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IbExamTests extends BaseExamTests {
	@BeforeEach
	public void setupCvteExam() {
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
}
