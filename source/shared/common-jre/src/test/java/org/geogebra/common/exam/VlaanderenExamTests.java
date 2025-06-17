package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class VlaanderenExamTests extends BaseExamTestSetup {
    @BeforeEach
    public void setupVlaanderenExam() {
        setupApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.VLAANDEREN, null);
    }

    @Test
    void testDerivativeOperationRestriction() {
        evaluate("f(x) = x^2");
        assertNull(evaluate("f'"));
        assertEquals("Please check your input", errorAccumulator.getErrorsSinceReset());
        errorAccumulator.resetError();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Solve(x^2 = 0)",
            "Solutions(x^2 = 0)",
            "CSolve(x^2 = 0)",
            "CSolutions(x^2 = 0)",
            "NSolve(x^2 = 0)",
            "NSolutions(x^2 = 0)",
    })
    public void testRestrictedCommands(String command) {
        assertNull(evaluate(command));
    }
}
