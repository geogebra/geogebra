package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NiedersachsenExamTests extends BaseExamTestSetup {
	@BeforeEach
	public void setupExam() {
		setupApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.NIEDERSACHSEN, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"CSolve(x^2 = 0)",
			"CSolutions(x^2 = 0)",
	})
	public void testRestrictedCommands(String command) {
		assertNull(evaluate(command));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Solve(x^2 = 0)",
			"Solutions(x^2 = 0)",
			"NSolve(x^2 = 0)",
			"NSolutions(x^2 = 0)",
	})
	public void testUnrestrictedCommands(String command) {
		assertNotNull(evaluate(command));
	}
}
