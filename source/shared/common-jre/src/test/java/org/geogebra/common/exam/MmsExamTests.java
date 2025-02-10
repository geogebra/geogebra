package org.geogebra.common.exam;

import static org.geogebra.common.exam.restrictions.MmsExamRestrictions.isVisibilityEnabled;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MmsExamTests extends BaseExamTests {
	@BeforeEach
	public void setupMmsExam() {
		setInitialApp(SuiteSubApp.CAS);
		examController.startExam(ExamType.MMS, null);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			// Restricted vectors
			"a = (1, 2)",
			"b = (1, 2) + 0",
	})
	public void testRestrictedVisibility(String expression) {
		assertFalse(isVisibilityEnabled(evaluateGeoElement(expression)));
	}
}
