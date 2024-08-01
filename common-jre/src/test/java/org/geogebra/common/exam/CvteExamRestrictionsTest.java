package org.geogebra.common.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.junit.Before;
import org.junit.Test;

public class CvteExamRestrictionsTest extends BaseUnitTest {

	@Before
	public void setupExam() {
		ExamController examController = new ExamController(new DefaultPropertiesRegistry());
		examController.setActiveContext(this, getKernel().getAlgebraProcessor()
				.getCommandDispatcher(), getKernel().getAlgebraProcessor(),
				getLocalization(), getSettings(), null, null);
		examController.startExam(ExamType.CVTE, null);
	}

	@Test
	public void testDirectMatrixInputIsNotAllowed() {
		assertNull(add("{{1,2,3,4}}")); // row vector
		assertNull(add("{{1},{2},{3},{4}}")); // column vector
		assertNull(add("{{1,2},{3,4}}"));
		assertNull(add("{{5,6,7},{1,2,3}}"));
	}

	@Test
	public void testIndirectMatrixInputIsNotAllowed() {
		assertNull(add("{{1,2},{3,4}} * {{5,6},{7,8}}"));
		assertNull(add("{1,2,3} + {{4,5,6}}"));
		assertNull(add("{{1},{2},{3}} * {{4,5,6}}"));
		assertNull(add("Invert({{1,2}, {3,4}})"));

	}

	@Test
	public void testMatrixOutputNotIsAllowed() {
		final ErrorHandler originalHandler = getErrorHandler();
		setErrorHandler(ErrorHelper.silent());

		add("l1={1,2}");
		add("l2={1,2}");

		assertNull(add("{l1, l2}"));
		assertNull(add("{If(true, l1}}"));
		assertNull(add("{IterationList(x^2,3,2)}"));
		assertNull(add("{Sequence(k,k,1,3)}"));

		// Assert that the geos are deleted from the construction
		assertEquals(2, getConstruction().steps());

		setErrorHandler(originalHandler);
	}
}
