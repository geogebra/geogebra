package org.geogebra.common.main.error;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Test;

public class ErrorHelperTest extends BaseUnitTest {

	@Test
	public void functionShouldTakePrecedence() {
		ErrorAccumulator accumulator = new ErrorAccumulator();
		accumulator.setCurrentCommand("nPr");
		ErrorHelper.handleException(new IllegalStateException("generic"), getApp(), accumulator);
		assertThat(accumulator.getErrors(), equalTo("Please check your input"));
	}

	@Test
	public void shouldIncludeCommand() {
		ErrorAccumulator accumulator = new ErrorAccumulator();
		accumulator.setCurrentCommand("Midpoint");
		ErrorHelper.handleException(new IllegalStateException("generic"), getApp(), accumulator);
		assertThat(accumulator.getErrors(), containsString("Syntax:\nMidpoint"));
	}
}
