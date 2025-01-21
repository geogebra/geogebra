package org.geogebra.common.exam;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class VlaanderenExamTests extends BaseExamTests {
    @BeforeEach
    public void setupVlaanderenExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.VLAANDEREN, null);
    }

    @Test
    void testDerivativeOperationRestriction() {
        evaluate("f(x) = x^2");
        assertNull(evaluate("f'"));
    }
}
