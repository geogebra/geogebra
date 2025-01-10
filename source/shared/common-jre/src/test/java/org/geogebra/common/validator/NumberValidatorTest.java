package org.geogebra.common.validator;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NumberValidatorTest extends BaseUnitTest {

	private NumberValidator numberValidator;

	@Before
	public void setupNumberValidatorTest() {
		numberValidator = new NumberValidator(getKernel().getAlgebraProcessor());
	}

	@Test
	public void testExceptionThrowing() {
		try {
			numberValidator.getDouble("a", null);
			Assert.fail("This should have thrown a NumberFormatException");
		} catch (NumberFormatException ignored) {
		}

		try {
			numberValidator.getDouble("-1", 0.0);
			Assert.fail("This should have thrown a NumberValueOutOfBoundsException");
		} catch (NumberValueOutOfBoundsException ignored) {
		}
	}

	@Test
	public void testConversion() {
		Assert.assertEquals(
				-1.0, numberValidator.getDouble("-1", -2.0), DELTA);
	}
}
