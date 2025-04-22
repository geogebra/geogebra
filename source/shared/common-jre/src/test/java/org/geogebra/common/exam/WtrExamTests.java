package org.geogebra.common.exam;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class WtrExamTests extends BaseExamTests {
	@BeforeEach
	public void setupWtrExam() {
		setInitialApp(SuiteSubApp.SCIENTIFIC);
		examController.startExam(ExamType.WTR, null);
	}

	@ParameterizedTest
	@CsvSource(value = {
			"BinomialDist(); Illegal number of arguments",
			"Normal(2, 0.5, 1, true); Illegal argument: Boolean",
			"BinomialDist(5, 0.2, 1, false && true); Sorry, something went wrong",
	}, delimiter = ';')
	public void testRestrictedCommands(String expression, String expectedError) {
		assertNull(evaluate(expression));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString(expectedError));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"nCr(4, 2)",
			"BinomialDist(5, 0.2, 1, false)",
			"Normal(2, 0.5, 1)",
			"Normal(2, 0.5, 1, 2)",
	})
	public void testUnrestrictedCommands(String expression) {
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"random()",
			"atan2(sqrt(3), 1)",
			"gamma(5)",
	})
	public void testRestrictedOperations(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{0}",
			"{0,1}",
			"{{0, 1}, {{1, 2}, 1}}",
			"Sequence({1, 2, 3}, x, 1, 2)"
	})
	public void testRestrictedListsInInput(String expression) {
		assertNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Sequence(n, n, 1, 10)",
	})
	public void testRestrictedListsInOutput(String expression) {
		assertNull(evaluate(expression));
		assertThat(errorAccumulator.getErrorsSinceReset(), containsString("Unknown command"));
		errorAccumulator.resetError();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// matrices (are composed of lists, but should be allowed)
			"{{0,1},{1,0}}",
			"{{0},{1}}"
	})
	public void testAllowedLists(String expression) {
		assertNotNull(evaluate(expression));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"3i",
			"1 / (1 - 2i)",
			"i^2",
	})
	public void testRestrictedComplexExpressions(String expression) {
		assertNull(evaluate(expression));
	}

	@Test
	public void testMixedNumbers() {
		assertFalse(app.getEditorFeatures().areMixedNumbersEnabled(),
				"mixed numbers should be disabled");
	}
}
