package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Test;

public class ScheduledPreviewFromInputBarTest extends BaseUnitTest {

	private final ErrorAccumulator errorHandler = new ErrorAccumulator();

	@Test
	public void shouldValidate() {
		ScheduledPreviewFromInputBar preview = new ScheduledPreviewFromInputBar(getKernel(),
				Integer.MAX_VALUE);
		preview.updatePreviewFromInputBar("a=1", errorHandler);
		assertEquals("", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=", errorHandler);
		assertEquals("Please check your input", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=2", errorHandler);
		assertEquals("", errorHandler.getErrorsSinceReset());
		preview.updatePreviewFromInputBar("a=1/(1,1,1)", errorHandler);
		assertEquals("Illegal division \n"
				+ "1 / (1, 1, 1) ", errorHandler.getErrorsSinceReset());
	}

	@Test
	public void shouldValidateRedefinition() {
		add("a=2");
		ScheduledPreviewFromInputBar preview = new ScheduledPreviewFromInputBar(getKernel());
		preview.updatePreviewFromInputBar("a=1", errorHandler);
		assertEquals("", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=1+", errorHandler);
		assertEquals("Please check your input", errorHandler.getErrors());
		preview.updatePreviewFromInputBar("a=1/(1,1,1)", errorHandler);
		// TODO with APPS-76 we should notice the invalid syntax
		assertEquals("", errorHandler.getErrorsSinceReset());
	}
}
