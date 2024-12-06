package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Before;
import org.junit.Test;

public class IbExamTests extends BaseExamTests {
	@Before
	public void setupCvteExam() {
		setInitialApp(SuiteSubApp.GRAPHING);
		examController.startExam(ExamType.IB, null);
	}

	@Test
	public void testPointDerivativeFiltering() {
		evaluate("f(x) = x^2");
		evaluate("p = 2");

		assertNotNull(evaluate("f'(1)"));
		assertNotNull(evaluate("f'(p)"));

		assertNull(evaluate("f'"));
		assertNull(evaluate("f'(x)"));
		assertNull(evaluate("g = f'"));
		assertNull(evaluate("g(x) = f'"));
		assertNull(evaluate("g(x) = f'(x)"));
	}

	@Test
	public void testRestrictedOperationsAreFreeFromSideEffect() {
		assertNotNull(evaluate("f(x) = x^3"));
		assertNotNull(evaluate("l1 = {x}"));
		assertNull(evaluate("SetValue(l1, 1, f')"));
		assertEquals(app.getKernel().getConstruction().lookupLabel("l1")
				.toString(StringTemplate.defaultTemplate), "l1 = {x}");
	}
}
