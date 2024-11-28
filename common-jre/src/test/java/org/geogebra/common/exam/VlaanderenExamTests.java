package org.geogebra.common.exam;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.junit.Before;
import org.junit.Test;

public final class VlaanderenExamTests extends BaseExamTests {
    @Before
    public void setupVlaanderenExam() {
        setInitialApp(SuiteSubApp.GRAPHING);
        examController.startExam(ExamType.VLAANDEREN, null);
    }

    @Test
    public void testDerivativeOperationRestriction() {
        assertNotNull(evaluate("f(x) = x^2"));
        assertNull(evaluate("f'"));
    }
}
