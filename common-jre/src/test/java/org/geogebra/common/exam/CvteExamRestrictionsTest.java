package org.geogebra.common.exam;

import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.contextmenu.ContextMenuFactory;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.properties.impl.DefaultPropertiesRegistry;
import org.junit.Before;
import org.junit.Test;

public class CvteExamRestrictionsTest extends BaseUnitTest {

	@Before
	public void setupExam() {
		ExamController examController = new ExamController(
				new DefaultPropertiesRegistry(), new ContextMenuFactory());
		examController.setActiveContext(this, getKernel().getAlgebraProcessor()
						.getCommandDispatcher(), getKernel().getAlgebraProcessor(),
				getLocalization(), getSettings(), null, null);
		examController.startExam(ExamType.CVTE, null);
	}

	@Test
	public void testMatrixOutputNotIsAllowed() {
		setErrorHandler(ErrorHelper.silent());

		add("l1={1,2}");
		add("l2={1,2}");

		assertNull(add("{l1, l2}"));
		assertNull(add("{If(true, l1}}"));
		assertNull(add("{IterationList(x^2,3,2)}"));
		assertNull(add("{Sequence(k,k,1,3)}"));

		resetErrorHandler();
	}
}
