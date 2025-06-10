package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
